package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.databinding.DeliveryDateTabLayoutBinding
import com.bupp.wood_spoon_eaters.model.SortedCookingSlots
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.utils.waitForLayout
import com.google.android.material.tabs.TabLayout


class DeliveryDateTabLayout @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: DeliveryDateTabLayoutBinding = DeliveryDateTabLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    var listener: DeliveryTimingTabLayoutListener? = null

    interface DeliveryTimingTabLayoutListener {
        fun onDateSelected(date: SortedCookingSlots?)
    }

    lateinit var tabSelectedListener: TabLayout.OnTabSelectedListener

    var datesList: List<SortedCookingSlots>? = null

    fun initDates(datesList: List<SortedCookingSlots>) {
        this.datesList = datesList
        with(binding) {
            datesList.forEachIndexed { index, deliveryDate ->
                tabLayout.addTab(tabLayout.newTab())
                tabLayout.getTabAt(index)?.text = DateUtils.parseDateToDayDateNumberOrToday(deliveryDate.date)
            }
        }
        setCurvedEdges()
        with(binding) {
            tabLayout.addOnTabSelectedListener(tabSelectedListener)
        }
    }

    fun setTabListener(listener: DeliveryTimingTabLayoutListener) {
        this.listener = listener
        this.tabSelectedListener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    listener.onDateSelected(datesList?.getOrNull(tab.position))

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        }
    }

    private fun setCurvedEdges() {
        with(binding) {
            datesList?.let { list ->
                if (list.size > 1) {
                    val startTab = tabLayout.getTabAt(0)?.view
                    val paramsStart = startTab?.layoutParams as MarginLayoutParams
                    paramsStart.setMargins(Utils.toPx(21), 0, 0, 0)
                    startTab.requestLayout()

                    val endTab = tabLayout.getTabAt(list.size - 1)?.view
                    val paramsEnd = endTab?.layoutParams as MarginLayoutParams
                    paramsEnd.setMargins(0, 0, Utils.toPx(21), 0)
                    endTab.requestLayout()
                } else {
                    val startTab = tabLayout.getTabAt(0)?.view
                    val paramsStart = startTab?.layoutParams as MarginLayoutParams?
                    paramsStart?.let{
                        paramsStart.setMargins(Utils.toPx(21), 0, Utils.toPx(21), 0)
                        startTab?.requestLayout()
                    }
                }
            }
        }
    }

    /**
     * Changing selected cookingSlot by Id - only UI without triggering listener
     * returns the selected CookingSlots if found
     */
    fun selectTabByCookingSlotId(cookingSlotId: Long): SortedCookingSlots? {
        datesList?.forEachIndexed { index, date ->
            val curDate = date.cookingSlots.find { it.id == cookingSlotId }
            if (curDate != null) {
                //relevant cookingSlot is found in current date
                with(binding) {
                    tabLayout.waitForLayout {
                        tabLayout.removeOnTabSelectedListener(tabSelectedListener)
                        tabLayout.getTabAt(index)?.select()
                        tabLayout.addOnTabSelectedListener(tabSelectedListener)
                    }
                    return datesList?.getOrNull(index)
                }
            }
        }
        return null
    }

    fun getCurrentSelection(): SortedCookingSlots? {
        with(binding) {
            val position = tabLayout.selectedTabPosition
            return datesList?.getOrNull(position)
        }
    }


}

