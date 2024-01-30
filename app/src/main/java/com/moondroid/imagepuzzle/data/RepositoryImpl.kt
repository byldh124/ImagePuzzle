package com.moondroid.imagepuzzle.data

import com.moondroid.imagepuzzle.domain.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val retrofit: Retrofit) : Repository {

    override suspend fun checkAppVersion(
        versionCode: Int,
        versionName: String,
        packageName: String,
    ): Flow<Int> {
        return flow {
            retrofit.create(ApiService::class.java)
                .checkAppVersion(versionCode, versionName, packageName)
                .run {
                    emit(code)
                }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getImageUrls(): Flow<List<String>> {
        return flow {
            retrofit.create(ApiService::class.java).getImageUrls().run {
                emit(result)
            }
        }.flowOn(Dispatchers.IO)
    }
}