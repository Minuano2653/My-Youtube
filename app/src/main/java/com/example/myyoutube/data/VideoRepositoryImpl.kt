package com.example.myyoutube.data

import com.example.myyoutube.data.local.LocalDataSource
import com.example.myyoutube.data.network.RemoteDataSource
import com.example.myyoutube.data.network.toVideo
import com.example.myyoutube.domain.entities.Video
import com.example.myyoutube.domain.repositories.VideoRepository
import com.example.myyoutube.exceptions.GeneralException
import com.example.myyoutube.exceptions.NetworkException
import com.example.myyoutube.exceptions.ServerException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
): VideoRepository {

    override suspend fun getVideoList(): List<Video> {
        return try {

            val response = remoteDataSource.getVideos()
            val videoList = response.map { it.toVideo() }

            localDataSource.saveVideos(videoList)

            videoList
        } catch (e: IOException) {
            val cachedVideos = localDataSource.getVideos()
            if (cachedVideos.isNotEmpty()) {
                cachedVideos
            } else {
                throw NetworkException("Проблемы с сетью. Проверьте подключение к интернету.")
            }
        } catch (e: HttpException) {
            throw ServerException("Ошибка сервера: ${e.code()}")
        } catch (e: Exception) {
            throw GeneralException("Неизвестная ошибка")
        }
    }
}