package com.bupp.wood_spoon_eaters.bottom_sheets.reviews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.ReviewsItemBinding
import com.bupp.wood_spoon_eaters.databinding.ReviewsItemSkeletonBinding
import com.bupp.wood_spoon_eaters.utils.DateUtils

class ReviewsAdapter : ListAdapter<CommentAdapterItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int = if (getItem(position) is CommentSkeleton) 0 else 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val binding = ReviewsItemSkeletonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ReviewSkeletonItemViewHolder(binding)
        } else {
            val binding = ReviewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ReviewItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val review = getItem(position)) {
            is Comment-> {
                holder as ReviewItemViewHolder
                holder.bindItem(review)
            }
            is CommentSkeleton -> {
                holder as ReviewSkeletonItemViewHolder
                holder.bindItem()
            }
        }
    }

    inner class ReviewItemViewHolder(val binding: ReviewsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(data: Comment) {
            with(binding) {
                reviewsItemName.text = data.eater?.firstName
                reviewsItemComment.text = data.reviewText ?: ""
                data.eater?.thumbnail?.let { reviewsItemIcon.setImage(it.url) }
                val rating = data.rating ?: 0.0
                reviewsItemRating.isVisible = rating > 0.0
                reviewsItemRating.setRating(rating.toInt())
                reviewsItemDate.text = DateUtils.parseDateToMonthAndYear(data.reviewDate)
            }
        }
    }

    inner class ReviewSkeletonItemViewHolder(val binding: ReviewsItemSkeletonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem() {
            with(binding) {

            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CommentAdapterItem>() {
        override fun areItemsTheSame(
            oldItem: CommentAdapterItem,
            newItem: CommentAdapterItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CommentAdapterItem,
            newItem: CommentAdapterItem
        ): Boolean {
            return oldItem == oldItem
        }
    }
}


