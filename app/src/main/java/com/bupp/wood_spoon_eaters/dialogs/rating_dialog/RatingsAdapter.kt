package com.bupp.wood_spoon_eaters.dialogs.rating_dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Comment
import com.bupp.wood_spoon_eaters.model.Review
import kotlinx.android.synthetic.main.dish_rating_item_view.view.*

class RatingsAdapter(val context: Context, private var comments: ArrayList<Comment>) :RecyclerView.Adapter<RatingsAdapter.DishViewHolder>() {

    class DishViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.dishRatingItemImage
        val name: TextView = view.dishRatingItemName
        val review: TextView = view.dishRatingItemReview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        return DishViewHolder(LayoutInflater.from(context).inflate(R.layout.dish_rating_item_view, parent, false))
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val review: Comment = comments[position]

        Glide.with(context).load(review.eater.thumbnail).apply(RequestOptions.circleCropTransform()).into(holder.image)
        holder.name.text = review.eater.getFullName()
        holder.review.text = review.body
    }
}