package com.bupp.wood_spoon_eaters.views.ws_range_time_picker

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.SnapOnScrollListener
import com.bupp.wood_spoon_eaters.common.recyclerview_ext.attachSnapHelperWithListener
import com.bupp.wood_spoon_eaters.databinding.WsRangeTimePickerBinding
import com.bupp.wood_spoon_eaters.model.WSRangeTimePickerHours
import com.bupp.wood_spoon_eaters.utils.DateUtils
import java.util.*


class WSRangeTimePicker @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr), SnapOnScrollListener.OnSnapPositionChangeListener {

    val viewModel = WSRangeTimePickerViewModel()

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

    private fun initDatesData() {
        val dates = getDaysFromNow(7)
        dates.let {
            it.forEach {
                datesAndHours.add(WSRangeTimePickerHours(it, getHoursForDay(it)))
            }
        }
    }

    private fun initView(attrs: AttributeSet?) {
        attrs?.let {
            val attr = context.obtainStyledAttributes(attrs, R.styleable.WSEditText)
            attr.recycle()
        }
    }

    private fun initDateAndHoursUi() {
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

        wsRangeTimePickerDateAdapter?.submitList(datesAndHours)

        ViewCompat.setNestedScrollingEnabled(binding.wsRangeTimePickerDateList, false)

        //hours
        wsRangeTimePickerHoursAdapter = WSRangeTimePickerHoursAdapter()
        binding.wsRangeTimePickerHoursList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        hoursSnapHelper.attachToRecyclerView(binding.wsRangeTimePickerHoursList)

        binding.wsRangeTimePickerHoursList.adapter = wsRangeTimePickerHoursAdapter
        wsRangeTimePickerHoursAdapter?.submitList(datesAndHours[0].hours)

        ViewCompat.setNestedScrollingEnabled(binding.wsRangeTimePickerHoursList, false)

    }

    override fun onSnapPositionChange(position: Int) {
        Log.d(TAG, "onDatesSnapPositionChange: $position")
        wsRangeTimePickerHoursAdapter?.submitList(datesAndHours[position].hours)
//        if(position > 0){
//            datesAndHours[position].hours.forEachIndexed { index, item ->
//                if(DateUtils.isIn30MinutesRangeFromNow(item)){
//                    binding.wsRangeTimePickerHoursList.scrollToPosition(index)
//                    return@forEachIndexed
//                }
//            }
//        }
    }

    private fun getDaysFromNow(daysFromNow: Int): List<Date> {
        val dates = mutableListOf<Date>()
        for (i in -1..daysFromNow) {
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
//        val today = Calendar.getInstance()
//        today.time = Date()

        val cal = Calendar.getInstance()
        cal.time = date

        val initialDay: Int = cal.get(Calendar.DAY_OF_YEAR)

        if (initialDay == cal.get(Calendar.DAY_OF_YEAR)) {
            cal.add(Calendar.MINUTE, viewModel.getMinFutureOrderWindow())
        }

        while (initialDay == cal.get(Calendar.DAY_OF_YEAR)) {
            val unRoundedMinutes: Int = cal.get(Calendar.MINUTE)
            val mod = unRoundedMinutes % 30
            cal.add(Calendar.MINUTE, 30 - mod)

            val startingHours = cal.time
            hours.add(startingHours)
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
