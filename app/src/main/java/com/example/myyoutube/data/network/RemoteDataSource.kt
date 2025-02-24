package com.example.myyoutube.data.network

import com.example.myyoutube.domain.entities.Video
import com.example.myyoutube.network.VideoApiService
import com.example.myyoutube.network.VideoDto
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val videoApiService: VideoApiService
) {
    suspend fun getVideos(): List<VideoDto> = videoApiService.getVideos()
}

fun VideoDto.toVideo(): Video {
    return Video(
        id = id,
        title = title,
        duration = duration,
        thumbnailUrl = thumbnailUrl,
        videoUrl = videoUrl,
        author =author
    )
}