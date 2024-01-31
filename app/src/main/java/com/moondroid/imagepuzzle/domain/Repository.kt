package com.moondroid.imagepuzzle.domain

import com.moondroid.imagepuzzle.data.SimpleResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface Repository {
    suspend fun checkAppVersion(
        versionCode: Int,
        versionName: String,
        packageName: String
    ): Flow<Int>

    suspend fun getImageUrls() : Flow<List<String>>

    suspend fun upload(file: MultipartBody.Part) : Flow<SimpleResponse>
}