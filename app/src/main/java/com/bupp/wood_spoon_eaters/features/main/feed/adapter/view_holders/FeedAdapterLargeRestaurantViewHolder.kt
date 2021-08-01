package com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterBigRestaurantItemBinding
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantItemBinding
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.AnimationUtil


class FeedAdapterLargeRestaurantViewHolder(val context: Context, val binding: FeedAdapterBigRestaurantItemBinding) : RecyclerView.ViewHolder(binding.root) {

    interface FeedAdapterRestaurantViewHolderListener{
        fun onRestaurantClick(cook: Cook)
    }

    fun bindItems(restaurantSection: FeedAdapterLargeRestaurant, listener: FeedAdapterRestaurantViewHolderListener) {
        Log.d("wowFeedPager", "bindItems - ${restaurantSection.restaurantSection.restaurantName}")
        restaurantSection.restaurantSection.let { restaurant ->
            with(binding) {
                Glide.with(context).load(restaurant.chefThumbnailUrl).circleCrop().into(feedRestaurantItemChefImage)
                feedRestaurantItemRestaurantName.text = restaurant.restaurantName
                feedRestaurantItemChefName.text = "By ${restaurant.chefName}"
                feedRestaurantItemRating.text = restaurant.avgRating

                restaurant.items?.let {
                    root.setOnClickListener{
                        listener.onRestaurantClick(restaurant.getCook())
                    }
                    val pagerListener = object: FeedRestaurantDishPagerAdapter.FeedRestaurantDishPagerAdapterListener{
                        override fun onPageClick() {
                            listener.onRestaurantClick(restaurant.getCook())
                        }
                    }
                    val adapter = FeedRestaurantDishPagerAdapter(pagerListener)
                    binding.feedRestaurantItemPager.adapter = adapter
                    binding.feedRestaurantItemPager.setPageTransformer(FeedRestaurantDishItemTransformer())
                    adapter.submitList(it)

                    if (it.size > 1) {
                        binding.feedRestaurantItemIndicator.visibility = View.VISIBLE
                        binding.feedRestaurantItemIndicator.initDishIndicator(it.size)
//                        binding.feedRestaurantItemIndicator.initDishIndicator(it.size, binding.feedRestaurantItemPager)

                    }

                    binding.feedRestaurantItemPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            Log.d("wowFeedPager", "onPageSelected - position: $position")
                            binding.feedRestaurantItemIndicator.onTabChanged(position)
                            when (position) {
                                0 -> {
                                    if (it.size > 1) {
                                        AnimationUtil().alphaIn(binding.feedRestaurantItemBtnNext)
                                    }
                                    AnimationUtil().alphaOut(binding.feedRestaurantItemBtnPrevious)
                                }
                                adapter.itemCount-1 -> {
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
