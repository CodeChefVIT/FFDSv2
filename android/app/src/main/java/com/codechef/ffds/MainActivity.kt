package com.codechef.ffds

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.codechef.ffds.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val matches: ArrayList<Profile>? = null
    lateinit var viewModel: UserViewModel

    companion object {
        var user = Profile()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            UserViewModelFactory(application)
        ).get(UserViewModel::class.java)

        viewModel.getUserData().observe(this) {
            user = it
        }
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

        /*val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://ffds-new.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val viewModel = ViewModelProvider(this, UserViewModelFactory(application)).get(UserViewModel::class.java)
        val user = viewModel.getUserData()

        val apiHolder=retrofit.create(ApiHolder::class.java)
        val call=apiHolder.showFeed(user.token,
            user.gender, "20")

        call.enqueue(object: Callback<Feed>{
            override fun onFailure(call: Call<Feed>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Feed>, response: Response<Feed>) {
                val profiles=response.body()?.payload
                for (profile in profiles!!){
                    matches?.add(profile)
                }
            }
        })*/

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
