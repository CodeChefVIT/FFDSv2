package com.codechef.ffds

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.codechef.ffds.databinding.LoginActivityBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

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

        val dialog = Dialog(this)
        dialog.setContentView(layoutInflater.inflate(R.layout.loading_dialog, null))
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val fields = mutableMapOf("email" to email, "password" to password)

        Api.retrofitService.login(fields)!!.enqueue(object : Callback<Token?> {
            override fun onFailure(call: Call<Token?>, t: Throwable) {
                dialog.dismiss()
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Token?>, response: Response<Token?>) {
                val token = response.body()?.token
                if (response.message() == "OK") {
                    if (token != null) {
                        viewModel.insertUser(Profile(token = token, email = email))
                        updateProfile(token)
                    }
                }
                else {
                    val gson = Gson()
                    val (_,message) = gson.fromJson(response.errorBody()?.charStream(), Error::class.java)
                    Toast.makeText(
                        applicationContext,
                        "Error ${response.code()}: $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
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
                        val user: Profile = response.body()!!
                        val image = user.userImage
                        if (image.url.isNotEmpty()) {
                            val bitmap = Glide.with(this@LoginActivity).asBitmap().load(Uri.parse(image.url)).submit().get()
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            user.copy(userArray = stream.toByteArray().toList())
                        }
                        viewModel.updateUser(user.copy(token = token))
                        val editor = getSharedPreferences("MY PREFS", MODE_PRIVATE).edit()
                        editor.putString("id", user._id).apply()
                        startActivity(Intent(baseContext, MainActivity::class.java))
                        finish()
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
}
