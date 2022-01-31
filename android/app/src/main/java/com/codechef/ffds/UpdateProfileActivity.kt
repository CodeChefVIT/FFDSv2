package com.codechef.ffds

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.codechef.ffds.databinding.UpdateProfileActivityBinding
import com.cunoraz.tagview.Tag
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: UpdateProfileActivityBinding
    lateinit var viewModel: UserViewModel
    var user = Profile()
    private val tags = ArrayList<String>()
    private var imageArray = byteArrayOf()
    private var filePart: MultipartBody.Part? = null

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
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    imageArray = stream.toByteArray()

                    val file =
                        File(getExternalFilesDir(null)?.absolutePath + File.separator + "profile.png")
                    file.createNewFile()

                    val fos = FileOutputStream(file)
                    fos.write(imageArray)
                    fos.flush()
                    fos.close()

                    filePart = MultipartBody.Part.createFormData(
                        "image", file.name, file.asRequestBody("image/png".toMediaTypeOrNull())
                    )
                }

            }

        val resultLauncher2 =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val args = data?.getBundleExtra("bundle")
                    user =
                        user.copy(slot = args?.getSerializable("tableMap") as java.util.ArrayList<java.util.ArrayList<HashMap<String, Any>>>)
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
                val intent = Intent(this@UpdateProfileActivity, TimeTableActivity::class.java)
                resultLauncher2.launch(intent)
            }

            delete.setOnClickListener {
                imageArray = byteArrayOf()
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.profile_image)
                dp.setImageBitmap(bitmap)
            }

            saveProfile.setOnClickListener {
                updateUser(
                    user.copy(
                        bio = bio.text.toString().trim(),
                        name = yourName.text.toString().trim(),
                        phone = phoneNoEdit.text.toString(),
                        expectations = tags,
                    )
                )
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun updateUser(user: Profile) {
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.loading_dialog, null)
        view.findViewById<TextView>(R.id.text).text = getString(R.string.saving)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val om = ObjectMapper()
        val fields = om.writeValueAsString(user)
        val body = fields.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        Api.retrofitService.update(user.token, body)
            ?.enqueue(object : Callback<ResponseBody?> {
                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    dialog.dismiss()
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.message() == "OK") {
                        if (filePart != null)
                            uploadImage(user, dialog)
                        else {
                            dialog.dismiss()
                            viewModel.updateUser(user)
                            startActivity(Intent(baseContext, MainActivity::class.java))
                        }
                    } else {
                        dialog.dismiss()
                        val gson = Gson()
                        val (_,message) = gson.fromJson(response.errorBody()?.charStream(), Error::class.java)
                        Toast.makeText(
                            applicationContext,
                            "Error ${response.code()}: $message",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

    private fun uploadImage(user: Profile, dialog: Dialog) {
        Api.retrofitService.uploadImage(user.token, filePart!!)?.enqueue(object : Callback<Image?> {
            override fun onFailure(call: Call<Image?>, t: Throwable) {
                Toast.makeText(applicationContext, "Failed: " + t.message, Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onResponse(call: Call<Image?>, response: Response<Image?>) {
                dialog.dismiss()
                if (response.message() == "OK") {
                    val image = response.body()
                    if (image != null) {
                        val profile = user.copy(userArray = imageArray.toList(), userImage = image)
                        viewModel.updateUser(profile)
                    }
                    startActivity(Intent(baseContext, MainActivity::class.java))
                } else {
                    val gson = Gson()
                    val (_,message) = gson.fromJson(response.errorBody()?.charStream(), Error::class.java)
                    Toast.makeText(
                        applicationContext,
                        "Error ${response.code()}: $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        })
    }

    private fun setDefaultData() {

        binding.apply {
            val prefs = getSharedPreferences("MY PREFS", MODE_PRIVATE)
            viewModel.getUserData(prefs.getString("id", "")!!)
                .observe(this@UpdateProfileActivity) { user ->
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
                    val bitmap = if (user.userArray.isNotEmpty())
                        BitmapFactory.decodeByteArray(user.userArray.toByteArray(), 0, user.userArray.size)
                    else
                        BitmapFactory.decodeResource(resources, R.drawable.profile_image)
                    dp.setImageBitmap(bitmap)
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

    private fun getNewTag(text: String): Tag {
        val tag = Tag(text)
        tag.isDeletable = true
        tag.layoutColor =
            ContextCompat.getColor(this, R.color.colorPrimary)

        return tag
    }
}
