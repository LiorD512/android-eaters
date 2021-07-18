package com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import kotlin.math.abs

class FeedCampaignCouponItemTransformer() : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        Log.d("wowFeedCampaignTransformer", "position: $position")
        page.apply {

            val titleView: TextView = findViewById(R.id.customBannerTitle)
            val subTitleView: TextView = findViewById(R.id.customBannerSubTitle)
            if (position <= -1.0f || position >= 1.0f) {
                titleView.alpha = 0.5f
                subTitleView.alpha = 0.5f
            } else if (position == 0.5f) {
                titleView.alpha = 1.0f
                subTitleView.alpha = 1.0f
            }
            titleView.alpha = 1.0f - abs(position)
            subTitleView.alpha = 1.0f - abs(position)

            titleView.translationX = 1.0f + (abs(position) * 600)
            subTitleView.translationX = 1.0f + (abs(position) * 750)
        }
    }
}