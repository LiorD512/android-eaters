package com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.BlueBtnCornered
import kotlin.math.abs

class FeedRestaurantDishItemTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.apply {

            val titleView: TextView
            var subTitleView: TextView? = null
            var seeMoreView: BlueBtnCornered? = null
            if(page.id == R.id.feedRestaurantDishItem){
                //this is a dish item
                titleView = findViewById(R.id.feedRestaurantItemName)
                subTitleView = findViewById(R.id.feedRestaurantItemPrice)
            }else{
                //this is a see more item
                titleView = findViewById(R.id.feedRestaurantSeeMoreItemQuantityLeft)
//                subTitleView = findViewById(R.id.feedRestaurantSeeMoreItemPrice)
                seeMoreView = findViewById(R.id.feedRestaurantSeeMoreItemSeeMore)
            }
            titleView.alpha = 1.0f - abs(position)
            subTitleView?.alpha = 1.0f - abs(position)
            seeMoreView?.alpha = 1.0f - (abs(position) / 0.2).toFloat()

            titleView.translationX = 1.0f + (abs(position) * 600)
        }
    }
}