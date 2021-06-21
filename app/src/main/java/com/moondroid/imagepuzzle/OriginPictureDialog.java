package com.moondroid.imagepuzzle;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class OriginPictureDialog extends Dialog implements View.OnClickListener {
    private Context context;
    ImageView ivOriginDialog;
    ImageView ivDialogClose;
    Bitmap originBitmap;

    public OriginPictureDialog(@NonNull Context context, Bitmap bitmap) {
        super(context);
        this.context = context;
        this.originBitmap = bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_origin_picture);

        ivOriginDialog = findViewById(R.id.iv_origin_dialog);
        ivOriginDialog.setImageBitmap(originBitmap);
        ivOriginDialog.setClickable(true);
        ivOriginDialog.setOnClickListener(this);
        ivDialogClose = findViewById(R.id.iv_dialog_close);
        ivDialogClose.setClickable(true);
        ivDialogClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
