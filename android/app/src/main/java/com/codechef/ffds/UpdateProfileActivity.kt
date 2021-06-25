package com.codechef.ffds

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.codechef.ffds.databinding.UpdateProfileActivityBinding
import com.cunoraz.tagview.Tag
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: UpdateProfileActivityBinding
    lateinit var viewModel: UserViewModel
    var user = Profile()
    private val tags = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UpdateProfileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            UserViewModelFactory(application)
        ).get(UserViewModel::class.java)
        setDefaultData()

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val imageURI: Uri = data?.data!!
                    val bitmap = if (android.os.Build.VERSION.SDK_INT >= 29)
                        ImageDecoder.decodeBitmap(
                            ImageDecoder.createSource(
                                contentResolver,
                                imageURI
                            )
                        )
                    else
                        MediaStore.Images.Media.getBitmap(contentResolver, imageURI)
                    binding.dp.setImageBitmap(bitmap)
                    val path = saveToInternalStorage(bitmap)
                    viewModel.update(user.copy(imagePath = path!!))
                }

            }

        val resultLauncher2 =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val args = data?.getBundleExtra("bundle")
                    user =
                        user.copy(slot = args?.getSerializable("tableMap") as java.util.ArrayList<java.util.ArrayList<HashMap<String, Boolean>>>)
                }

            }

        binding.apply {
            uploadDp.setOnClickListener {
                val gallery = Intent()
                gallery.type = "image/*"
                gallery.action = Intent.ACTION_GET_CONTENT

                resultLauncher.launch(Intent.createChooser(gallery, "Select profile photo"))
            }

            add.setOnClickListener {
                handleTags(tags)
            }

            tagView2.setOnTagDeleteListener { _, tag, position ->
                tags.remove(tag.text)
                tagView2.remove(position)
            }

            uploadTimeTable.setOnClickListener {
                val intent = Intent(this@UpdateProfileActivity, TimeTable::class.java)
                val bundle = Bundle()
                val tableMap = if (user.slot.isEmpty()) Slots().getSlots() else user.slot
                bundle.putSerializable("tableMap", tableMap as Serializable)
                intent.putExtra("bundle", bundle)
                resultLauncher2.launch(intent)
            }

            saveProfile.setOnClickListener {
                updateUser(
                    user.copy(
                        bio = bio.text.toString().trim(),
                        name = yourName.text.toString().trim(),
                        phone = phoneNoEdit.text.toString(),
                        expectations = tags
                    )
                )
            }
        }
    }

    private fun updateUser(user: Profile) {
        val dialog = Dialog(this)
        dialog.setContentView(layoutInflater.inflate(R.layout.loading_dialog, null))
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val om = ObjectMapper()
        val fields = om.writeValueAsString(user)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), fields)
        Log.d("myTag", fields)
        Api.retrofitService.update(user.token, body)
            ?.enqueue(object : retrofit2.Callback<ResponseBody?> {
                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    dialog.dismiss()
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    dialog.dismiss()
                    if (response.message() == "OK") {
                        viewModel.update(user)
                        startActivity(Intent(baseContext, MainActivity::class.java))
                    } else
                        Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT)
                            .show()
                }
            })

    }

    private fun setDefaultData() {

        binding.apply {
            viewModel.getUserData().observe(this@UpdateProfileActivity) { user ->
                this@UpdateProfileActivity.user = user
                for (tag in user.expectations)
                    tags.add(tag)
                bio.setText(user.bio)
                yourName.setText(user.name)
                phoneNoEdit.text = user.phone
                tagView2.setTagMargin(10f)
                tagView2.setTextPaddingTop(2f)
                tagView2.settextPaddingBottom(2f)
                for (tag in tags) {
                    tagView2.addTag(getNewTag(tag))
                }
                try {
                    dp.setImageBitmap(loadImageFromStorage(user.imagePath))
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun handleTags(tags: ArrayList<String>) {
        binding.apply {
            val tag = addTags.text.toString().trim()
            if (tag.isNotEmpty()) {
                if (!tags.contains(tag)) {
                    tagView2.addTag(getNewTag(tag))
                    tags.add(tag)
                } else
                    Toast.makeText(
                        this@UpdateProfileActivity,
                        "Tag already present",
                        Toast.LENGTH_SHORT
                    ).show()
            }
            addTags.text = null
        }
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap): String? {
        val cw = ContextWrapper(applicationContext)
        val directory: File = cw.getDir("FFDS", Context.MODE_PRIVATE)
        val myPath = File(directory, "profileImage.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(myPath)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }

    private fun getNewTag(text: String): Tag {
        val tag = Tag(text)
        tag.isDeletable = true
        tag.layoutColor =
            ContextCompat.getColor(this, R.color.colorPrimary)

        return tag
    }

    @Throws(FileNotFoundException::class)
    private fun loadImageFromStorage(path: String): Bitmap? {
        val f = File(path, "profileImage.jpg")
        return BitmapFactory.decodeStream(FileInputStream(f))
    }
}
