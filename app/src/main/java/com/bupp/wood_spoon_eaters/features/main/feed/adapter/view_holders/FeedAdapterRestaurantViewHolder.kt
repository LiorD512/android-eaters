package com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.SnapOnScrollListener
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.attachSnapHelperWithListener
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterBigRestaurantItemBinding
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantItemBinding
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.views.abs.RecyclerHorizontalIndicatorDecoration
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper


class FeedAdapterRestaurantViewHolder(val context: Context, val binding: FeedAdapterRestaurantItemBinding, val adapter: FeedRestaurantDishPagerAdapter, val snapHelper: GravitySnapHelper) :
    RecyclerView.ViewHolder(binding.root),
    SnapOnScrollListener.OnSnapPositionChangeListener {

    interface FeedAdapterRestaurantViewHolderListener {
        fun onRestaurantClick(cook: Cook)
    }

    fun bindItems(restaurantSection: FeedAdapterRestaurant, listener: FeedAdapterRestaurantViewHolderListener) {
        Log.d("wowFeedPager", "bindItems - ${restaurantSection.restaurantSection.restaurantName}")
        restaurantSection.restaurantSection.let { restaurant ->
            with(binding) {
                Glide.with(context).load(restaurant.chefThumbnailUrl).circleCrop().into(feedRestaurantItemChefImage)
                feedRestaurantItemRestaurantName.text = restaurant.restaurantName
                feedRestaurantItemChefName.text = "By ${restaurant.chefName}"
                feedRestaurantItemRating.text = restaurant.avgRating

                restaurant.items?.let {
                    root.setOnClickListener {
                        Log.d("wowFeedPager", "onRestaurantClick")
                        listener.onRestaurantClick(restaurant.getCook())
                    }
                    binding.feedRestaurantItemPager.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                    binding.feedRestaurantItemPager.addItemDecoration(FeedAdapterDishItemDecorator())
                    binding.feedRestaurantItemPager.addItemDecoration(RecyclerHorizontalIndicatorDecoration())
                    binding.feedRestaurantItemPager.adapter = adapter
                    binding.feedRestaurantItemPager.attachSnapHelperWithListener(snapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
                        object : SnapOnScrollListener.OnSnapPositionChangeListener {
                            override fun onSnapPositionChange(position: Int) {
//                                binding.feedRestaurantItemIndicator.onTabChanged(position)
                                when (position) {
                                    0 -> {
                                        if (it.size > 1) {
                                            AnimationUtil().alphaIn(binding.feedRestaurantItemBtnNext)
                                        }
                                        AnimationUtil().alphaOut(binding.feedRestaurantItemBtnPrevious)
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

//                    if (it.size > 1) {
//                        binding.feedRestaurantItemIndicator.visibility = View.VISIBLE
//                        binding.feedRestaurantItemIndicator.initDishIndicator(it.size)
//
//                    }

//                    binding.feedRestaurantItemPager.isUserInputEnabled = false
//
//                    binding.feedRestaurantItemPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//                        override fun onPageSelected(position: Int) {
//                            super.onPageSelected(position)
//                            Log.d("wowFeedPager", "onPageSelected - position: $position")
//                            when (position) {
//                                0 -> {
//                                    if (it.size > 1) {
//                                        AnimationUtil().alphaIn(binding.feedRestaurantItemBtnNext)
//                                    }
//                                    AnimationUtil().alphaOut(binding.feedRestaurantItemBtnPrevious)
//                                }
//                                adapter.itemCount-1 -> {
//                                    AnimationUtil().alphaIn(binding.feedRestaurantItemBtnPrevious)
//                                    AnimationUtil().alphaOut(binding.feedRestaurantItemBtnNext)
//                                }
//                                else -> {
//                                    AnimationUtil().alphaIn(binding.feedRestaurantItemBtnPrevious)
//                                    AnimationUtil().alphaIn(binding.feedRestaurantItemBtnNext)
//                                }
//                            }
//                        }
//                    })
                }

//                binding.feedRestaurantItemBtnNext.setOnClickListener {
//                    val curPosition = binding.feedRestaurantItemPager.currentItem
//                    binding.feedRestaurantItemPager.currentItem = curPosition +1
//                }
//                binding.feedRestaurantItemBtnPrevious.setOnClickListener {
//                    val curPosition = binding.feedRestaurantItemPager.currentItem
//                    binding.feedRestaurantItemPager.currentItem = curPosition -1
//                }
            }
        }
    }

    override fun onSnapPositionChange(position: Int) {

    }
}
