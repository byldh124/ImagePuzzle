package com.moondroid.imagepuzzle.data

import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {
    @GET("checkVersion.php")
    suspend fun checkAppVersion(
        @Query("versionCode") versionCode: Int,
        @Query("versionName") versionName: String,
        @Query("packageName") packageName: String,
    ): SimpleResponse

    @GET("items.php")
    suspend fun getImageUrls(): Response

    @JvmSuppressWildcards
    @Multipart
    @POST("upload.php")
    suspend fun upload(
        @Part file: MultipartBody.Part,
    ): SimpleResponse
}