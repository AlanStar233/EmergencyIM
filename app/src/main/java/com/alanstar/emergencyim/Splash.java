package com.alanstar.emergencyim;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class Splash extends AppCompatActivity {

    //create
    Button bt_skip;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);   //隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();   //隐藏标题栏
        setContentView(R.layout.activity_splash);

        //bind
        bt_skip = findViewById(R.id.bt_skip);

        //Intent启动MainActivity
        Thread splashThread = new Thread() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                try {
                    sleep(3000);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);    //启动Splash
                    startActivity(intent);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        splashThread.start();   //启动线程

        //skip Listener
        bt_skip.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);    //直接启动MainActivity
            startActivity(intent);
            finish();
            splashThread.stop();    //当跳过按钮按下后终止splashThread线程，防止二次呈现MainActivity(但有间歇性黑屏，取决于机器性能)
        });
    }
}