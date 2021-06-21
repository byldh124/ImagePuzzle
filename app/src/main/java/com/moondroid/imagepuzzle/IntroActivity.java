package com.moondroid.imagepuzzle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class IntroActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
//    FirebaseDatabase firebaseDatabase;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

//        firebaseDatabase = FirebaseDatabase.getInstance("users");

        progressDialog = new ProgressDialog(this);

        progressDialog.setCancelable(false);
        progressDialog.setMessage("사진 이미지를 불러오는 중입니다~\n잠시만 기다려주십시오.");

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("imageUrl").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String url = (String) documentSnapshot.get("url");
                        ImageValue.imgUrls.add(url);
                    }
                    Collections.shuffle(ImageValue.imgUrls);
                    progressDialog.dismiss();
                    startActivity(new Intent(IntroActivity.this, MainActivity.class));
                    IntroActivity.this.finish();
                }
            }
        });

    }
}