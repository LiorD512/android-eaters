package com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters

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
import com.bupp.wood_spoon_eaters.databinding.DishPhotoItemBinding
import com.bupp.wood_spoon_eaters.databinding.DishRatingItemViewBinding
import com.bupp.wood_spoon_eaters.model.WSImage

class DishPhotosAdapter(val context: Context) :RecyclerView.Adapter<DishPhotosAdapter.DishViewHolder>() {

    private var photos: List<WSImage> = listOf()
    fun setDishPhotos(photos: List<WSImage>){
        this.photos = photos
        notifyDataSetChanged()
    }
    class DishViewHolder(view: DishPhotoItemBinding) : RecyclerView.ViewHolder(view.root) {
        val image: ImageView = view.dishPhoto
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val binding = DishPhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DishViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val photo: WSImage = photos[position]

        photo.url?.let {
            Glide.with(context).load(it).into(holder.image)
        }

    }
}