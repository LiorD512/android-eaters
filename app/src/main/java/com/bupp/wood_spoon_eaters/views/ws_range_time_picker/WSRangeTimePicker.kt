package com.bupp.wood_spoon_eaters.views.ws_range_time_picker

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
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.MyLinearSnapHelper
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.SnapOnScrollListener
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.attachSnapHelperWithListener
import com.bupp.wood_spoon_eaters.databinding.WsRangeTimePickerBinding
import com.bupp.wood_spoon_eaters.features.main.search.WSRangeTimeViewItemDecorator
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.WSRangeTimePickerHours
import com.bupp.wood_spoon_eaters.utils.DateUtils
import java.util.*
import java.util.concurrent.TimeUnit


class WSRangeTimePicker @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr), SnapOnScrollListener.OnSnapPositionChangeListener {

    val viewModel = WSRangeTimePickerViewModel()
    val interval = viewModel.getHoursInterval()

    var wsRangeTimePickerDateAdapter: WSRangeTimePickerDateAdapter? = null
    var wsRangeTimePickerHoursAdapter: WSRangeTimePickerHoursAdapter? = null
    private var binding: WsRangeTimePickerBinding = WsRangeTimePickerBinding.inflate(LayoutInflater.from(context), this, true)

    private val datesAndHours: MutableList<WSRangeTimePickerHours> = mutableListOf()
    private val hoursSnapHelper: SnapHelper = LinearSnapHelper()

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
        wsRangeTimePickerDateAdapter = WSRangeTimePickerDateAdapter()
        binding.wsRangeTimePickerDateList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.wsRangeTimePickerDateList.adapter = wsRangeTimePickerDateAdapter

        val snapHelper: SnapHelper = LinearSnapHelper()
        binding.wsRangeTimePickerDateList.attachSnapHelperWithListener(
            snapHelper,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            this@WSRangeTimePicker
        )

//        binding.wsRangeTimePickerDateList.addItemDecoration(WSRangeTimeViewItemDecorator())
        ViewCompat.setNestedScrollingEnabled(binding.wsRangeTimePickerDateList, false)

        //hours
        wsRangeTimePickerHoursAdapter = WSRangeTimePickerHoursAdapter()
        binding.wsRangeTimePickerHoursList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        MyLinearSnapHelper().attachToRecyclerView(binding.wsRangeTimePickerHoursList)
//        binding.wsRangeTimePickerHoursList.addItemDecoration(WSRangeTimeViewItemDecorator())
        binding.wsRangeTimePickerHoursList.adapter = wsRangeTimePickerHoursAdapter
        ViewCompat.setNestedScrollingEnabled(binding.wsRangeTimePickerHoursList, false)

    }

    private fun initDatesData() {
        val dates = getDaysFromNow(7)
        dates.let {
            it.forEach {
                datesAndHours.add(WSRangeTimePickerHours(it, getHoursForDay(it)))
            }
        }
    }

    private fun initDateAndHoursUi() {
        wsRangeTimePickerDateAdapter?.submitList(datesAndHours)

        wsRangeTimePickerHoursAdapter?.submitList(datesAndHours[0].hours)
        binding.wsRangeTimePickerHoursList.scrollToPosition(datesAndHours[0].hours.size/2)
    }

    override fun onSnapPositionChange(position: Int) {
        Log.d(TAG, "onDatesSnapPositionChange: $position")
        wsRangeTimePickerHoursAdapter?.submitList(datesAndHours[position].hours)
        binding.wsRangeTimePickerHoursList.scrollToPosition(datesAndHours[0].hours.size/2)
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

    private fun getHoursForDay(date: Date): List<Date> {
        val hours = mutableListOf<Date>()

        val cal = Calendar.getInstance()
        cal.time = date

        val initialDay: Int = cal.get(Calendar.DAY_OF_YEAR)

        if (initialDay == cal.get(Calendar.DAY_OF_YEAR)) {
            cal.add(Calendar.MINUTE, viewModel.getMinFutureOrderWindow())
        }

        while (initialDay == cal.get(Calendar.DAY_OF_YEAR)) {
            val unRoundedMinutes: Int = cal.get(Calendar.MINUTE)
            val mod = unRoundedMinutes % interval
            cal.add(Calendar.MINUTE, interval - mod)

            val startingHours = cal.time
            hours.add(startingHours)
        }

        return hours
    }

    fun setDatesByMenuItems(menuItems: List<MenuItem>) {
        datesAndHours.clear()
        val dates = getDaysForMenuItems(menuItems)
        dates.let {
            it.forEachIndexed { index, date ->
                val currentMenuItem = menuItems[index]
                datesAndHours.add(WSRangeTimePickerHours(dates[index], getHoursForMenuItem(currentMenuItem.cookingSlot.orderFrom, currentMenuItem.cookingSlot.endsAt)))
            }
        }
        initDateAndHoursUi()
    }

    private fun getDaysForMenuItems(menuItems: List<MenuItem>): List<Date> {
        val dates = mutableListOf<Date>()
        menuItems.forEach {
            val menuItemDate = it.cookingSlot.orderFrom
            dates.add(menuItemDate)
        }
        return dates
    }

    private fun getHoursForMenuItem(startingDate: Date, endingDate: Date): List<Date> {
        val hours = mutableListOf<Date>()

        val startDate = Calendar.getInstance()
        startDate.time = startingDate

        var startingTime: Long = startingDate.time
        val endingTime: Long = endingDate.time

        if (DateUtils.isToday(startingDate)) {
            startDate.time = DateUtils.truncateDate30MinUp(Date())
            startingTime = startDate.timeInMillis
        }

        while (startingTime < endingTime) {
            val unRoundedMinutes: Int = startDate.get(Calendar.MINUTE)
            val mod = unRoundedMinutes % interval
            startDate.add(Calendar.MINUTE, interval - mod)

            val startingHours = startDate.time
            hours.add(startingHours)

            startingTime += TimeUnit.MINUTES.toMillis(interval.toLong())
        }

        return hours
    }

    fun getChosenDate(): Date? {
        val chosenDateView = hoursSnapHelper.findSnapView(binding.wsRangeTimePickerDateList.layoutManager)
        val chosenDatePos = chosenDateView?.let { binding.wsRangeTimePickerDateList.getChildLayoutPosition(it) }
        val chosenView = hoursSnapHelper.findSnapView(binding.wsRangeTimePickerHoursList.layoutManager)
        val chosenPos = chosenView?.let { binding.wsRangeTimePickerHoursList.getChildLayoutPosition(it) }
        chosenDatePos?.let { datePos ->
            chosenPos?.let { hourPos ->
                Log.d(TAG, "chosenDate: ${datesAndHours[datePos].hours[hourPos]}")
                return datesAndHours[datePos].hours[hourPos]
            }
        }
        return null
    }

    companion object {
        const val TAG = "wowWSRangeTimePicker"
    }


}
