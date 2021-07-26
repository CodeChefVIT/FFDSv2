package com.codechef.ffds

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.codechef.ffds.databinding.ActivityTimeTableBinding
import java.io.ByteArrayOutputStream
import java.io.Serializable
import java.util.*


class TimeTable : AppCompatActivity() {

    private var tableMap = ArrayList<ArrayList<HashMap<String, Any>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTimeTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tableMap = Slots().getSlots()
        Log.d("SLOTS", tableMap.toString())

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == Activity.RESULT_OK) {
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
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    encodeToString(stream.toByteArray(), DEFAULT)
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