package com.example.myyoutube.presentation

import androidx.lifecycle.ViewModel

class VideoPlaybackViewModel: ViewModel() {
    var playWhenReady = true
    var playbackPosition = 0L
    var mediaItemIndex = 0
}