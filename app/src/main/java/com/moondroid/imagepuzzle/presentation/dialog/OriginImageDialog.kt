package com.moondroid.imagepuzzle.presentation.dialog

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import com.bumptech.glide.Glide
import com.moondroid.imagepuzzle.databinding.DialogOriginPictureBinding
import com.moondroid.imagepuzzle.presentation.base.BaseDialog

class OriginImageDialog(context: Context, private val bitmap: Bitmap) : BaseDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DialogOriginPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(context).load(bitmap).into(binding.ivBitmap)
        binding.icClose.setOnClickListener { cancel() }
        binding.root.setOnClickListener { cancel() }
    }
}