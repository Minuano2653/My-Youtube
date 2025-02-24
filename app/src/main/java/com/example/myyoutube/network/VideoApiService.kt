package com.example.myyoutube.network

import retrofit2.http.GET
import retrofit2.http.Query

interface VideoApiService {
    @GET("api")
    suspend fun getVideos(): List<VideoDto>
}