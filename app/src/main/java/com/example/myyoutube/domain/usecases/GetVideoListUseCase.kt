package com.example.myyoutube.domain.usecases

import com.example.myyoutube.domain.entities.Video
import com.example.myyoutube.domain.repositories.VideoRepository
import javax.inject.Inject

class GetVideoListUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(): List<Video> {
        return videoRepository.getVideoList()
    }
}