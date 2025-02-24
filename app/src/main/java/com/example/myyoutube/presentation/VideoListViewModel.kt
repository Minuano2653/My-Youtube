package com.example.myyoutube.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myyoutube.domain.entities.Video
import com.example.myyoutube.domain.usecases.GetVideoListUseCase
import com.example.myyoutube.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.myyoutube.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val getVideoListUseCase: GetVideoListUseCase
): ViewModel() {

    private val _videos = MutableStateFlow<Result<List<Video>>>(Result.Loading)
    val videos: StateFlow<Result<List<Video>>> = _videos.asStateFlow()

    private val _error = MutableSharedFlow<Event<String?>>(replay = 1)
    val error: SharedFlow<Event<String?>> = _error.asSharedFlow()

    private val _shouldLaunchVideoPlaybackFragment = MutableSharedFlow<Event<Video>>(replay = 1)
    val shouldLaunchVideoPlaybackFragment: SharedFlow<Event<Video>> = _shouldLaunchVideoPlaybackFragment.asSharedFlow()

    init {
        fetchVideos()
    }

    fun fetchVideos() {
        viewModelScope.launch {
            getVideoListUseCase().collect { result ->
                when(result) {
                    is Result.Loading -> _videos.emit(result)
                    is Result.Success -> _videos.emit(result)
                    is Result.Error -> _error.emit(Event(result.message))
                }
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