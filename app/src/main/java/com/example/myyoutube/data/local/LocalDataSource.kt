package com.example.myyoutube.data.local

import com.example.myyoutube.domain.entities.Video
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val videoDao: VideoDao
) {
    suspend fun getVideos(): List<Video> {
        return videoDao.getAllVideos().map { it.toDomain() }
    }

    suspend fun saveVideos(videos: List<Video>) {
        videoDao.clearVideos()
        videoDao.insertVideos(videos.map { it.toEntity() })
    }
}

fun VideoEntity.toDomain(): Video {
    return Video(
        id = id,
        title = title,
        duration = duration,
        thumbnailUrl = thumbnailUrl,
        videoUrl = videoUrl,
        author = author
    )
}

fun Video.toEntity(): VideoEntity {
    return VideoEntity(
        id = id,
        title = title,
        duration = duration,
        thumbnailUrl = thumbnailUrl,
        videoUrl = videoUrl,
        author = author
    )
}