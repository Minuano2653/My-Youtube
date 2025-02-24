package com.example.myyoutube.domain.usecases

import com.example.myyoutube.domain.entities.Video
import com.example.myyoutube.domain.repositories.VideoRepository
import javax.inject.Inject
import com.example.myyoutube.utils.Result
import kotlinx.coroutines.flow.Flow


class GetVideoListUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(): Flow<Result<List<Video>>> {
        return videoRepository.getVideoList()
    }
}