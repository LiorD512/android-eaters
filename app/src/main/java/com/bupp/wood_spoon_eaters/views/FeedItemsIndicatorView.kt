package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FeedItemsIndicatorViewBinding
import com.google.android.material.tabs.TabLayout


class FeedItemsIndicatorView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: FeedItemsIndicatorViewBinding = FeedItemsIndicatorViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun initDishIndicator(itemsCount: Int){
        binding.feedItemsIndicatorTab.removeAllTabs()
        for (i in 0 until itemsCount) {
            binding.feedItemsIndicatorTab.addTab(binding.feedItemsIndicatorTab.newTab())
            binding.feedItemsIndicatorTab.getTabAt(i)?.setCustomView(R.layout.feed_item_indicator_empty_item)
        }
    }

    fun onTabChanged(position: Int){
        Log.d(TAG, "onTabChanged: $position")
        val tab: TabLayout.Tab? = binding.feedItemsIndicatorTab.getTabAt(position)
        tab?.select()
    }


    companion object{
        const val TAG = "wowFeedItemsIndicator"
    }

}
