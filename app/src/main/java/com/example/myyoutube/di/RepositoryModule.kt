package com.example.myyoutube.di

import com.example.myyoutube.data.VideoRepositoryImpl
import com.example.myyoutube.data.local.LocalDataSource
import com.example.myyoutube.data.local.VideoDao
import com.example.myyoutube.data.network.RemoteDataSource
import com.example.myyoutube.domain.repositories.VideoRepository
import com.example.myyoutube.domain.usecases.GetVideoListUseCase
import com.example.myyoutube.network.VideoApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideVideoRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource
    ): VideoRepository {
        return VideoRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Provides
    fun provideGetVideoListUseCase(videoRepository: VideoRepository): GetVideoListUseCase {
        return GetVideoListUseCase(videoRepository)
    }
}