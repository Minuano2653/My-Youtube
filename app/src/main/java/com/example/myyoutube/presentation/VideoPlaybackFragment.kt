package com.example.myyoutube.presentation

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myyoutube.databinding.FragmentVideoPlaybackBinding


class VideoPlaybackFragment: Fragment() {
    private var _binding: FragmentVideoPlaybackBinding? = null
    private val binding: FragmentVideoPlaybackBinding
        get() = _binding ?: throw IllegalStateException("FragmentVideoPlaybackBinding is null")

    private val args by navArgs<VideoPlaybackFragmentArgs>()

    private var exoPlayer: ExoPlayer? = null

    private val viewModel: VideoPlaybackViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoPlaybackBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.mediaItemIndex == 0 && args.videoIndex >= 0) {
            viewModel.mediaItemIndex = args.videoIndex
        }

        initializePlayer()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFullScreen()) {
                    toggleFullScreen() // Выходим из полноэкранного режима
                } else {
                    findNavController().popBackStack() // Закрываем фрагмент
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        showSystemUi(false)
        if (exoPlayer == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        showSystemUi(true)
        _binding = null
    }

    private fun toggleFullScreen() {
        if (isFullScreen()) {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    private fun isFullScreen(): Boolean {
        return requireActivity().requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            binding.playerView.setFullscreenButtonClickListener {
                toggleFullScreen()
            }

            val mediaItems = args.videoList.mapNotNull { video ->
                video.videoUrl?.let { MediaItem.fromUri(it) }
            }

            exoPlayer.setMediaItems(mediaItems, viewModel.mediaItemIndex, viewModel.playbackPosition)
            exoPlayer.playWhenReady = viewModel.playWhenReady
            exoPlayer.seekTo(viewModel.playbackPosition)
            exoPlayer.prepare()
        }
    }

    private fun releasePlayer() {
        exoPlayer?.let { exoPlayer ->
            viewModel.playbackPosition = exoPlayer.currentPosition
            viewModel.playWhenReady = exoPlayer.playWhenReady
            viewModel.mediaItemIndex = exoPlayer.currentMediaItemIndex
            exoPlayer.release()
        }
        exoPlayer = null
    }

    @SuppressLint("InlinedApi")
    private fun showSystemUi(shouldShow: Boolean) {
        activity?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, shouldShow)
            WindowInsetsControllerCompat(window, binding.playerView).let { controller ->
                if (!shouldShow) {
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                    controller.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                } else {
                    controller.show(WindowInsetsCompat.Type.systemBars())
                }
            }
        }
    }
}