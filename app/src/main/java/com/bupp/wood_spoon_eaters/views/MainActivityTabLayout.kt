package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.MainActTabLayoutBinding
import com.google.android.material.tabs.TabLayoutMediator


class MainActivityTabLayout @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: MainActTabLayoutBinding = MainActTabLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private val imageResId = arrayOf(
        R.drawable.tab_home_selector,
        R.drawable.tab_search_selector,
        R.drawable.tab_orders_selector,
        R.drawable.tab_account_selector
    )

    private val texts = arrayOf("Home", "Search", "Orders", "Account")

    fun setViewPager(viewPager: ViewPager2){
        TabLayoutMediator(binding.mainActTabLayout, viewPager) { tab, position ->
            when(position){
                0 -> tab.setCustomView(R.layout.feed_tab_home)
                1 -> tab.setCustomView(R.layout.feed_tab_search)
                2 -> tab.setCustomView(R.layout.feed_tab_orders)
                3 -> tab.setCustomView(R.layout.feed_tab_account)
            }
        }.attach()
    }

    fun handleTabGestures(forceLock: Boolean){
        val tabStrip = binding.mainActTabLayout.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).setOnTouchListener { v, event -> forceLock }
        }
    }


}