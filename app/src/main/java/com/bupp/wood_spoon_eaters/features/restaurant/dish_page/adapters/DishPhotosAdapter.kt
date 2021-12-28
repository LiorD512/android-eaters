package com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters

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
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.Comment
import com.bupp.wood_spoon_eaters.databinding.DishPhotoItemBinding
import com.bupp.wood_spoon_eaters.databinding.DishRatingItemViewBinding
import com.bupp.wood_spoon_eaters.model.WSImage
import com.bupp.wood_spoon_eaters.model.WSMedia
import com.bupp.wood_spoon_eaters.model.WSMediaType
import com.bupp.wood_spoon_eaters.model.WSMediaType.*

class DishPhotosAdapter(val context: Context, val listener: DishPhotosListener) :RecyclerView.Adapter<DishPhotosAdapter.DishViewHolder>() {

    interface DishPhotosListener{
        fun onPlayBtnClicked(videoUrl: String)
    }

    private var media: List<WSMedia> = listOf()
    fun setDishMedia(photos: List<WSMedia>){
        this.media = photos
        notifyDataSetChanged()
    }
    class DishViewHolder(view: DishPhotoItemBinding) : RecyclerView.ViewHolder(view.root) {
        val image: ImageView = view.dishPhoto
        val playBtn: ImageView = view.dishPhotoBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val binding = DishPhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DishViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return media.size
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val media: WSMedia = media[position]
        when(media.mediaType){
            VIDEO -> {
                Glide.with(context).load(media.url).into(holder.image)
                holder.playBtn.visibility = View.VISIBLE
                holder.playBtn.setOnClickListener {
                    media.url?.let { it1 -> listener.onPlayBtnClicked(it1) }
                }
            }
            IMAGE -> {
                Glide.with(context).load(media.url).into(holder.image)
            }
        }
    }
}