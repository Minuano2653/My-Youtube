package com.example.myyoutube.data

import com.example.myyoutube.data.local.LocalDataSource
import com.example.myyoutube.data.network.RemoteDataSource
import com.example.myyoutube.data.network.toVideo
import com.example.myyoutube.domain.entities.Video
import com.example.myyoutube.domain.repositories.VideoRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import com.example.myyoutube.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow


@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
): VideoRepository {

    override suspend fun getVideoList(): Flow<Result<List<Video>>> = flow {
        emit(Result.Loading)
        val cachedVideos = localDataSource.getVideos().takeIf { it.isNotEmpty() }
        cachedVideos?.let { emit(Result.Success(it)) }

        val response = remoteDataSource.getVideos()
        val videoList = response.map { it.toVideo() }
        localDataSource.saveVideos(videoList)
        emit(Result.Success(videoList))
    }.catch { e ->
        emit(Result.Error(handleError(e)))
    }

    private fun handleError(e: Throwable): String {
        return when (e) {
            is IOException -> "Проблемы с сетью. Проверьте подключение к интернету."
            is HttpException -> "Ошибка сервера: ${e.code()}"
            else -> "Неизвестная ошибка"
        }
    }
}