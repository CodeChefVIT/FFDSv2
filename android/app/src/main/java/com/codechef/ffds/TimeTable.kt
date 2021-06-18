package com.codechef.ffds

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.codechef.ffds.databinding.ActivityTimeTableBinding
import java.util.*


class TimeTable : AppCompatActivity() {

    companion object {
        private val tableMap = Slots().getSlots()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTimeTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val imageUri = data?.data
                }

            }

        binding.apply {
            uploadTimeTable.setOnClickListener {
                val gallery = Intent()
                gallery.type = "image/*"
                gallery.action = Intent.ACTION_GET_CONTENT

                resultLauncher.launch(Intent.createChooser(gallery, "Upload Time Table"))
            }

            saveTimeTable.setOnClickListener {

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
                    item1.text = tableMap[i][j].keys.elementAt(0)
                    if (item1.text != "LUNCH") {
                        item1.setOnClickListener {
                            tableMap[i][j][item1.text.toString()] = !tableMap[i][j][item1.text]!!
                            if (tableMap[i][j][item1.text] == true)
                                itemView1.setBackgroundResource(R.drawable.free_slot_background)
                            else
                                itemView1.setBackgroundResource(R.drawable.occupied_slot_background)
                        }
                        if (tableMap[i][j][item1.text] == true)
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