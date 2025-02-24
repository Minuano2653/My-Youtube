package com.example.myyoutube.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myyoutube.domain.entities.Video
import com.example.myyoutube.domain.usecases.GetVideoListUseCase
import com.example.myyoutube.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val getVideoListUseCase: GetVideoListUseCase
): ViewModel() {

    private val _videos = MutableSharedFlow<List<Video>>(replay = 1)
    val videos: SharedFlow<List<Video>> = _videos.asSharedFlow()

    private val _error = MutableSharedFlow<Event<String?>>(replay = 1)
    val error: SharedFlow<Event<String?>> = _error.asSharedFlow()

    private val _shouldLaunchVideoPlaybackFragment = MutableSharedFlow<Event<Video>>(replay = 1)
    val shouldLaunchVideoPlaybackFragment: SharedFlow<Event<Video>> = _shouldLaunchVideoPlaybackFragment.asSharedFlow()

    init {
        fetchVideos()
    }

    fun fetchVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val videoList = getVideoListUseCase()
                Log.d("VIEWMDODEL", videoList.toString())
                _videos.emit(videoList)
                _error.emit(Event(null))
            } catch (e: Exception) {
                _error.emit(Event(e.localizedMessage))
            }
        }
    }

    fun launchVideoPlaybackFragment(video: Video) {
        viewModelScope.launch {
            if (video.videoUrl.isNullOrEmpty()) {
                _error.emit(Event("Это видео недоступно"))
            } else {
                _shouldLaunchVideoPlaybackFragment.emit(Event(video))
            }
        }
    }
}