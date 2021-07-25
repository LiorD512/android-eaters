package com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.SnapOnScrollListener
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.attachSnapHelperWithListener
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantItemBinding
import com.bupp.wood_spoon_eaters.model.FeedAdapterRestaurant
import com.bupp.wood_spoon_eaters.model.FeedRestaurantItemDish
import com.bupp.wood_spoon_eaters.model.FeedRestaurantItemSeeMore
import com.bupp.wood_spoon_eaters.model.FeedRestaurantSectionItem
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.views.ws_range_time_picker.WSRangeTimePickerDateAdapter
import com.google.android.material.tabs.TabLayoutMediator

class FeedAdapterRestaurantViewHolder(val context: Context, val binding: FeedAdapterRestaurantItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(restaurantSection: FeedAdapterRestaurant) {
        Log.d("wowFeedPager", "bindItems - ${restaurantSection.restaurantSection.restaurantName}")
        restaurantSection.restaurantSection.let { restaurant ->
            with(binding) {
                Glide.with(context).load(restaurant.chefThumbnailUrl).circleCrop().into(feedRestaurantItemChefImage)
                feedRestaurantItemRestaurantName.text = restaurant.restaurantName
                feedRestaurantItemChefName.text = "By ${restaurant.chefName}"
                feedRestaurantItemRating.text = restaurant.avgRating

                restaurant.items?.let {
                    val adapter = FeedRestaurantDishPagerAdapter()
                    binding.feedRestaurantItemPager.adapter = adapter
                    binding.feedRestaurantItemPager.setPageTransformer(FeedRestaurantDishItemTransformer())
                    adapter.submitList(it)

                    if (it.size > 1) {
                        binding.feedRestaurantItemIndicator.visibility = View.VISIBLE
                        binding.feedRestaurantItemIndicator.initDishIndicator(it.size, binding.feedRestaurantItemPager)

                        AnimationUtil().alphaIn(binding.feedRestaurantItemBtnNext)
                    }

                    binding.feedRestaurantItemPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            Log.d("wowFeedPager", "onPageSelected - position: $position")
                            when (position) {
                                0 -> {
                                    AnimationUtil().alphaOut(binding.feedRestaurantItemBtnPrevious)
                                }
                                adapter.itemCount-1 -> {
                                    AnimationUtil().alphaOut(binding.feedRestaurantItemBtnNext)
                                }
                                else -> {
                                    AnimationUtil().alphaIn(binding.feedRestaurantItemBtnPrevious)
                                    AnimationUtil().alphaIn(binding.feedRestaurantItemBtnNext)
                                }
                            }
                        }
                    })
                }

                binding.feedRestaurantItemBtnNext.setOnClickListener {
                    val curPosition = binding.feedRestaurantItemPager.currentItem
                    binding.feedRestaurantItemPager.currentItem = curPosition +1
                }
                binding.feedRestaurantItemBtnPrevious.setOnClickListener {
                    val curPosition = binding.feedRestaurantItemPager.currentItem
                    binding.feedRestaurantItemPager.currentItem = curPosition -1
                }
            }
        }
    }



}

