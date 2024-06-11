package com.ntmsecurity.sova.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ntmsecurity.sova.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getSupportActionBar().hide();

        YoYo.with(Techniques.FadeIn)
                .duration(1500)
                .repeat(1)
                .playOn(findViewById(R.id.appName));

        SharedPreferences preferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String firstTime = preferences.getString("FirstTimeInstall", "Yes");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(firstTime.equals("Yes")) {
                    Intent intent = new Intent(StartActivity.this, GetStarted.class);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("FirstTimeInstall", "No");
                    editor.apply();
                    finish();
                }
                else {
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1500);
    }
}