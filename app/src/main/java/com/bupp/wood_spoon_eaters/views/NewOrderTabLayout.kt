package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.NewOrderTabLayoutBinding
import com.google.android.material.tabs.TabLayoutMediator


class NewOrderTabLayout @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: NewOrderTabLayoutBinding = NewOrderTabLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private val imageResId = arrayOf(
        R.drawable.dish_tab_info_selector,
        R.drawable.dish_tab_cook_selector,
        R.drawable.dish_tab_ingredients_selector
    )

    private val texts = arrayOf("info", "Ingredients", "Home Chef")

    fun setViewPager(viewPager: ViewPager2){
        TabLayoutMediator(binding.newOrderTabLayout, viewPager) { tab, position ->
            tab.text = texts[position]
            tab.icon = ContextCompat.getDrawable(context, imageResId[position])
        }.attach()
    }

    fun handleTabGestures(forceLock: Boolean){
        val tabStrip = binding.newOrderTabLayout.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).setOnTouchListener { v, event -> forceLock }
        }
    }


}