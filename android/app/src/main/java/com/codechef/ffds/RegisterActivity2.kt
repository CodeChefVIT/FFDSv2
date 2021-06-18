package com.codechef.ffds

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.codechef.ffds.databinding.Register2ActivityBinding
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
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
                    viewModel.getUserData().observe(this@RegisterActivity2) { user ->
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
        val om = ObjectMapper()
        val fields = om.writeValueAsString(user)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), fields)
        Api.retrofitService.update(user.token, body)
            ?.enqueue(object : retrofit2.Callback<ResponseBody?> {
                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.d("tag3", t.toString())
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    Log.d("tag2", response.toString())
                    if (response.message() == "OK") {
                        viewModel.update(user)
                        startActivity(Intent(baseContext, MainActivity::class.java))
                        finish()
                    } else
                        Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT)
                            .show()
                }
            })

    }

    override fun onBackPressed() {
        startActivity(Intent(this, RegisterActivity1::class.java))
    }
}
