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
import com.bupp.wood_spoon_eaters.views.ResizableTagsView
import com.bupp.wood_spoon_eaters.views.dish_tags_view.DishTagsView
import jp.wasabeef.glide.transformations.BlurTransformation


class FeedRestaurantDishPagerAdapter(val listener: FeedRestaurantDishPagerAdapterListener) :
    ListAdapter<FeedRestaurantSectionItem, RecyclerView.ViewHolder>(DiffCallback()) {

private var parentItemPosition: Int = -1
private var parentItemId: Long? = null
    interface FeedRestaurantDishPagerAdapterListener {
        fun onPageClick(itemLocalId: Long?)
    }

    @JvmName("setChefId1")
    fun setParentItemPosition(position: Int) {
        Log.d("feedItemPosition", "setParentItemPosition: $position")
        this.parentItemPosition = position
    }

    fun setItemLocalId(id: Long?) {
        Log.d("feedItemPosition", "setItemLocalId: $id")
        this.parentItemId = id
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type!!.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
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
                holder.bindItem(listener, holder.itemView.context, item.data as FeedRestaurantItemDish, parentItemId)
            }
            is FeedRestaurantItemSeeMore -> {
                holder as FeedDishSeeMoreViewHolder
                holder.bindItem(listener, holder.itemView.context, item.data as FeedRestaurantItemSeeMore, parentItemId)
            }
        }
    }



    class FeedDishViewHolder(val binding: FeedAdapterRestaurantDishItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val layout: ConstraintLayout = binding.feedRestaurantDishItem
        private val thumbnail: ImageView = binding.feedRestaurantDishItemImg
        private val name: TextView = binding.feedRestaurantItemName
        private val price: TextView = binding.feedRestaurantItemPrice
        private val tagView: ResizableTagsView = binding.feedRestaurantItemTags

        fun bindItem(listener: FeedRestaurantDishPagerAdapterListener, context: Context, dish: FeedRestaurantItemDish, parentItemId: Long?) {
            Log.d("feedItemPosition", "bindItem: $parentItemId")
//            dish.thumbnailHash?.let{
//                GlideApp.with(context).load(dish.thumbnail_url)
//                    .blurPlaceHolder(it, thumbnail, blurHash)
//                    { requestBuilder ->
//                        requestBuilder.into(thumbnail)
//                    }
//            }
            GlideApp.with(context).load(dish.thumbnail?.url).thumbnail(0.1f).placeholder(R.drawable.grey_white_cornered_rect).into(thumbnail)
            name.text = dish.name
            price.text = dish.formatted_price

            tagView.setTags(dish.tags)

            layout.setOnClickListener{
                Log.d("feedItemPosition", "parentItemPosition: $parentItemId")
                listener.onPageClick(parentItemId)
            }
        }


    }

    class FeedDishSeeMoreViewHolder(val binding: FeedAdapterRestaurantSeeMoreItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val layout: ConstraintLayout = binding.feedRestaurantSeeMoreItemLayout
        private val thumbnail: ImageView = binding.feedRestaurantSeeMoreItemImg
        private val quantityLeft: TextView = binding.feedRestaurantSeeMoreItemQuantityLeft

        fun bindItem(listener: FeedRestaurantDishPagerAdapterListener, context: Context, dish: FeedRestaurantItemSeeMore, parentItemId: Long?) {
            quantityLeft.text = dish.title
            val multiTransformation = MultiTransformation(BlurTransformation( 10, 2), CenterCrop())
            GlideApp.with(context)
                .load(dish.thumbnail?.url)
                .thumbnail(0.1f)
                .apply(RequestOptions.bitmapTransform(multiTransformation))
                .placeholder(R.drawable.grey_white_cornered_rect).into(thumbnail)

            layout.setOnClickListener{
                listener.onPageClick(parentItemId)
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