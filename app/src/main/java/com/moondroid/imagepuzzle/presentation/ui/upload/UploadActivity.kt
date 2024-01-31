package com.moondroid.imagepuzzle.presentation.ui.upload

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.moondroid.imagepuzzle.common.ImageHelper
import com.moondroid.imagepuzzle.common.viewBinding
import com.moondroid.imagepuzzle.databinding.ActivityUploadBinding
import com.moondroid.imagepuzzle.domain.Repository
import com.moondroid.imagepuzzle.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class UploadActivity : BaseActivity() {
    private val binding by viewBinding(ActivityUploadBinding::inflate)

    @Inject
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.icBack.setOnClickListener { finish() }
        binding.btnFindImage.setOnClickListener { getImage() }
        binding.btnUploadImage.setOnClickListener { upload() }
    }

    private fun upload() {
        val file = path?.let {
            File(it)
        } ?: return

        val requestBody = file.asRequestBody("image/*".toMediaType())
        val filePart = MultipartBody.Part.createFormData("puzzle", file.name, requestBody)

        CoroutineScope(Dispatchers.Main).launch {
            repository.upload(filePart).collect {
                if (it.code == 1000) {
                    Toast.makeText(mContext, "이미지 업로드 완료", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(mContext, "이미지 업로드를 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    cropImageLauncher.launch(Intent(mContext, ImageCropActivity::class.java).apply {
                        data = uri
                    })
                }
            }
        }

    private val cropImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    path = ImageHelper.getPathFromUri(mContext, uri)
                    if (!path.isNullOrEmpty()) {
                        val bitmap = BitmapFactory.decodeFile(path)
                        Glide.with(mContext).load(bitmap).into(binding.ivPickedImage)
                    }
                }
            }
        }

    private var path: String? = null


    private fun getImage() {
        imageLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
    }
}