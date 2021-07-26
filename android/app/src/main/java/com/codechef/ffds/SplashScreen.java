package com.codechef.ffds;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {

    boolean tokenPresent = false;
    boolean namePresent = false;

    @Override
    protected void onStart() {
        super.onStart();
        UserViewModel viewModel = new ViewModelProvider(this, new UserViewModelFactory(getApplication())).get(UserViewModel.class);
        viewModel.getUserData().observe(this, user -> {
            if (user!= null) {
                if (!user.getToken().isEmpty())
                    tokenPresent = true;
                if(!user.getName().isEmpty())
                    namePresent = true;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        LottieAnimationView animation = findViewById(R.id.lottie_animation);
        ImageView icon = findViewById(R.id.icon);
        TextView text = findViewById(R.id.app_name);
        animation.setMaxFrame(170);
        animation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animation.setVisibility(View.GONE);
                icon.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);

                new Handler(getMainLooper()).postDelayed(() -> {
                    if (tokenPresent && namePresent)
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    else if(tokenPresent)
                        startActivity(new Intent(SplashScreen.this, RegisterActivity2.class));
                    else
                        startActivity(new Intent(SplashScreen.this, RegisterActivity1.class));
                    finishAffinity();
                }, 500);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }
}