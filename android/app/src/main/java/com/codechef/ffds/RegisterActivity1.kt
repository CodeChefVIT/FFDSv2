package com.codechef.ffds

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.codechef.ffds.databinding.Register1ActivityBinding
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URISyntaxException
import java.util.*
import android.content.SharedPreferences
import com.google.gson.Gson


class RegisterActivity1 : AppCompatActivity() {

    lateinit var binding: Register1ActivityBinding
    lateinit var viewModel: UserViewModel
    var user = Profile()
    private lateinit var mSocket: Socket
    private val verified = "verified"
    private val looking = "looking"

    init {
        try {
            mSocket = IO.socket("https://ffds-backend.herokuapp.com/")
        } catch (e: URISyntaxException) {
        }
    }

    private val onNewMessage =
        Emitter.Listener { args ->
            runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                Log.d("myTag", data.toString())
                val verified: Boolean
                try {
                    verified = data.getBoolean(this.verified)
                } catch (e: JSONException) {
                    Log.d("myTag2", e.message!!)
                    return@Runnable
                }

                if (verified)
                    binding.apply { login(emailInput.text.toString(), passInput.text.toString()) }
            })
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Register1ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mSocket.connect()
        mSocket.on(verified, onNewMessage)
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

        val dialog = Dialog(this)
        dialog.setContentView(layoutInflater.inflate(R.layout.loading_dialog, null))
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val fields = mutableMapOf(
            "email" to email,
            "password" to password,
        )

        Api.retrofitService.register(fields)?.enqueue(object : Callback<ResponseBody?> {
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                dialog.dismiss()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                dialog.dismiss()
                if (response.message() == "OK") {
                    Toast.makeText(
                        applicationContext,
                        "Email Sent to $email",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    mSocket.emit(looking, email)
                    saveUser(email)
                    binding.registerBtn.text = "CONTINUE"
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
                        viewModel.updateUser(user.copy(token = token))
                        startActivity(Intent(this@RegisterActivity1, RegisterActivity2::class.java))
                        finish()
                    }
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

    private fun saveUser(email: String) {
        user = Profile(email = email)
        viewModel.insertUser(user)
    }
}
