package com.example.myyoutube.network

data class VideoDto(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val duration: String,
    val uploadTime: String,
    val views: String,
    val author: String,
    val videoUrl: String,
    val description: String,
    val subscriber: String,
    val isLive: Boolean
)