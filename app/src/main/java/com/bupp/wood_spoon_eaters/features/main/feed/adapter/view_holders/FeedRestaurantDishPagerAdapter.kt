package com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import at.favre.lib.dali.Dali
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bupp.wood_spoon_eaters.databinding.*
import com.bupp.wood_spoon_eaters.model.*

class FeedRestaurantDishPagerAdapter :
    ListAdapter<FeedRestaurantSectionItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int = getItem(position).type!!.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return return when (viewType) {
            FeedRestaurantSectionItemViewType.DISH.ordinal -> {
                val binding = FeedAdapterRestaurantDishItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedDishViewHolder(binding)
            }
            else -> { //FeedRestaurantSectionItemViewType.SEE_MORE
                val binding = FeedAdapterRestaurantSeeMoreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedDishSeeMoreViewHolder(binding)
            }

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item.data) {
            is FeedRestaurantItemDish -> {
                holder as FeedDishViewHolder
                holder.bindItem(holder.itemView.context, item.data as FeedRestaurantItemDish)
            }
            is FeedRestaurantItemSeeMore -> {
                holder as FeedDishSeeMoreViewHolder
                holder.bindItem(holder.itemView.context, item.data as FeedRestaurantItemSeeMore)
            }
        }
    }
//
//    class FeedDishViewHolder(binding: FeedAdapterRestaurantDishItemBinding) : RecyclerView.ViewHolder(binding.root) {
//        private val thumbnail: ImageView = binding.feedRestaurantDishItemImg
//
//        fun bindItem(context: Context, dish: FeedRestaurantItemDish) {
//            Log.d("wowFeedPager","bindItem: $dish")
//            Glide.with(context).load(dish.thumbnail_url).into(thumbnail)
//        }
//    }

    class FeedDishViewHolder(binding: FeedAdapterRestaurantDishItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val layout: ConstraintLayout = binding.feedRestaurantDishItem
        private val thumbnail: ImageView = binding.feedRestaurantDishItemImg
        private val name: TextView = binding.feedRestaurantItemName
        private val price: TextView = binding.feedRestaurantItemPrice

        fun bindItem(context: Context, dish: FeedRestaurantItemDish) {
            Glide.with(context).load(dish.thumbnail_url).into(thumbnail)
            name.text = dish.name
            price.text = dish.formatted_price

        }
    }

    class FeedDishSeeMoreViewHolder(binding: FeedAdapterRestaurantSeeMoreItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val thumbnail: ImageView = binding.feedRestaurantSeeMoreItemImg
        private val quantityLeft: TextView = binding.feedRestaurantSeeMoreItemQuantityLeft
        private val price: TextView = binding.feedRestaurantSeeMoreItemPrice

        fun bindItem(context: Context, dish: FeedRestaurantItemSeeMore) {
            quantityLeft.text = dish.title
            price.text = dish.formatted_price
            Glide.with(context)
                .asBitmap()
                .load(dish.thumbnail_url)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        Dali.create(context).load(resource).blurRadius(12).reScale().into(thumbnail)
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FeedRestaurantSectionItem>() {

        override fun areItemsTheSame(oldItem: FeedRestaurantSectionItem, newItem: FeedRestaurantSectionItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FeedRestaurantSectionItem, newItem: FeedRestaurantSectionItem): Boolean {
            return oldItem == newItem
        }
    }
}