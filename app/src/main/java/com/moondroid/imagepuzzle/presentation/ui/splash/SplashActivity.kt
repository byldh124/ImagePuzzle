package com.moondroid.imagepuzzle.presentation.ui.splash

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.moondroid.imagepuzzle.BuildConfig
import com.moondroid.imagepuzzle.common.ItemHelper
import com.moondroid.imagepuzzle.common.exitApp
import com.moondroid.imagepuzzle.common.logException
import com.moondroid.imagepuzzle.common.viewBinding
import com.moondroid.imagepuzzle.databinding.ActivitySplashBinding
import com.moondroid.imagepuzzle.domain.Repository
import com.moondroid.imagepuzzle.presentation.base.BaseActivity
import com.moondroid.imagepuzzle.presentation.dialog.ButtonDialog
import com.moondroid.imagepuzzle.presentation.ui.home.MainActivity
import com.moondroid.imagepuzzle.presentation.ui.upload.UploadActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private val binding by viewBinding(ActivitySplashBinding::inflate)

    @Inject
    lateinit var repository: Repository

    private val launcher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            getImageUrls()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnStart.setOnClickListener {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }

        binding.btnUpload.setOnClickListener {
            checkPermission()
        }
    }

    override fun onStart() {
        super.onStart()
        checkAppVersion()
    }

    private fun checkAppVersion() {
        CoroutineScope(Dispatchers.Main).launch {
            repository.checkAppVersion(
                BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME, packageName
            ).collect {
                when (it) {
                    1000 -> getImageUrls()
                    2003 -> requestVersionUpdate()
                }
            }
        }
    }

    /**
     *  플레이스토어로 이동
     */
    private fun requestVersionUpdate() {
        val builder = ButtonDialog.Builder(mContext).apply {
            message = "새로운 버전이 출시됐습니다.\\n업데이트 후 이용이 가능합니다."
            setPositiveButton("업데이트") {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("market://details?id=$packageName")
                    startActivity(intent)
                } catch (e: Exception) {
                    logException(e)
                    exitApp()
                }
            }
            setNegativeButton("나가기", ::finish)
            cancelable = false
        }
        builder.show()
    }

    private fun getImageUrls() {
        CoroutineScope(Dispatchers.Main).launch {
            repository.getImageUrls().collect {
                binding.btnStart.isVisible = it.isNotEmpty()
                ItemHelper.itemList = it.shuffled()
            }
        }
    }

    /** 이미지 퍼미션 런쳐 **/
    private val requestPermissionLauncher =
        registerForActivityResult(
            RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                toUpload()
            } else {
                Toast.makeText(mContext, "이미지 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    /**
     * 이미지 퍼미션 확인
     **/
    private fun checkPermission() {
        val imagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                mContext,
                imagePermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            toUpload()
        } else {
            requestPermissionLauncher.launch(imagePermission)
        }
    }

    private fun toUpload() {
        launcher.launch(Intent(mContext, UploadActivity::class.java))
    }
}