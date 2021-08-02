package com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterBigRestaurantItemBinding
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
                        override fun onPageClick(position: Int) {
                            listener.onRestaurantClick(restaurant.getCook())
                        }
                    }
                    val adapter = FeedRestaurantDishPagerAdapter(pagerListener)
                    binding.feedRestaurantItemList.adapter = adapter
                    binding.feedRestaurantItemList.setPageTransformer(FeedRestaurantDishItemTransformer())
                    adapter.submitList(it)


                    binding.feedRestaurantItemList.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
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
                    val curPosition = binding.feedRestaurantItemList.currentItem
                    binding.feedRestaurantItemList.currentItem = curPosition +1
                }
                binding.feedRestaurantItemBtnPrevious.setOnClickListener {
                    val curPosition = binding.feedRestaurantItemList.currentItem
                    binding.feedRestaurantItemList.currentItem = curPosition -1
                }
            }
        }
    }
}
