package com.bupp.wood_spoon_eaters.bottom_sheets.reviews

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.ReviewsItemBinding
import com.bupp.wood_spoon_eaters.databinding.ReviewsTitleItemBinding

class ReviewsAdapter: ListAdapter<Comment, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ReviewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val review = getItem(position)
        holder as ReviewItemViewHolder
        holder.bindItem(review)
    }

    inner class ReviewItemViewHolder(val binding: ReviewsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(data: Comment) {
            with(binding){
                reviewsItemName.text = data.eater?.firstName
                reviewsItemComment.text = data.reviewText
                data.eater?.thumbnail?.let { reviewsItemIcon.setImage(it) }
                val rating = data.rating?:0
                reviewsItemRating.isVisible = rating > 0
                reviewsItemRating.setRating(rating)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(
            oldItem: Comment,
            newItem: Comment
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Comment,
            newItem: Comment
        ): Boolean {
            return oldItem.id == oldItem.id
        }
    }
}

