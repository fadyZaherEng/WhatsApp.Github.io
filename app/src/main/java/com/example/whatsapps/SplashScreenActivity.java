package com.example.whatsapps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashScreenActivity extends AppCompatActivity {

    CircleImageView SplashScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        SplashScreen=findViewById(R.id.imgSplashScreen);

        Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.anim);
        SplashScreen.setAnimation(animation);

        android.os.Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);

    }
}