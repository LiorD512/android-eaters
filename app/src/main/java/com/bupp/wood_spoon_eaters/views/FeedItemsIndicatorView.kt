package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FeedItemIndicatorEmptyItemBinding
import com.bupp.wood_spoon_eaters.databinding.FeedItemsIndicatorViewBinding
import com.bupp.wood_spoon_eaters.databinding.WsFeaturePbBinding
import com.bupp.wood_spoon_eaters.views.feature_pb.WSSingleFeatureProgressBar
import com.google.android.material.tabs.TabLayoutMediator


class FeedItemsIndicatorView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: FeedItemsIndicatorViewBinding = FeedItemsIndicatorViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun initDishIndicator(itemsCount: Int, viewPager: ViewPager2){
        TabLayoutMediator(binding.feedItemsIndicatorTab, viewPager) { tab, position ->
            tab.setCustomView(R.layout.feed_item_indicator_empty_item)
        }.attach()

    }

    companion object{
        const val TAG = "wowFeedItemsIndicatorView"
    }

}
