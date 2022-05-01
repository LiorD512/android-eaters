package com.bupp.wood_spoon_chef.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bupp.wood_spoon_chef.databinding.OrderDateTabLayoutBinding
import com.bupp.wood_spoon_chef.utils.DateUtils
import com.google.android.material.tabs.TabLayout
import java.util.*

class OrderDateTabLayout @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: OrderDateTabLayoutBinding = OrderDateTabLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    var listener: DeliveryTimingTabLayoutListener? = null

    interface DeliveryTimingTabLayoutListener {
        fun onDateSelected(date: Date?)
    }

    private lateinit var tabSelectedListener: TabLayout.OnTabSelectedListener

    var datesList: List<Date>? = null

    init {
        this.tabSelectedListener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    listener?.onDateSelected(datesList?.getOrNull(tab.position))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        }
    }

    fun initDates(datesList: List<Date>) {
        this.datesList = datesList
        with(binding) {
            datesList.forEachIndexed { index, date ->
                tabLayout.addTab(tabLayout.newTab())
                tabLayout.getTabAt(index)?.text = DateUtils.parseDateToDay(date)
            }
        }
        with(binding) {
            tabLayout.addOnTabSelectedListener(tabSelectedListener)
        }
    }


}

