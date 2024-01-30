package com.moondroid.imagepuzzle;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IntroActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
//    FirebaseDatabase firebaseDatabase;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

//        firebaseDatabase = FirebaseDatabase.getInstance("users");

        progressDialog = new ProgressDialog(this);

        progressDialog.setCancelable(false);
        progressDialog.setMessage("사진 이미지를 불러오는 중입니다~\n잠시만 기다려주십시오.");

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDialog.show();

    }
}