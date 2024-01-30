package com.moondroid.imagepuzzle.presentation

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import com.bumptech.glide.Glide
import com.moondroid.imagepuzzle.R
import com.moondroid.imagepuzzle.databinding.DialogOriginPictureBinding

class OriginImageDialog(context: Context, private val bitmap: Bitmap) : Dialog(context, R.style.DialogTheme) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DialogOriginPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(context).load(bitmap).into(binding.ivBitmap)
        binding.icClose.setOnClickListener { cancel() }
        binding.root.setOnClickListener { cancel() }
    }
}