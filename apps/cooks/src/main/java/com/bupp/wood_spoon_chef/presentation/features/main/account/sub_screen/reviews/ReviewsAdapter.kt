package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.reviews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bupp.wood_spoon_chef.databinding.ReviewsItemBinding
import com.bupp.wood_spoon_chef.databinding.ReviewsItemSkeletonBinding
import com.bupp.wood_spoon_chef.data.remote.model.Comment
import com.bupp.wood_spoon_chef.data.remote.model.CommentAdapterItem
import com.bupp.wood_spoon_chef.data.remote.model.CommentSkeleton
import com.bupp.wood_spoon_chef.utils.DateUtils

class ReviewsAdapter : ListAdapter<CommentAdapterItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int = if (getItem(position) is CommentSkeleton) 0 else 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val binding = ReviewsItemSkeletonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReviewSkeletonItemViewHolder(binding)
        } else {
            val binding = ReviewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReviewItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val review = getItem(position)) {
            is Comment -> {
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
                data.eater?.thumbnail?.let {
                    Glide.with(root).load(it).transform(CircleCrop()).into(binding.reviewsItemIcon)
                }
                val rating = data.rating
                rating?.let{
                    reviewsItemRating.isVisible = rating > 0.0
                    reviewsItemRating.setRating(rating.toInt())
                }
                data.reviewDate?.let{
                    reviewsItemDate.text = DateUtils.parseDateToMonthAndYear(it)
                }
            }
        }
    }

    inner class ReviewSkeletonItemViewHolder(val binding: ReviewsItemSkeletonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem() {
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


