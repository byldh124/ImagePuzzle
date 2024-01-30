package com.moondroid.imagepuzzle.data

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("checkVersion.php")
    suspend fun checkAppVersion(
        @Query("versionCode") versionCode: Int,
        @Query("versionName") versionName: String,
        @Query("packageName") packageName: String,
    ): SimpleResponse

    @GET("items.php")
    suspend fun getImageUrls(): Response
}