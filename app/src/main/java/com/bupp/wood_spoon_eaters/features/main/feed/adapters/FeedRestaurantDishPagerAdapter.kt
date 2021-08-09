package com.bupp.wood_spoon_eaters.features.main.feed.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantDishItemBinding
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantSeeMoreItemBinding
import com.bupp.wood_spoon_eaters.di.GlideApp
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.views.dish_tags_view.DishTagsView
import jp.wasabeef.glide.transformations.BlurTransformation


class FeedRestaurantDishPagerAdapter(val listener: FeedRestaurantDishPagerAdapterListener) :
    ListAdapter<FeedRestaurantSectionItem, RecyclerView.ViewHolder>(DiffCallback()) {

//    val blurHash: BlurHash = BlurHash(context, lruSize = 20, punch = 1F)
private var parentItemPosition: Int = -1
    interface FeedRestaurantDishPagerAdapterListener {
        fun onPageClick(position: Int)
    }

    @JvmName("setChefId1")
    fun setParentItemPosition(position: Int) {
        this.parentItemPosition = position
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type!!.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FeedRestaurantSectionItemViewType.DISH.ordinal -> {
                val binding = FeedAdapterRestaurantDishItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedDishViewHolder(binding, parentItemPosition)
            }
            else -> { //FeedRestaurantSectionItemViewType.SEE_MORE
                val binding = FeedAdapterRestaurantSeeMoreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedDishSeeMoreViewHolder(binding, parentItemPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item.data) {
            is FeedRestaurantItemDish -> {
                holder as FeedDishViewHolder
                holder.bindItem(listener, holder.itemView.context, item.data as FeedRestaurantItemDish)
            }
            is FeedRestaurantItemSeeMore -> {
                holder as FeedDishSeeMoreViewHolder
                holder.bindItem(listener, holder.itemView.context, item.data as FeedRestaurantItemSeeMore)
            }
        }
    }



    class FeedDishViewHolder(val binding: FeedAdapterRestaurantDishItemBinding, private val parentItemPosition: Int) : RecyclerView.ViewHolder(binding.root) {
        private val layout: ConstraintLayout = binding.feedRestaurantDishItem
        private val thumbnail: ImageView = binding.feedRestaurantDishItemImg
        private val name: TextView = binding.feedRestaurantItemName
        private val price: TextView = binding.feedRestaurantItemPrice
        private val tagView: DishTagsView = binding.feedRestaurantItemTags

        fun bindItem(listener: FeedRestaurantDishPagerAdapterListener, context: Context, dish: FeedRestaurantItemDish) {

//            dish.thumbnailHash?.let{
//                GlideApp.with(context).load(dish.thumbnail_url)
//                    .blurPlaceHolder(it, thumbnail, blurHash)
//                    { requestBuilder ->
//                        requestBuilder.into(thumbnail)
//                    }
//            }
            GlideApp.with(context).load(dish.thumbnail_url).thumbnail(0.1f).placeholder(R.drawable.grey_white_cornered_rect).into(thumbnail)
            name.text = dish.name
            price.text = dish.formatted_price

            tagView.initTagView(dish.tags)

            layout.setOnClickListener{
                Log.d("wowFeedPager", "parentItemPosition: $parentItemPosition")
                listener.onPageClick(parentItemPosition)
            }
        }


    }

    class FeedDishSeeMoreViewHolder(val binding: FeedAdapterRestaurantSeeMoreItemBinding, private val parentItemPosition: Int) : RecyclerView.ViewHolder(binding.root) {
        private val layout: ConstraintLayout = binding.feedRestaurantSeeMoreItemLayout
        private val thumbnail: ImageView = binding.feedRestaurantSeeMoreItemImg
        private val quantityLeft: TextView = binding.feedRestaurantSeeMoreItemQuantityLeft

        fun bindItem(listener: FeedRestaurantDishPagerAdapterListener, context: Context, dish: FeedRestaurantItemSeeMore) {
            quantityLeft.text = dish.title
            val multiTransformation = MultiTransformation(BlurTransformation( 10, 2), CenterCrop())
            GlideApp.with(context)
                .load(dish.thumbnail_url)
                .thumbnail(0.1f)
                .apply(RequestOptions.bitmapTransform(multiTransformation))
                .placeholder(R.drawable.grey_white_cornered_rect).into(thumbnail)

            layout.setOnClickListener{
                listener.onPageClick(parentItemPosition)
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