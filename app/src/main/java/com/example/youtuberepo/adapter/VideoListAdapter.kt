package com.example.youtuberepo.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.youtuberepo.data.model.Items
import com.example.youtuberepo.databinding.ItemVideoPreviewBinding
import java.text.SimpleDateFormat
import java.util.*

class VideoListAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Items, VideoListAdapter.ArticleViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =
            ItemVideoPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class ArticleViewHolder(private val binding: ItemVideoPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val article = getItem(position)
                        listener.onItemClick(article)
                    }
                }
            }
        }

        fun bind(items: Items) {
            binding.apply {
                Glide.with(itemView)
                    .load(items.snippet?.thumbnails?.high?.url)
                    .into(ivThumb)
                tvTitle.text = items.snippet?.title.toString().trim()
                tvChannel.text = items.snippet?.channelTitle.toString().trim()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(items: Items)
    }


    class DiffCallback : DiffUtil.ItemCallback<Items>() {
        override fun areItemsTheSame(oldItem: Items, newItem: Items): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Items, newItem: Items): Boolean {
            return oldItem == newItem
        }

    }

}