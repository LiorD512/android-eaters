package com.bupp.wood_spoon_eaters.features.main.feed.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
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
import jp.wasabeef.glide.transformations.BlurTransformation


class FeedRestaurantDishPagerAdapter(val listener: FeedRestaurantDishPagerAdapterListener) :
    ListAdapter<FeedRestaurantSectionItem, RecyclerView.ViewHolder>(DiffCallback()) {

private var parentItemPosition: Int = -1
private var parentItemId: Long? = null
private var isAvailable: Boolean? = null
    interface FeedRestaurantDishPagerAdapterListener {
        fun onPageClick(itemLocalId: Long?, position: Int, itemType: String)
    }

    @JvmName("setChefId1")
    fun setParentItemPosition(position: Int) {
        Log.d("wowProcessFeedData", "setParentItemPosition: $position")
        this.parentItemPosition = position
    }

    fun setItemLocalId(id: Long?, isAvailable: Boolean? = true) {
        Log.d("wowProcessFeedData", "setItemLocalId: $id")
        this.parentItemId = id
        this.isAvailable = isAvailable
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type!!.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FeedRestaurantSectionItemViewType.DISH.ordinal -> {
                val binding = FeedAdapterRestaurantDishItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedDishViewHolder(binding)
            }

            FeedRestaurantSectionItemViewType.COVER.ordinal -> {
                val binding = FeedAdapterRestaurantDishItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedCoverViewHolder(binding)
            }
            else -> {// FeedRestaurantSectionItemViewType.SEE_MORE.ordinal -> {
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
                holder.bindItem(holder.itemView.context, item.data as FeedRestaurantItemDish, parentItemId, isAvailable)

                holder.layout.setOnClickListener{
                    Log.d("wowProcessFeedData", "parentItemPosition: $parentItemId")
                    listener.onPageClick(parentItemId, position, "dish $position")

//                    val absPos = absoluteAdapterPosition
//                    val bindPos = bindingAdapterPosition
//                    val layPos = layoutPosition
//                    Log.d("wowAdapterPos", "absPos: $absPos bindPos $bindPos, layPos = $layPos, position: $position")
                }
            }
            is FeedRestaurantItemSeeMore -> {
                holder as FeedDishSeeMoreViewHolder
                holder.bindItem(listener, holder.itemView.context, item.data as FeedRestaurantItemSeeMore, parentItemId, position)
            }
            is FeedRestaurantItemCover -> {
                holder as FeedCoverViewHolder
                holder.bindItem(holder.itemView.context, item.data as FeedRestaurantItemCover, parentItemId, isAvailable)

                holder.layout.setOnClickListener{
                    Log.d("wowProcessFeedData", "parentItemPosition: $parentItemId")
                    listener.onPageClick(parentItemId, position, "chef_cover")

//                    val absPos = absoluteAdapterPosition
//                    val bindPos = bindingAdapterPosition
//                    val layPos = layoutPosition
//                    Log.d("wowAdapterPos", "absPos: $absPos bindPos $bindPos, layPos = $layPos, position: $position")
                }
            }
        }
    }



    class FeedDishViewHolder(val binding: FeedAdapterRestaurantDishItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val layout: ConstraintLayout = binding.feedRestaurantDishItem
        private val thumbnail: ImageView = binding.feedRestaurantDishItemImg
        private val name: TextView = binding.feedRestaurantItemName
        private val price: TextView = binding.feedRestaurantItemPrice
        private val tagView: ResizableTagsView = binding.feedRestaurantItemTags

        @SuppressLint("ClickableViewAccessibility")
        fun bindItem(
            context: Context,
            dish: FeedRestaurantItemDish,
            parentItemId: Long?,
            isAvailable: Boolean?
        ) {
            Log.d("wowProcessFeedData", "dishItem - bindItem: $parentItemId")
//            dish.thumbnailHash?.let{
//                GlideApp.with(context).load(dish.thumbnail_url)
//                    .blurPlaceHolder(it, thumbnail, blurHash)
//                    { requestBuilder ->
//                        requestBuilder.into(thumbnail)
//                    }
//            }
            GlideApp.with(context).load(dish.thumbnail?.url).thumbnail(0.1f).placeholder(R.drawable.grey_white_cornered_rect).into(thumbnail)
            if(isAvailable == true){
                name.text = dish.name
                price.text = dish.formatted_price

                tagView.setTags(dish.tags)
            }
            name.isVisible = isAvailable ?: true
            price.isVisible = isAvailable ?: true
            tagView.isVisible = isAvailable ?: true
        }
    }

    class FeedCoverViewHolder(val binding: FeedAdapterRestaurantDishItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val layout: ConstraintLayout = binding.feedRestaurantDishItem
        private val thumbnail: ImageView = binding.feedRestaurantDishItemImg
        private val name: TextView = binding.feedRestaurantItemName
        private val price: TextView = binding.feedRestaurantItemPrice
        private val tagView: ResizableTagsView = binding.feedRestaurantItemTags

        @SuppressLint("ClickableViewAccessibility")
        fun bindItem(
            context: Context,
            coverImage: FeedRestaurantItemCover,
            parentItemId: Long?,
            isAvailable: Boolean?
        ) {
            Log.d("wowProcessFeedData", "dishItem - bindItem: $parentItemId")
            GlideApp.with(context).load(coverImage.thumbnail?.url).thumbnail(0.1f).placeholder(R.drawable.grey_white_cornered_rect).into(thumbnail)
            name.isVisible = isAvailable ?: true
            price.isVisible = isAvailable ?: true
            tagView.isVisible = isAvailable ?: true
        }
    }

    class FeedDishSeeMoreViewHolder(val binding: FeedAdapterRestaurantSeeMoreItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val layout: ConstraintLayout = binding.feedRestaurantSeeMoreItemLayout
        private val thumbnail: ImageView = binding.feedRestaurantSeeMoreItemImg
        private val quantityLeft: TextView = binding.feedRestaurantSeeMoreItemQuantityLeft

        fun bindItem(listener: FeedRestaurantDishPagerAdapterListener, context: Context, dish: FeedRestaurantItemSeeMore, parentItemId: Long?, position: Int) {
            quantityLeft.text = dish.title
            val multiTransformation = MultiTransformation(BlurTransformation( 10, 2), CenterCrop())
            GlideApp.with(context)
                .load(dish.thumbnail?.url)
                .thumbnail(0.1f)
                .apply(RequestOptions.bitmapTransform(multiTransformation))
                .placeholder(R.drawable.grey_white_cornered_rect).into(thumbnail)

            layout.setOnClickListener{
                listener.onPageClick(parentItemId, position, "see_more")
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