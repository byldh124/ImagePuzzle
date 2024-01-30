package com.moondroid.imagepuzzle.di

import com.moondroid.imagepuzzle.data.RepositoryImpl
import com.moondroid.imagepuzzle.domain.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun provideAppRepository(repository: RepositoryImpl): Repository
}