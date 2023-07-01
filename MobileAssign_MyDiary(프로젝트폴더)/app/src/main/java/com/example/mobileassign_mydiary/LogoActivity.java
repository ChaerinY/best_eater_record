package com.example.mobileassign_mydiary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LogoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        // onCreate는 액티비티가 실행될때 가장먼저 실행되는 곳.

        // 딜레이를 2000밀리초 그 이후에 mainactivity로 이동
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //2초 후에 이 내용을 실행
                Intent mainIntent = new Intent(LogoActivity.this , MainActivity.class); //인텐트는 액티비티간의 이동을 제공 (현재->이동하려는곳)
                startActivity(mainIntent);
                finish(); //logo 액티비티 종료
            }
        }, 2000);


    }
}


