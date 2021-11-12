package com.contact.unmatch;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.contact.unmatch.authentication.AuthenticationActivity;
import com.contact.unmatch.home.HomeActivity;
import com.contact.unmatch.model.User;
import com.contact.unmatch.utils.Utils;

public class SplashActivity extends AppCompatActivity {

    private long showStartTime;
    private final static long DELAY_TIME = 2000;
    private boolean isRunning;
    private ImageView logoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logoImageView = findViewById(R.id.image_logo);
        startAnimation();
        if (Utils.isNetworkAvailable(this)) {
            // Start Splash
            startSplash();
        } else {
            Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
        }
    }

    private void startSplash() {

        showStartTime = System.currentTimeMillis();
        isRunning = true;

        Thread background = new Thread() {
            public void run() {
                try {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - showStartTime < DELAY_TIME) {
                        try {
                            Thread.sleep(showStartTime + DELAY_TIME - currentTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            doFinish();
                        }
                    });
                }
            }
        };

        background.start();
    }

    private void doFinish() {
        if (this.isRunning) {
            SharedPreferences sharedPreferences = Utils.getPreferences();
            boolean isFirstLaunch = sharedPreferences.getBoolean("FIRST_LAUNCH", true);
            if (isFirstLaunch) {
                startActivity(new Intent(this, TutorialActivity.class));
            } else {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                if (User.getUser() == null) {
                    intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
                }
                startActivity(intent);
            }
            //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (this.isRunning) {
            this.isRunning = false;
        }
        finish();
    }

    private void startAnimation() {
        logoImageView.getViewTreeObserver()
            .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    logoImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    AnimatorSet mAnimatorSet = new AnimatorSet();
                    mAnimatorSet.playTogether(ObjectAnimator.ofFloat(logoImageView, "alpha", 0, 1, 1, 1),
                            ObjectAnimator.ofFloat(logoImageView, "scaleX", 0.3f, 1.05f, 0.9f, 1),
                            ObjectAnimator.ofFloat(logoImageView, "scaleY", 0.3f, 1.05f, 0.9f, 1));
                    mAnimatorSet.setDuration(2000);
                    mAnimatorSet.start();
                }
            });
    }
}
