package com.moondroid.imagepuzzle.presentation.ui.upload

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.moondroid.imagepuzzle.common.logException
import com.moondroid.imagepuzzle.common.viewBinding
import com.moondroid.imagepuzzle.databinding.ActivityImageCropBinding
import com.moondroid.imagepuzzle.presentation.base.BaseActivity
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ImageCropActivity : BaseActivity() {
    private val binding by viewBinding(ActivityImageCropBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.icBack.setOnClickListener { finish() }

        val uri = intent.data

        binding.cropImageView.setImageUriAsync(uri)
        binding.cropImageView.setAspectRatio(1, 1)

        binding.btnDone.setOnClickListener {
            try {
                cropImageAndSaveCacheFile()
            } catch (e: Exception) {
                logException(e)
            }
        }

    }

    @Throws(FileNotFoundException::class, IOException::class, IllegalStateException::class)
    private fun cropImageAndSaveCacheFile() {
        val cropBitmap = binding.cropImageView.getCroppedImage()
            ?: throw IllegalStateException("Cropped image must not be null")

        val cacheStorage = cacheDir
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val tempFile = File(cacheStorage, fileName)

        if (!tempFile.exists()) {
            tempFile.createNewFile()
        }
        val out = FileOutputStream(tempFile)

        cropBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)

        out.close()

        val recvIntent = Intent()
        recvIntent.data = tempFile.toUri()
        setResult(RESULT_OK, recvIntent)
        finish()
    }
}
