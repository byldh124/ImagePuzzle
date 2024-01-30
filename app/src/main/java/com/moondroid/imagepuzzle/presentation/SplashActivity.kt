package com.moondroid.imagepuzzle.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.moondroid.imagepuzzle.BuildConfig
import com.moondroid.imagepuzzle.common.ItemHelper
import com.moondroid.imagepuzzle.common.debug
import com.moondroid.imagepuzzle.databinding.ActivitySplashBinding
import com.moondroid.imagepuzzle.domain.Repository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAppVersion()

        binding.btnStart.setOnClickListener {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun checkAppVersion() {
        CoroutineScope(Dispatchers.Main).launch {
            repository.checkAppVersion(
                BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME, packageName
            ).collect {
                when (it) {
                    1000 -> getImageUrls()
                }
            }
        }
    }

    private fun getImageUrls() {
        CoroutineScope(Dispatchers.Main).launch {
            repository.getImageUrls().collect {
                binding.btnStart.isVisible = it.isNotEmpty()
                ItemHelper.itemList = it.shuffled()
            }
        }
    }
}