package com.example.myyoutube.domain.repositories

import com.example.myyoutube.domain.entities.Video

interface VideoRepository {
    suspend fun getVideoList(): List<Video>
}