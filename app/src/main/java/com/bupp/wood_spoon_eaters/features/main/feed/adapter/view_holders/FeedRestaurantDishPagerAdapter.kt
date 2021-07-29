package com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
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
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantDishItemBinding
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantSeeMoreItemBinding
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.views.dish_tags_view.DishTagsView
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

class FeedRestaurantDishPagerAdapter(val listener : FeedRestaurantDishPagerAdapterListener) :
    ListAdapter<FeedRestaurantSectionItem, RecyclerView.ViewHolder>(DiffCallback()) {

    interface FeedRestaurantDishPagerAdapterListener{
        fun onPageClick()
    }

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
                holder.bindItem(listener,holder.itemView.context, item.data as FeedRestaurantItemDish)
            }
            is FeedRestaurantItemSeeMore -> {
                holder as FeedDishSeeMoreViewHolder
                holder.bindItem(listener,holder.itemView.context, item.data as FeedRestaurantItemSeeMore)
            }
        }
    }

    class FeedDishViewHolder(val binding: FeedAdapterRestaurantDishItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val layout: ConstraintLayout = binding.feedRestaurantDishItem
        private val thumbnail: ImageView = binding.feedRestaurantDishItemImg
        private val name: TextView = binding.feedRestaurantItemName
        private val price: TextView = binding.feedRestaurantItemPrice
        private val tagView: DishTagsView = binding.feedRestaurantItemTags

        private val shimmer: Shimmer = Shimmer.AlphaHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
            .setDuration(1300) // how long the shimmering animation takes to do one full sweep
            .setBaseAlpha(0.7f) //the alpha of the underlying children
            .setHighlightAlpha(0.6f) // the shimmer alpha amount
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()

        // This is the placeholder for the imageView
        private val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(shimmer)
        }

        fun bindItem(listener: FeedRestaurantDishPagerAdapterListener, context: Context, dish: FeedRestaurantItemDish) {


            Glide.with(context).load(dish.thumbnail_url).placeholder(shimmerDrawable).into(thumbnail)
            name.text = dish.name
            price.text = dish.formatted_price

            //todo - remove this when Tag entity is provided by server
            val tags = listOf<Tag>(Tag(0, "Vegan"), Tag(1, "sababa achi its gooos and looooooks wellll"), Tag(2, "Kosher"))
            tagView.initTagView(tags)
//            tagView.initTagView(dish.tags)
            binding.feedRestaurantItemView.setOnClickListener(){
                listener.onPageClick()
            }
        }


    }

    class FeedDishSeeMoreViewHolder(val binding: FeedAdapterRestaurantSeeMoreItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val thumbnail: ImageView = binding.feedRestaurantSeeMoreItemImg
        private val quantityLeft: TextView = binding.feedRestaurantSeeMoreItemQuantityLeft
//        private val price: TextView = binding.feedRestaurantSeeMoreItemPrice

        fun bindItem(listener: FeedRestaurantDishPagerAdapterListener, context: Context, dish: FeedRestaurantItemSeeMore) {
            quantityLeft.text = dish.title
//            price.text = dish.formatted_price
            Glide.with(context)
                .asBitmap()
                .load(dish.thumbnail_url)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        Dali.create(context).load(resource).into(thumbnail)
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
            binding.feedRestaurantItemView.setOnClickListener(){
                listener.onPageClick()
            }
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