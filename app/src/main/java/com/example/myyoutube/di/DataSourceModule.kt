package com.example.myyoutube.di

import com.example.myyoutube.data.local.LocalDataSource
import com.example.myyoutube.data.local.VideoDao
import com.example.myyoutube.data.network.RemoteDataSource
import com.example.myyoutube.network.VideoApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    fun provideRemoteDataSource(videoApiService: VideoApiService): RemoteDataSource {
        return RemoteDataSource(videoApiService)
    }

    @Provides
    fun provideLocalDataSource(videoDao: VideoDao): LocalDataSource {
        return LocalDataSource(videoDao)
    }
}