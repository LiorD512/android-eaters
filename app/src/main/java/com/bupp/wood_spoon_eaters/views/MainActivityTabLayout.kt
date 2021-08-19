package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.MainActTabLayoutBinding
import com.bupp.wood_spoon_eaters.utils.waitForLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivityTabLayout @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: MainActTabLayoutBinding = MainActTabLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    fun setViewPager(viewPager: ViewPager2){
        TabLayoutMediator(binding.mainActTabLayout, viewPager, true, false) { tab, position ->
            when(position){
                0 -> tab.setCustomView(R.layout.feed_tab_home)
//                1 -> tab.setCustomView(R.layout.feed_tab_search)
                1 -> tab.setCustomView(R.layout.feed_tab_orders)
                2 -> tab.setCustomView(R.layout.feed_tab_account)
            }
        }.attach()
    }

    fun handleOrdersIndicator(shouldShow: Boolean){
        binding.mainActTabLayout.waitForLayout {
            val tabStrip = binding.mainActTabLayout.getTabAt(1)?.customView
            val indicator = tabStrip?.findViewById<View>(R.id.indicator)
            indicator?.isVisible = shouldShow
        }
    }

//    fun handleTabGestures(forceLock: Boolean){
//        val tabStrip = binding.mainActTabLayout.getChildAt(0) as LinearLayout
//        for (i in 0 until tabStrip.childCount) {
//            tabStrip.getChildAt(i).setOnTouchListener { v, event -> forceLock }
//        }
//    }




}