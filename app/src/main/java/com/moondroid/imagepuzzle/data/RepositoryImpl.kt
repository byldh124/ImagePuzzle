package com.moondroid.imagepuzzle.data

import com.moondroid.imagepuzzle.domain.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val apiService: ApiService) : Repository {

    override suspend fun checkAppVersion(
        versionCode: Int,
        versionName: String,
        packageName: String,
    ): Flow<Int> {
        return flow {
            val response = apiService.checkAppVersion(versionCode, versionName, packageName)
            emit(response.code)
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getImageUrls(): Flow<List<String>> {
        return flow {
            val response = apiService.getImageUrls()
            emit(response.result)
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun upload(file: MultipartBody.Part): Flow<SimpleResponse> {
        return flow {
            emit(apiService.upload(file))
        }.flowOn(Dispatchers.IO)
    }
}