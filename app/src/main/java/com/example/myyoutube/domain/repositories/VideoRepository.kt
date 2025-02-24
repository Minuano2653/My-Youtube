package com.example.myyoutube.domain.repositories

import com.example.myyoutube.domain.entities.Video
import kotlinx.coroutines.flow.Flow
import com.example.myyoutube.utils.Result

interface VideoRepository {
    suspend fun getVideoList(): Flow<Result<List<Video>>>
}