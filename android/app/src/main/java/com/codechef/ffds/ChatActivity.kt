package com.codechef.ffds

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codechef.ffds.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val name = bundle?.getString("Name")

        binding.apply {
            nameText.text = name

            back.setOnClickListener {
                onBackPressed()
            }
        }
    }
}