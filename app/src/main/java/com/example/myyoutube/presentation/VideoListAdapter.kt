package com.example.myyoutube.presentation

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.myyoutube.R
import com.example.myyoutube.databinding.ItemVideoBinding
import com.example.myyoutube.domain.entities.Video

class VideoListAdapter: ListAdapter<Video, VideoListAdapter.VideoViewHolder>(VideoDiffCallback()) {

    var onVideoClickListener: ((Video) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = getItem(position)

        with(holder.binding) {
            titleTextView.text = video.title
            durationTextView.text = video.duration
            authorTextView.text = video.author

            root.setOnClickListener {
                onVideoClickListener?.invoke(video)
            }
        }

        val radiusInPx = holder.itemView.context.dpToPx(14f)

        Glide.with(holder.itemView.context)
            .load(video.thumbnailUrl)
            .placeholder(R.drawable.play)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(radiusInPx)))
            .into(holder.binding.thumbnailImageView)
    }

    class VideoViewHolder(val binding: ItemVideoBinding): RecyclerView.ViewHolder(binding.root)
}

class VideoDiffCallback: DiffUtil.ItemCallback<Video>() {

    override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
        return oldItem == newItem
    }
}

fun Context.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        resources.displayMetrics
    ).toInt()
}