package com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.bupp.wood_spoon_eaters.R
import kotlin.math.abs

class FeedRestaurantDishItemTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.apply {

            val titleView: TextView
            val subTitleView: TextView
            if(page.id == R.id.feedRestaurantDishItem){
                //this is a dish item
                titleView = findViewById(R.id.feedRestaurantItemName)
                subTitleView = findViewById(R.id.feedRestaurantItemPrice)
            }else{
                //this is a see more item
                titleView = findViewById(R.id.feedRestaurantSeeMoreItemQuantityLeft)
                subTitleView = findViewById(R.id.feedRestaurantSeeMoreItemPrice)
            }
                if (position <= -1.0f || position >= 1.0f) {
    //                titleView.alpha = 0.5f
                    subTitleView.alpha = 0.5f
                } else if (position == 0.5f) {
                    titleView.alpha = 1.0f
                    subTitleView.alpha = 1.0f
                } else if (position < 0.5f) {
                    titleView.alpha = 1.0f - abs(position)
                    subTitleView.alpha = 1.0f - abs(position)

                    titleView.translationX = 1.0f + (abs(position) * 600)
                }
        }
    }
}