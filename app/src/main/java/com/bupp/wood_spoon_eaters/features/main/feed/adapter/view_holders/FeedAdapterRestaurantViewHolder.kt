package com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.SnapOnScrollListener
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.attachSnapHelperWithListener
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantItemBinding
import com.bupp.wood_spoon_eaters.model.FeedAdapterRestaurant
import com.bupp.wood_spoon_eaters.views.ws_range_time_picker.WSRangeTimePickerDateAdapter

class FeedAdapterRestaurantViewHolder(val context: Context, val binding: FeedAdapterRestaurantItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(restaurantSection: FeedAdapterRestaurant) {
        restaurantSection.restaurantSection.let { restaurant ->
            with(binding){
                Glide.with(context).load(restaurant.chefThumbnailUrl).circleCrop().into(feedRestaurantItemChefImage)
                feedRestaurantItemRestaurantName.text = restaurant.restaurantName
                feedRestaurantItemChefName.text = "By ${restaurant.chefName}"
                feedRestaurantRating.text = restaurant.avgRating

//                val sidhAdapter = wsRangeTimePickerDateAdapter = FeedReDi()
//                binding.wsRangeTimePickerDateList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//                binding.wsRangeTimePickerDateList.adapter = wsRangeTimePickerDateAdapter
//
//                val snapHelper: SnapHelper = LinearSnapHelper()
//                binding.wsRangeTimePickerDateList.attachSnapHelperWithListener(
//                    snapHelper,
//                    SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
//                    this@WSRangeTimePicker
//                )

            }
        }



    }
}

