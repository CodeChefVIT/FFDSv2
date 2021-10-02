package com.codechef.ffds

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.codechef.ffds.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            var selectedFragment: Fragment? = null

            when (menuItem.itemId) {
                R.id.profile -> selectedFragment = ProfileFragment()
                R.id.matches -> selectedFragment = MatchFragment()
                R.id.dms -> selectedFragment = MessagesFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, selectedFragment!!).commit()
            return@setOnItemSelectedListener true
        }

        supportFragmentManager.beginTransaction().replace(R.id.container, ProfileFragment())
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
