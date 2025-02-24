package com.example.myyoutube.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myyoutube.databinding.FragmentVideoListBinding
import com.example.myyoutube.domain.entities.Video
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.myyoutube.utils.Result
import com.example.myyoutube.utils.showSnackbar
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class VideoListFragment: Fragment() {
    private var _binding: FragmentVideoListBinding? = null
    private val binding: FragmentVideoListBinding
        get() = _binding ?: throw IllegalStateException("FragmentVideoListBinding is null")

    private val viewModel: VideoListViewModel by viewModels()

    private lateinit var videoListAdapter: VideoListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSwipeRefreshListener()
        setupRecyclerView()
        observeViewModel()

    }

    private fun showLoading() {
        binding.swipeRefreshLayout.isRefreshing = true
    }

    private fun hideLoading() {
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun setSwipeRefreshListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchVideos()
        }
    }

    private fun setOnVideoClickListener() {
        videoListAdapter.onVideoClickListener = { video ->
            viewModel.launchVideoPlaybackFragment(video)
        }
    }

    private fun launchVideoPlaybackFragment(video: Video) {
        val videoList = videoListAdapter.currentList
        val videoIndex = videoList.indexOf(video)

        findNavController().navigate(
            VideoListFragmentDirections.actionVideoListFragmentToVideoPlaybackFragment(
                videoIndex,
                videoList.toTypedArray()
            )
        )
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        videoListAdapter = VideoListAdapter()
        with(binding) {
            recyclerView.adapter = videoListAdapter
            recyclerView.layoutManager = linearLayoutManager
        }
        setOnVideoClickListener()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videos.collect { result ->
                    when (result) {
                        is Result.Loading -> showLoading()
                        is Result.Success -> {
                            videoListAdapter.submitList(result.data)
                            hideLoading()
                        }
                        is Result.Error -> {
                            hideLoading()
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { event ->
                    event.getContentIfNotHandled()?.let { message ->
                        hideLoading()
                        showSnackbar(message)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shouldLaunchVideoPlaybackFragment.collect { event ->
                    event.getContentIfNotHandled()?.let { video ->
                        launchVideoPlaybackFragment(video)
                    }
                }
            }
        }
    }
}