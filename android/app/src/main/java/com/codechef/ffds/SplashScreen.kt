package com.codechef.ffds

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.codechef.ffds.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private var tokenPresent = false
    private var namePresent = false

    override fun onStart() {
        super.onStart()
        val viewModel = ViewModelProvider(this, UserViewModelFactory(application)).get(
            UserViewModel::class.java
        )
        val prefs = getSharedPreferences("MY PREFS", MODE_PRIVATE)
        viewModel.getUserData(prefs.getString("id", "")!!).observe(this, { user: Profile? ->
            if (user != null) {
                if (user.token.isNotEmpty()) tokenPresent = true
                if (user.name.isNotEmpty()) namePresent = true
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            lottieAnimation.setMaxFrame(170)
            lottieAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    lottieAnimation.visibility = View.GONE
                    icon.visibility = View.VISIBLE
                    appName.visibility = View.VISIBLE
                    Handler(mainLooper).postDelayed({
                        if (tokenPresent && namePresent) startActivity(
                            Intent(
                                this@SplashScreen,
                                MainActivity::class.java
                            )
                        ) else if (tokenPresent) startActivity(
                            Intent(
                                this@SplashScreen,
                                RegisterActivity2::class.java
                            )
                        ) else startActivity(
                            Intent(
                                this@SplashScreen,
                                RegisterActivity1::class.java
                            )
                        )
                        finish()
                    }, 500)
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
        }
    }
}