package com.example.myyoutube.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Video(
    val id: String,
    val title: String,
    val duration: String,
    val author: String,
    val thumbnailUrl: String?,
    val videoUrl: String?
): Parcelable