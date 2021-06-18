package com.codechef.ffds

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.codechef.ffds.databinding.Register1ActivityBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity1 : AppCompatActivity() {

    lateinit var binding: Register1ActivityBinding
    lateinit var viewModel: UserViewModel
    var user = Profile()

    override fun onStart() {
        super.onStart()

        if (this.getDatabasePath("user_database").exists()) {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Register1ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            UserViewModelFactory(application)
        ).get(UserViewModel::class.java)

        binding.apply {

            registerBtn.setOnClickListener {
                if (registerBtn.text == "SUBMIT") {
                    val email = emailInput.text.toString()
                    val pass = passInput.text.toString()
                    val confirm = confirmPass.text.toString()

                    if (!email.endsWith("vitstudent.ac.in")) {
                        prompt.text = "* Enter a valid VIT email"
                        prompt.visibility = View.VISIBLE
                    } else if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                        prompt.visibility = View.VISIBLE
                        prompt.text = "* Fields can't be empty"
                    } else if (pass != confirm) {
                        prompt.visibility = View.VISIBLE
                        prompt.text = "* Passwords don't match"
                    } else {
                        register(email, pass)
                    }
                } else {
                    login(emailInput.text.toString(), passInput.text.toString())
                }
            }
            emailInput.addTextChangedListener {
                registerBtn.text = "SUBMIT"
            }

            emailInput.addTextChangedListener {
                registerBtn.text = "SUBMIT"
            }
            emailInput.addTextChangedListener {
                registerBtn.text = "SUBMIT"
            }

            loginBtn.setOnClickListener {
                startActivity(
                    Intent(
                        baseContext,
                        LoginActivity::class.java
                    )
                )
            }

        }
    }

    private fun register(email: String, password: String) {
        val fields = mutableMapOf(
            "email" to email,
            "password" to password,
        )

        Api.retrofitService.register(fields)?.enqueue(object : Callback<ResponseBody?> {
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response.message() == "OK") {
                    Toast.makeText(
                        applicationContext,
                        "Email Sent to $email",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    saveUser(email)
                    binding.registerBtn.text = "CONTINUE"
                } else
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT)
                        .show()
            }
        })
    }

    private fun login(email: String, password: String) {
        val fields = mutableMapOf(
            "email" to email,
            "password" to password,
        )
        Api.retrofitService.login(fields)?.enqueue(object : Callback<Token?> {
            override fun onFailure(call: Call<Token?>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Token?>, response: Response<Token?>) {
                val token = response.body()?.token
                if (response.message() == "OK") {
                    if (token != null) {
                        viewModel.update(user.copy(token = token))
                        startActivity(Intent(this@RegisterActivity1, RegisterActivity2::class.java))
                        finish()
                    }
                } else
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT)
                        .show()
            }

        })
    }

    private fun saveUser(email: String) {
        user = Profile(email = email)
        viewModel.insert(user)
    }
}
