package com.moondroid.imagepuzzle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ImagePickerActivity extends AppCompatActivity implements View.OnClickListener {

    private final int CAMERA_ACTION_PICK_REQUEST_CODE = 0;
    private final int PICK_IMAGE_GALLERY_REQUEST_CODE = 1;
    private String currentPhotoPath = "";
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    Button btnFindImage;
    Button btnUploadImage;
    ImageView ivSelectedImage;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, permissions, 3);
        }

        Toolbar toolbar = findViewById(R.id.toolbar_image_picker);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnFindImage = findViewById(R.id.btn_find_image);
        btnUploadImage = findViewById(R.id.btn_upload_image);
        ivSelectedImage = findViewById(R.id.iv_picked_image);

        ivSelectedImage.setClickable(true);

        btnFindImage.setOnClickListener(this);
        ivSelectedImage.setOnClickListener(this);

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri == null) return;
                uploadImage(uri);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(512, 512)
                .withAspectRatio(5f, 5f)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_ACTION_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = Uri.parse(currentPhotoPath);
            openCropActivity(uri, uri);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            uri = UCrop.getOutput(data);
            Glide.with(this).load(uri).into(ivSelectedImage);
        } else if (requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri sourceUri = data.getData(); // 1
//            File file = getImageFile(); // 2
//            Uri destinationUri = Uri.fromFile(file);  // 3
//            openCropActivity(sourceUri, destinationUri);  // 4
            startCrop(sourceUri);
        }
    }

    private void startCrop(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;


        destinationFileName += ".jpg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withMaxResultSize(512, 512).withAspectRatio(5f, 5f);
        uCrop.start(this);

    }


    private void uploadImage(Uri imageUri) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("잠시만 기다려 주십시오");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("images/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg");
        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map<String, String> url = new HashMap<>();
                        url.put("url", uri.toString());
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference imgRef = db.collection("imageUrl");
                        imgRef.add(url).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(ImagePickerActivity.this, "이미지가 올라갔습니다.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    private File getImageFile() {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        File file = null;
        try {
            file = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = "file:" + file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("io Error", e.getMessage());
        } finally {
            return file;
        }
    }

    private void openCamera() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getImageFile(); // 1
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // 2
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.concat(".provider"), file);
        else
            uri = Uri.fromFile(file); // 3
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 4
        startActivityForResult(pictureIntent, CAMERA_ACTION_PICK_REQUEST_CODE);
    }

    private void openImagesDocument() {
        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pictureIntent.setType("image/*");  // 1
        pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);  // 2
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = new String[]{"image/jpeg", "image/png"};  // 3
            pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(Intent.createChooser(pictureIntent, "Select Picture"), PICK_IMAGE_GALLERY_REQUEST_CODE);  // 4
    }

    @Override
    public void onClick(View v) {
        openImagesDocument();
    }
}