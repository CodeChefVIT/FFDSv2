package com.codechef.ffds

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.codechef.ffds.databinding.Register2ActivityBinding
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity2 : AppCompatActivity() {

    private var gender: String? = "Male"
    private lateinit var binding: Register2ActivityBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Register2ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            UserViewModelFactory(application)
        ).get(UserViewModel::class.java)
        binding.apply {
            submitBtn.setOnClickListener {
                prompt.visibility = View.GONE
                val phone = phoneNumber.text.toString().trim()
                val name = fullName.text.toString().trim()
                if (phone.length != 10) {
                    prompt.text = "* Enter a valid phone number"
                    prompt.visibility = View.VISIBLE
                } else if (phone.isEmpty() || name.isEmpty()) {
                    prompt.text = "* Fields can't be empty"
                    prompt.visibility = View.VISIBLE
                } else {
                    val prefs = getSharedPreferences("MY PREFS", MODE_PRIVATE)
                    viewModel.getUserData(prefs.getString("id", "")!!).observe(this@RegisterActivity2) { user ->
                        updateUser(
                            user.copy(
                                name = name,
                                phone = phone,
                                gender = gender!!,
                                verified = true
                            )
                        )
                    }
                }
            }
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            gender = when (view.getId()) {
                R.id.male -> "Male"
                R.id.female -> "Female"
                R.id.others -> "Others"
                else -> "Rather not say"
            }
        }
    }

    private fun updateUser(user: Profile) {

        val dialog = Dialog(this)
        dialog.setContentView(layoutInflater.inflate(R.layout.loading_dialog, null))
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val om = ObjectMapper()
        val fields = om.writeValueAsString(user)
        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), fields)
        Api.retrofitService.update(user.token, body)
            ?.enqueue(object : retrofit2.Callback<ResponseBody?> {
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
                        updateProfile(user.token)
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
                        viewModel.updateUser(user)
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

    override fun onBackPressed() {
        startActivity(Intent(this, RegisterActivity1::class.java))
    }
}
