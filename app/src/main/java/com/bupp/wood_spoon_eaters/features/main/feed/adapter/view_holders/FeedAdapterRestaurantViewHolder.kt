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
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.SnapOnScrollListener
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.attachSnapHelperWithListener
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantItemBinding
import com.bupp.wood_spoon_eaters.model.FeedAdapterRestaurant
import com.bupp.wood_spoon_eaters.model.FeedRestaurantItemDish
import com.bupp.wood_spoon_eaters.model.FeedRestaurantItemSeeMore
import com.bupp.wood_spoon_eaters.model.FeedRestaurantSectionItem
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.views.ws_range_time_picker.WSRangeTimePickerDateAdapter

class FeedAdapterRestaurantViewHolder(val context: Context, val binding: FeedAdapterRestaurantItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(restaurantSection: FeedAdapterRestaurant) {
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
                        binding.feedRestaurantItemIndicator.setViewPager(binding.feedRestaurantItemPager)

                        AnimationUtil().alphaIn(binding.feedRestaurantItemBtnNext)
                    }

//                    initItemData(0, it)

//                    binding.feedRestaurantItemPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//                        override fun onPageSelected(position: Int) {
//                            super.onPageSelected(position)
//                            Log.d("wowFeedPager", "onPageSelected - position: $position")
//                            initItemData(position, it, true)
//
//                            when (position) {
//                                0 -> {
//                                    AnimationUtil().alphaOut(binding.feedRestaurantItemBtnPrevious)
//                                }
//                                adapter.itemCount-1 -> {
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

    private fun initItemData(position: Int, data: List<FeedRestaurantSectionItem>, animate: Boolean = false) {
        when (data[position].data) {
            is FeedRestaurantItemDish -> {
                val currentDish = data[position].data as FeedRestaurantItemDish
                binding.feedRestaurantItemDishName.text = currentDish.name
                binding.feedRestaurantItemDishPrice.text = currentDish.formatted_price

                if(animate){
                    binding.feedRestaurantItemDishSeeMore.alpha = 0f
                    AnimationUtil().alphaIn(binding.feedRestaurantItemDishName)
                    AnimationUtil().alphaIn(binding.feedRestaurantItemDishPrice)
                }
            }
            is FeedRestaurantItemSeeMore -> {
                val currentDish = data[position].data as FeedRestaurantItemSeeMore
                binding.feedRestaurantItemDishName.text = currentDish.title
                binding.feedRestaurantItemDishPrice.text = currentDish.formatted_price

                if(animate){
                    AnimationUtil().alphaIn(binding.feedRestaurantItemDishName)
                    AnimationUtil().alphaIn(binding.feedRestaurantItemDishPrice)
                    AnimationUtil().alphaIn(binding.feedRestaurantItemDishSeeMore)
                }
            }
        }
    }


}

