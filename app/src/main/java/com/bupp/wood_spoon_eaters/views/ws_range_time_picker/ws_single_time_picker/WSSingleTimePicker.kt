package com.bupp.wood_spoon_eaters.views.ws_range_time_picker.ws_single_time_picker

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.SnapOnScrollListener
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.attachSnapHelperWithListener
import com.bupp.wood_spoon_eaters.databinding.WsSingleTimePickerBinding
import com.bupp.wood_spoon_eaters.model.CookingSlot
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.views.ws_range_time_picker.*
import com.bupp.wood_spoon_eaters.views.ws_range_time_picker.WSSingleTimePicker
import java.util.*


class WSSingleTimePicker @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr), SnapOnScrollListener.OnSnapPositionChangeListener {

    val viewModel = WSRangeTimePickerViewModel()
    lateinit var snapHelper: SnapHelper

    var wsTimePickerCustomAdapter: WSTimePickerCustomAdapter? = null
    private var binding: WsSingleTimePickerBinding = WsSingleTimePickerBinding.inflate(LayoutInflater.from(context), this, true)

    private val datesList: MutableList<WSBaseTimePicker> = mutableListOf()

    init {
        initView(attrs)
        initDatesData()
        initDateAndHoursUi()
    }

    private fun initView(attrs: AttributeSet?) {
        attrs?.let {
            val attr = context.obtainStyledAttributes(attrs, R.styleable.WSEditText)
            attr.recycle()
        }

        //dates
        wsTimePickerCustomAdapter = WSTimePickerCustomAdapter()
        binding.wsRangeTimePickerDateList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.wsRangeTimePickerDateList.adapter = wsTimePickerCustomAdapter

        snapHelper = LinearSnapHelper()
        binding.wsRangeTimePickerDateList.attachSnapHelperWithListener(
            snapHelper,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            this@WSSingleTimePicker
        )

        ViewCompat.setNestedScrollingEnabled(binding.wsRangeTimePickerDateList, false)

    }

    private fun initDatesData() {
        val dates = getDaysFromNow(7)
        dates.let {
            it.forEach {
                datesList.add(WSSingleTimePicker(date = it))
            }
        }
    }

    private fun initDateAndHoursUi() {
//        wsTimePickerCustomAdapter?.submitList(datesList as List<WSBaseTimePicker>?)
    }

    override fun onSnapPositionChange(position: Int) {
        Log.d(TAG, "onDatesSnapPositionChange: $position")
//        wsRangeTimePickerHoursAdapter?.submitList(datesAndHours[position].hours)
//        binding.wsRangeTimePickerHoursList.scrollToPosition(datesAndHours[0].hours.size/2)
    }

    private fun getDaysFromNow(daysFromNow: Int): List<Date> {
        val dates = mutableListOf<Date>()
        for (i in 0..daysFromNow) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, i)
            if (i > 0) {
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
            }
            dates.add(calendar.time)
        }
        return dates
    }

    fun setDatesByMenuItems(menuItems: List<MenuItem>) {
        datesList.clear()
        val dates = getDaysForMenuItems(menuItems)
        dates.let {
            it.forEachIndexed { index, date ->
                val currentMenuItem = menuItems[index]
                currentMenuItem.cookingSlot?.let{
                    datesList.add(WSSingleTimePicker(date = dates[index]))
                }
            }
        }
        wsTimePickerCustomAdapter?.submitList(datesList.toList() as List<WSSingleTimePicker>?)
//        initDateAndHoursUi()
    }

    private fun getDaysForMenuItems(menuItems: List<MenuItem>): List<Date> {
        val dates = mutableListOf<Date>()
        menuItems.forEach { item ->
            item.cookingSlot?.orderFrom?.let{
                dates.add(it)
            }
        }
        return dates
    }

    fun getChosenDate(): Date? {
        val chosenDateView = snapHelper.findSnapView(binding.wsRangeTimePickerDateList.layoutManager)
        val chosenDatePos = chosenDateView?.let { binding.wsRangeTimePickerDateList.getChildLayoutPosition(it) }
        chosenDatePos?.let {
            Log.d(TAG, "chosenDate: ${datesList[it].date}")
            return datesList[it].date
        }
        return null
    }

    fun setDatesByCookingSlots(cookingSlots: List<CookingSlot>) {
    datesList.clear()
        val dates = getCookingSlotsDates(cookingSlots)
        dates.let {
            it.forEachIndexed { index, date ->
                val currentCookingSlot = cookingSlots[index]
                currentCookingSlot?.let{
                    datesList.add(WSCookingSlotTimePicker(title = it.name, cookingSlot = it))
                }
            }
        }
        wsTimePickerCustomAdapter?.submitList(datesList.toList() as List<WSCookingSlotTimePicker>?)
//        initDateAndHoursUi()
    }

    private fun getCookingSlotsDates(cookingSlots: List<CookingSlot>): List<Pair<Date, String?>> {
        val dates = mutableListOf<Pair<Date, String?>>()
        cookingSlots.forEach { item ->
            item.orderFrom.let{
                dates.add(Pair(it, item.name))
            }
        }
        return dates
    }

    fun getChosenCookingSlot(): CookingSlot? {
        val chosenDateView = snapHelper.findSnapView(binding.wsRangeTimePickerDateList.layoutManager)
        val chosenDatePos = chosenDateView?.let { binding.wsRangeTimePickerDateList.getChildLayoutPosition(it) }
        chosenDatePos?.let {
            Log.d(TAG, "chosenDate: ${datesList[it].date}")
            return (datesList[it] as WSCookingSlotTimePicker).cookingSlot
        }
        return null
    }

    companion object {
        const val TAG = "wowWSRangeTimePicker"
    }


}
