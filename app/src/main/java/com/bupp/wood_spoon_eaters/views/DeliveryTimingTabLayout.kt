package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.DeliveryTimingTabLayoutBinding
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.RestaurantPageViewModel
import com.bupp.wood_spoon_eaters.utils.DateUtils.parseDateToDayDateSplash
import com.google.android.material.tabs.TabLayout
import com.trading212.stickyheader.dpToPx


class DeliveryTimingTabLayout @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: DeliveryTimingTabLayoutBinding = DeliveryTimingTabLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    var datesList: List<RestaurantPageViewModel.DeliveryDate>? = null

    fun initDates(datesList: List<RestaurantPageViewModel.DeliveryDate>) {
        this.datesList = datesList
        with(binding) {
            datesList.forEachIndexed() { index, deliveryDate ->
                tabLayout.addTab(tabLayout.newTab())
                tabLayout.getTabAt(index)?.text = parseDateToDayDateSplash(deliveryDate.date)
            }
        }
        setCurvedEdges()
    }

    private fun setCurvedEdges() {
        with(binding) {
            datesList?.let { list ->
                if (list.size > 1) {
                    val startTab = tabLayout.getTabAt(0)?.view
                    val paramsStart = startTab?.layoutParams as ViewGroup.MarginLayoutParams
                    paramsStart.setMargins(dpToPx(21F), 0, 0, 0)
                    startTab.requestLayout()

                    val endTab = tabLayout.getTabAt(list.size - 1)?.view
                    val paramsEnd = endTab?.layoutParams as ViewGroup.MarginLayoutParams
                    paramsEnd.setMargins(0, 0, dpToPx(21F), 0)
                    endTab.requestLayout()
                } else {
                    val startTab = tabLayout.getTabAt(0)?.view
                    val paramsStart = startTab?.layoutParams as ViewGroup.MarginLayoutParams
                    paramsStart.setMargins(dpToPx(21F), 0, dpToPx(21F), 0)
                    startTab.requestLayout()
                }
            }
        }
    }

    fun onDateChangeListener() {
        with(binding) {
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        updateTabUi(it, true)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.let {
                        updateTabUi(it, false)
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })
        }
    }

    fun updateTabUi(tab: TabLayout.Tab, isSelected: Boolean) {
        val tabLayout = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(tab.position) as LinearLayout
        val tabTextView = tabLayout.getChildAt(1) as TextView
        if(isSelected){
            tabTextView.setTypeface(null, Typeface.BOLD);
        } else {
            tabTextView.setTypeface(null, Typeface.NORMAL);
        }
    }

}

