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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.codechef.ffds.databinding.ActivityTimeTableBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class TimeTable : AppCompatActivity() {

    private var tableMap = ArrayList<ArrayList<HashMap<String, Any>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTimeTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tableMap = Slots().getSlots()

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == Activity.RESULT_OK) {

                    val dialog = Dialog(this)
                    val view = layoutInflater.inflate(R.layout.loading_dialog, null)
                    view.findViewById<TextView>(R.id.text).text = "Uploading..."
                    dialog.setContentView(view)
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
                            .asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    )

                    Api.retrofitServiceForSlots.getFreeSlots(filePart)
                        ?.enqueue(object : retrofit2.Callback<ResponseBody?> {
                            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                                dialog.dismiss()
                                Toast.makeText(
                                    applicationContext,
                                    "FAILED: ${t.message}",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }

                            override fun onResponse(
                                call: Call<ResponseBody?>,
                                response: Response<ResponseBody?>
                            ) {
                                dialog.dismiss()
                                if (response.message() == "OK") {
                                    Log.d("myTag", response.body()?.string()!!)
                                } else
                                    Toast.makeText(
                                        applicationContext,
                                        "ERROR: ${response.message()}",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
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
                args.putSerializable("tableMap", tableMap as Serializable)
                intent.putExtra("bundle", args)
                setResult(RESULT_OK, intent)
                finish()
            }

            for (i in 0..6) {
                val tableRow = TableRow(this@TimeTable)
                val itemView = layoutInflater.inflate(R.layout.item_time_table, null)
                val item = itemView.findViewById<TextView>(R.id.text)
                item.text =
                    if (i == 0) "MON" else if (i == 1) "TUE" else if (i == 2) "WED" else if (i == 3) "THUR" else if (i == 4) "FRI" else if (i == 5) "SAT" else "SUN"
                item.setTextColor(ContextCompat.getColor(this@TimeTable, android.R.color.white))
                tableRow.addView(itemView)
                for (j in 0..13) {
                    val itemView1 = layoutInflater.inflate(R.layout.item_time_table, null)
                    val item1 = itemView1.findViewById<TextView>(R.id.text)
                    item1.text = tableMap[i][j]["name"].toString()
                    if (item1.text != "LUNCH") {
                        item1.setOnClickListener {
                            val free = tableMap[i][j]["free"] as Boolean
                            tableMap[i][j]["free"] = !free
                            if (tableMap[i][j]["free"] == true)
                                itemView1.setBackgroundResource(R.drawable.free_slot_background)
                            else
                                itemView1.setBackgroundResource(R.drawable.occupied_slot_background)
                        }
                        if (tableMap[i][j]["free"] == true)
                            itemView1.setBackgroundResource(R.drawable.free_slot_background)
                        else
                            itemView1.setBackgroundResource(R.drawable.occupied_slot_background)
                    } else
                        item1.setTextColor(
                            ContextCompat.getColor(
                                this@TimeTable,
                                android.R.color.white
                            )
                        )
                    tableRow.addView(itemView1)
                }
                tableLayout.addView(tableRow)
            }

        }
    }
}