package com.example.myyoutube.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myyoutube.databinding.FragmentVideoListBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

    private fun hideLoading() {
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.videos.collect { videoList ->
                videoListAdapter.submitList(videoList)
                hideLoading()
                Log.d("ObserveViewModel", videoList.toString())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { errorMessageEvent ->
                errorMessageEvent.getContentIfNotHandled()?.let { showSnackbar(it) }
                hideLoading()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.shouldLaunchVideoPlaybackFragment.collect { shouldNavigateEvent ->
                shouldNavigateEvent.getContentIfNotHandled()?.let { video ->
                    val videoList = videoListAdapter.currentList
                    val videoIndex = videoList.indexOf(video)

                    findNavController().navigate(
                        VideoListFragmentDirections.actionVideoListFragmentToVideoPlaybackFragment(
                            videoIndex,
                            videoList.toTypedArray()
                        )
                    )
                }
            }
        }
    }
}