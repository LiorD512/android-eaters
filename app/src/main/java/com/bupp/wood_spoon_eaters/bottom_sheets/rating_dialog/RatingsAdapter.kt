package com.bupp.wood_spoon_eaters.bottom_sheets.rating_dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.Comment
import com.bupp.wood_spoon_eaters.databinding.DishRatingItemViewBinding

class RatingsAdapter(val context: Context, private var comments: List<Comment>) :RecyclerView.Adapter<RatingsAdapter.DishViewHolder>() {

    class DishViewHolder(view: DishRatingItemViewBinding) : RecyclerView.ViewHolder(view.root) {
        val image: ImageView = view.dishRatingItemImage
        val name: TextView = view.dishRatingItemName
        val review: TextView = view.dishRatingItemReview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val binding = DishRatingItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DishViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val review: Comment = comments[position]

        review.eater.thumbnail?.let {
            Glide.with(context).load(review.eater.thumbnail).placeholder(R.drawable.profile_pic_placeholder).apply(RequestOptions.circleCropTransform()).into(holder.image)
        }

        holder.name.text = review.eater.getFullName()
        holder.review.text = review.body
    }
}