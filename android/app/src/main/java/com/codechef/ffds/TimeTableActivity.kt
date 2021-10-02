package com.codechef.ffds

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TableRow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.codechef.ffds.databinding.ActivityTimeTableBinding
import com.codechef.ffds.databinding.ItemTimeTableBinding
import com.codechef.ffds.databinding.LoadingDialogBinding
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*

class TimeTableActivity : AppCompatActivity() {

    private var slots = Slots()
    private lateinit var binding: ActivityTimeTableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        slots.createSlots()

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == Activity.RESULT_OK) {

                    val dialog = Dialog(this)
                    val dialogBinding = LoadingDialogBinding.inflate(layoutInflater)
                    dialogBinding.text.text = getString(R.string.uploading)
                    dialog.setContentView(dialogBinding.root)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.show()

                    val data = result.data
                    val imageURI = data?.data

                    val bitmap = if (android.os.Build.VERSION.SDK_INT >= 29)
                        ImageDecoder.decodeBitmap(
                            ImageDecoder.createSource(
                                contentResolver,
                                imageURI!!
                            )
                        )
                    else
                        MediaStore.Images.Media.getBitmap(contentResolver, imageURI)

                    val file =
                        File(getExternalFilesDir(null)?.absolutePath + File.separator + "timeTable.png")
                    file.createNewFile()

                    val bos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
                    val bitmapData = bos.toByteArray()

                    val fos = FileOutputStream(file)
                    fos.write(bitmapData)
                    fos.flush()
                    fos.close()

                    val filePart = MultipartBody.Part.createFormData(
                        "file", file.absolutePath, file
                            .asRequestBody("image/png".toMediaTypeOrNull())
                    )

                    Api.retrofitServiceForSlots.getFreeSlots(filePart)
                        ?.enqueue(object : Callback<SlotMapper?> {
                            override fun onFailure(call: Call<SlotMapper?>, t: Throwable) {
                                dialog.dismiss()
                                Toast.makeText(
                                    applicationContext,
                                    "FAILED: ${t.message}",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }

                            override fun onResponse(
                                call: Call<SlotMapper?>,
                                response: Response<SlotMapper?>
                            ) {
                                if (response.message() == "OK") {
                                    Log.d("myTag", response.body().toString())
                                    Api.retrofitService.slotMapper(response.body()?.slots!!)
                                        ?.enqueue(object : Callback<Slots> {
                                            override fun onFailure(
                                                call: Call<Slots>,
                                                t: Throwable
                                            ) {
                                                dialog.dismiss()
                                                Toast.makeText(applicationContext, "Failed: " + t.message, Toast.LENGTH_SHORT)
                                                    .show()
                                            }

                                            override fun onResponse(
                                                call: Call<Slots>,
                                                response: Response<Slots>
                                            ) {
                                                if(response.message() == "OK") {
                                                    if(response.body() != null) {
                                                        slots = response.body()!!
                                                        updateTimeTable()
                                                        dialog.dismiss()
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

                                } else {
                                    dialog.dismiss()
                                    Toast.makeText(
                                        applicationContext,
                                        "ERROR: ${response.message()}",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        })
                }

            }

        binding.apply {
            uploadTimeTable.setOnClickListener {
                val gallery = Intent()
                gallery.type = "image/*"
                gallery.action = Intent.ACTION_GET_CONTENT

                resultLauncher.launch(Intent.createChooser(gallery, "Upload Time Table"))
            }

            ok.setOnClickListener {
                val intent = Intent()
                val args = Bundle()
                args.putSerializable("tableMap", slots.slot as Serializable)
                intent.putExtra("bundle", args)
                setResult(RESULT_OK, intent)
                finish()
            }

            updateTimeTable()
        }
    }

    fun updateTimeTable() {
        binding.apply {
            tableLayout.removeAllViews()
            for (i in 0..6) {
                val tableRow = TableRow(this@TimeTableActivity)
                val itemBinding = ItemTimeTableBinding.inflate(layoutInflater)
                itemBinding.text.text =
                    if (i == 0) "MON" else if (i == 1) "TUE" else if (i == 2) "WED" else if (i == 3) "THUR" else if (i == 4) "FRI" else if (i == 5) "SAT" else "SUN"
                itemBinding.text.setTextColor(
                    ContextCompat.getColor(
                        this@TimeTableActivity,
                        android.R.color.white
                    )
                )
                tableRow.addView(itemBinding.root)
                for (j in 0..13) {
                    val itemBinding1 = ItemTimeTableBinding.inflate(layoutInflater)
                    itemBinding1.apply {
                        text.text = slots.slot[i][j]["name"].toString()
                        if (text.text != "LUNCH") {
                            text.setOnClickListener {
                                val free = slots.slot[i][j]["free"] as Boolean
                                slots.slot[i][j]["free"] = !free
                                if (slots.slot[i][j]["free"] == true)
                                    root.setBackgroundResource(R.drawable.free_slot_background)
                                else
                                    root.setBackgroundResource(R.drawable.occupied_slot_background)
                            }
                            if (slots.slot[i][j]["free"] == true)
                                root.setBackgroundResource(R.drawable.free_slot_background)
                            else
                                root.setBackgroundResource(R.drawable.occupied_slot_background)
                        } else
                            text.setTextColor(
                                ContextCompat.getColor(
                                    this@TimeTableActivity,
                                    android.R.color.white
                                )
                            )
                        tableRow.addView(itemBinding1.root)
                    }
                }
                tableLayout.addView(tableRow)
            }
        }
    }
}