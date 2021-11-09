package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil.DiffResult.NO_POSITION
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.SnapOnScrollListener
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.attachSnapHelperWithListener
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantItemBinding
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.FeedRestaurantDishPagerAdapter
import com.bupp.wood_spoon_eaters.model.FeedAdapterRestaurant
import com.bupp.wood_spoon_eaters.model.FeedRestaurantSection
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.utils.CountryCodeUtils
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.utils.Utils
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import java.util.*


class FeedAdapterRestaurantViewHolder(
    val context: Context,
    val binding: FeedAdapterRestaurantItemBinding,
    val adapter: FeedRestaurantDishPagerAdapter,
    val snapHelper: GravitySnapHelper
) :
    RecyclerView.ViewHolder(binding.root), FeedRestaurantDishPagerAdapter.FeedRestaurantDishPagerAdapterListener {

    interface FeedAdapterRestaurantViewHolderListener {
        fun onRestaurantClick(restaurant: FeedRestaurantSection)
        fun onDishSwiped()
    }

    @SuppressLint("SetTextI18n")
    fun bindItems(restaurantSection: FeedAdapterRestaurant, listener: FeedAdapterRestaurantViewHolderListener, parentAdapterPosition: Int) {
        restaurantSection.restaurantSection.let { restaurant ->
            Log.d("wowProcessFeedData", "bindItems - ${restaurantSection.restaurantSection.restaurantName} - pos ${restaurantSection.id}")
            with(binding) {
                Glide.with(context).load(restaurant.chefThumbnail?.url).circleCrop().into(feedRestaurantItemChefImage)
                Glide.with(context).load(restaurant.countryIso?.let { CountryCodeUtils.countryCodeToEmojiFlag(it) }).circleCrop().into(feedRestaurantItemChefFlag)
                feedRestaurantItemRestaurantName.text = restaurant.restaurantName
                feedRestaurantItemChefName.text = "By ${restaurant.chefName}"
                feedRestaurantItemRating.text = restaurant.avgRating.toString()
                feedRestaurantItemRating.isVisible = restaurant.avgRating ?: 0f > 0

//                adapter.setParentItemPosition(restaurantSection.id)
                adapter.setItemLocalId(restaurantSection.id)
//                Log.d("feedPosition", "restaurant.chefName - ${restaurant.chefName}")
//                Log.d("feedPosition", "parentAdapterPosition - $parentAdapterPosition")

                restaurant.items?.let {
                    binding.feedRestaurantItemList.attachSnapHelperWithListener(snapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
                        object : SnapOnScrollListener.OnSnapPositionChangeListener {
                            override fun onSnapPositionChange(position: Int) {
                                if (absoluteAdapterPosition == parentAdapterPosition){
                                    //todo - this method id been called for recycled items as well - need fix when have time
                                    handleArrows(position, it.size, binding)
//                                    listener.onDishSwiped()
                                }
                            }
                        })
                    adapter.submitList(it)
                }

                binding.feedRestaurantItemBtnNext.setOnClickListener {
                    val curPosition = snapHelper.currentSnappedPosition
                    binding.feedRestaurantItemList.smoothScrollToPosition(curPosition + 1)
                }
                binding.feedRestaurantItemBtnPrevious.setOnClickListener {
                    var curPosition = snapHelper.currentSnappedPosition
                    if (curPosition == NO_POSITION) {
                        curPosition = adapter.itemCount - 1
                    }
                    if (curPosition >= 1)
                        binding.feedRestaurantItemList.smoothScrollToPosition(curPosition - 1)
                }
                handleArrows(0, restaurant.items?.size, binding)

                root.setOnClickListener {
                    listener.onRestaurantClick(restaurant)
                }

                val curCookingSlot = restaurant.cookingSlot
                curCookingSlot?.let{
                    if(Date().after(it.startsAt)){
                        Log.d("wowFeedPager", "now is after starts_at")
                        // if now is after starts_at - availably now
                        binding.feedRestaurantItemAvailability.text = "Available now"
                    }else if(Date().before(it.startsAt) && DateUtils.isToday(it.startsAt)){
                        Log.d("wowFeedPager", "now is before starts_at")
                        // if now is before starts_at and in the same day - today MM:HH PM
                        binding.feedRestaurantItemAvailability.text = "Today, ${DateUtils.parseDateToUsTime(it.startsAt)}"
                    }else if(DateUtils.isTomorrow(it.startsAt)){
                        Log.d("wowFeedPager", "cooking slot is tomotrrow")
                        // if cooking slot is tomotrrow - tomorrow MM:HH PM
                        binding.feedRestaurantItemAvailability.text = "Tomorrow, ${DateUtils.parseDateToUsTime(it.startsAt)}"
                    }else if(DateUtils.isAfterTomorrow(it.startsAt)) {
                        Log.d("wowFeedPager", "after tomorrow")
                        // if after tomorrow - Tue, mm:hh PM
                        binding.feedRestaurantItemAvailability.text = DateUtils.parseDateToDayAndUsTime(it.startsAt)
                    }else{
                        // if after a week from now - Nov 10, 6:40 pm
                        Log.d("wowFeedPager", "after a week from now")
                        binding.feedRestaurantItemAvailability.text = DateUtils.parseDateToMonthTime(it.startsAt)
                    }
                }
            }
        }
    }

    private fun handleArrows(position: Int, maxItems: Int? = 0, binding: FeedAdapterRestaurantItemBinding) {
        Log.d("wowFeedPager", "handleArrows $position, $this")
        Log.d("wowFeedPager", "handleArrows itemCount - ${adapter.itemCount}")
        when (position) {
            0 -> {
                Log.d("wowFeedPager", "first pos")
                AnimationUtil().alphaOut(binding.feedRestaurantItemBtnPrevious, customStartDelay = 150)
                if (maxItems!! > 1) {
                    AnimationUtil().alphaIn(binding.feedRestaurantItemBtnNext)
                } else {
                    binding.feedRestaurantItemBtnNext.alpha = 0f
                }
            }
            adapter.itemCount - 1 -> { //last page
                Log.d("wowFeedPager", "last pos")
                AnimationUtil().alphaIn(binding.feedRestaurantItemBtnPrevious)
                AnimationUtil().alphaOut(binding.feedRestaurantItemBtnNext, customStartDelay = 150)
            }
            else -> {
                Log.d("wowFeedPager", "middle pos")
                AnimationUtil().alphaIn(binding.feedRestaurantItemBtnPrevious)
                AnimationUtil().alphaIn(binding.feedRestaurantItemBtnNext)
            }
        }
    }

//    override fun onPageClick(position: Int) {
//
//    }

    override fun onPageClick(itemLocalId: Long?, position: Int) {
    }
}