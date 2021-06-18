package com.codechef.ffds

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.codechef.ffds.databinding.LoginActivityBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            UserViewModelFactory(application)
        ).get(UserViewModel::class.java)

        binding.apply {
            createNew.setOnClickListener {
                startActivity(
                    Intent(
                        baseContext,
                        RegisterActivity1::class.java
                    )
                )
            }

            loginBtn.setOnClickListener {
                val email = emailInput.text.toString().trim()
                val password = passInput.text.toString().trim()
                if (email.isEmpty() || password.isEmpty()) {
                    prompt.text = "* Fields can't be empty"
                    prompt.visibility = View.VISIBLE
                } else if (!email.contains("vitstudent.ac.in")) {
                    prompt.text = "* Enter a valid VIT email"
                    prompt.visibility = View.VISIBLE
                } else
                    loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {

        val fields = mutableMapOf("email" to email, "password" to password)

        Api.retrofitService.login(fields)!!.enqueue(object : Callback<Token?> {
            override fun onFailure(call: Call<Token?>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Token?>, response: Response<Token?>) {
                Toast.makeText(baseContext, response.message(), Toast.LENGTH_SHORT).show()
                val token = response.body()?.token
                if (response.message() == "OK") {
                    if (token != null) {
                        viewModel.insert(Profile(token = token))
                        updateProfile(token)
                    }
                }
                else
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT)
                        .show()
            }
        })
    }

    fun updateProfile(token: String) {
        Api.retrofitService.profileView(token)
            ?.enqueue(object : Callback<Profile?> {
                override fun onFailure(call: Call<Profile?>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }
                override fun onResponse(
                    call: Call<Profile?>,
                    response: Response<Profile?>
                ) {
                    if (response.message() == "OK") {
                        var user: Profile = response.body()!!
                        viewModel.update(user.copy(token = token))
                        startActivity(Intent(baseContext, MainActivity::class.java))
                        finish()
                    } else
                        Toast.makeText(applicationContext, response.message(), Toast.LENGTH_LONG)
                            .show()
                }
            })
    }
}
