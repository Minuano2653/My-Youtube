package com.example.myyoutube.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val duration: String,
    val thumbnailUrl: String?,
    val videoUrl: String?,
    val author: String
)