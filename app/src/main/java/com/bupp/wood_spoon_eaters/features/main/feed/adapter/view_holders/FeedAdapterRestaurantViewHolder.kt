package com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.SnapOnScrollListener
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.attachSnapHelperWithListener
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantItemBinding
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper


class FeedAdapterRestaurantViewHolder(val context: Context, val binding: FeedAdapterRestaurantItemBinding, val adapter: FeedRestaurantDishPagerAdapter, val snapHelper: GravitySnapHelper) :
    RecyclerView.ViewHolder(binding.root) {

    interface FeedAdapterRestaurantViewHolderListener {
        fun onRestaurantClick(cook: Cook)
    }

    fun bindItems(restaurantSection: FeedAdapterRestaurant, listener: FeedAdapterRestaurantViewHolderListener, parentAdapterPosition: Int) {
        Log.d("wowFeedPager", "bindItems - ${restaurantSection.restaurantSection.restaurantName}")
        restaurantSection.restaurantSection.let { restaurant ->
            with(binding) {
                Glide.with(context).load(restaurant.chefThumbnailUrl).circleCrop().into(feedRestaurantItemChefImage)
                feedRestaurantItemRestaurantName.text = restaurant.restaurantName
                feedRestaurantItemChefName.text = "By ${restaurant.chefName}"
                feedRestaurantItemRating.text = restaurant.avgRating

                adapter.setParentItemPosition(parentAdapterPosition)

                restaurant.items?.let {
                    root.setOnClickListener {
                        Log.d("wowFeedPager", "onRestaurantClick")
                        listener.onRestaurantClick(restaurant.getCook())
                    }
                    binding.feedRestaurantItemList.attachSnapHelperWithListener(snapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
                        object : SnapOnScrollListener.OnSnapPositionChangeListener {
                            override fun onSnapPositionChange(position: Int) {
                                when (position) {
                                    0 -> {
                                        AnimationUtil().alphaOut(binding.feedRestaurantItemBtnPrevious)
                                        if (it.size > 1) {
                                            AnimationUtil().alphaIn(binding.feedRestaurantItemBtnNext)
                                        }
                                    }
                                    adapter.itemCount - 1 -> {
                                        AnimationUtil().alphaIn(binding.feedRestaurantItemBtnPrevious)
                                        AnimationUtil().alphaOut(binding.feedRestaurantItemBtnNext)
                                    }
                                    else -> {
                                        AnimationUtil().alphaIn(binding.feedRestaurantItemBtnPrevious)
                                        AnimationUtil().alphaIn(binding.feedRestaurantItemBtnNext)
                                    }
                                }
                            }

                        })
                    adapter.submitList(it)
                }

                binding.feedRestaurantItemBtnNext.setOnClickListener {
                    val curPosition = snapHelper.currentSnappedPosition
                    binding.feedRestaurantItemList.smoothScrollToPosition(curPosition +1)
                }
                binding.feedRestaurantItemBtnPrevious.setOnClickListener {
                    var curPosition = snapHelper.currentSnappedPosition
                    if(curPosition == NO_POSITION){
                        curPosition = adapter.itemCount - 1
                    }
                    if(curPosition >= 1)
                        binding.feedRestaurantItemList.smoothScrollToPosition(curPosition-1)
                }
            }
        }
    }


}
