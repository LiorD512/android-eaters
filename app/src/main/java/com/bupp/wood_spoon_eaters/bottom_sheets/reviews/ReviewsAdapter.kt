package com.bupp.wood_spoon_eaters.bottom_sheets.reviews

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.*
import com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders.FeedAdapterCampaignViewHolder
import com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders.FeedAdapterTitleViewHolder
import com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders.FeedAdapterRestaurantViewHolder
import com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders.FeedAdapterSkeletonViewHolder
import com.bupp.wood_spoon_eaters.model.*

class ReviewsAdapter() : ListAdapter<ReviewsBaseAdapterItem, RecyclerView.ViewHolder>(DiffCallback()) {


    override fun getItemViewType(position: Int): Int = getItem(position).type!!.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ReviewsBaseAdapterViewType.TITLE.ordinal -> {
                val binding = ReviewsTitleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ReviewTitleItemViewHolder(binding)
            }
            else -> { //ReviewsBaseAdapterViewType.REVIEW
                val binding = ReviewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ReviewItemViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        Log.d("wowReviewsAdapter","position: $position")
        val review = getItem(position)
        when (review) {
            is ReviewAdapterTitleItem -> {
                holder as ReviewTitleItemViewHolder
                holder.bindItem(review)
            }
            is ReviewAdapterItem -> {
                holder as ReviewItemViewHolder
                holder.bindItem(review)
            }
        }
    }

    inner class ReviewItemViewHolder(val binding: ReviewsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(data: ReviewAdapterItem) {
            data.comment.eater
            data.comment.body
        }

        val icon: ImageView = binding.reviewsItemIcon
    }

    inner class ReviewTitleItemViewHolder(val binding: ReviewsTitleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(data: ReviewAdapterTitleItem) {
            val reviewStr = "${data.rating} (${data.reviewCount} revies)"
            binding.reviewTitle.text = reviewStr
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ReviewsBaseAdapterItem>() {
        override fun areItemsTheSame(
            oldItem: ReviewsBaseAdapterItem,
            newItem: ReviewsBaseAdapterItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ReviewsBaseAdapterItem,
            newItem: ReviewsBaseAdapterItem
        ): Boolean {
            return oldItem.type == oldItem.type
        }
    }
}

