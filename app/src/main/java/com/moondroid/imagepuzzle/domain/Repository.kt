package com.moondroid.imagepuzzle.domain

import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun checkAppVersion(
        versionCode: Int,
        versionName: String,
        packageName: String
    ): Flow<Int>

    suspend fun getImageUrls() : Flow<List<String>>
}