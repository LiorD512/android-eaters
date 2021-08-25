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
import com.bupp.wood_spoon_eaters.model.DeliveryDates
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.views.ws_range_time_picker.*
import com.bupp.wood_spoon_eaters.views.ws_range_time_picker.WSSingleTimePicker
import java.util.*


class WSSingleTimePicker @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    val viewModel = WSRangeTimePickerViewModel()
    lateinit var snapHelper: SnapHelper

    var wsTimePickerCustomAdapter: WSTimePickerStringAdapter? = null
    private var binding: WsSingleTimePickerBinding = WsSingleTimePickerBinding.inflate(LayoutInflater.from(context), this, true)

    private val datesList: MutableList<Date> = mutableListOf()
    private val cookingSlotList: MutableList<CookingSlot> = mutableListOf()
    private val stringPair: MutableList<Pair<String?, String?>> = mutableListOf()

    init {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        attrs?.let {
            val attr = context.obtainStyledAttributes(attrs, R.styleable.WSEditText)
            attr.recycle()
        }

        //dates
        wsTimePickerCustomAdapter = WSTimePickerStringAdapter()
        binding.wsRangeTimePickerDateList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.wsRangeTimePickerDateList.adapter = wsTimePickerCustomAdapter

        snapHelper = LinearSnapHelper()
        binding.wsRangeTimePickerDateList.attachSnapHelperWithListener(
            snapHelper,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE, null)

        ViewCompat.setNestedScrollingEnabled(binding.wsRangeTimePickerDateList, false)
    }

    fun getChosenDate(): Date? {
        val chosenDateView = snapHelper.findSnapView(binding.wsRangeTimePickerDateList.layoutManager)
        val chosenDatePos = chosenDateView?.let { binding.wsRangeTimePickerDateList.getChildLayoutPosition(it) }
        chosenDatePos?.let {
            Log.d(TAG, "chosenDate: ${datesList[it].date}")
            return datesList[it]
        }
        return null
    }

    fun initSimpleDatesData(daysFromNow: Int, selectedDate: Date? = null) {
        stringPair.clear()
        datesList.clear()
        val dates = getDaysFromNow(daysFromNow)
        dates.let {
            it.forEach {
                if (DateUtils.isToday(it)) {
                    stringPair.add(Pair(null, "Today"))
                } else {
                    stringPair.add(Pair(null, DateUtils.parseDateToFullDayDate(it)))
                }
                datesList.add(it)
            }
        }
        wsTimePickerCustomAdapter?.submitList(stringPair)

        selectedDate?.let{
            setSelectedDate(it)
        }
    }

    fun setSelectedDate(selectedDate: Date){
            Log.d(TAG, "setSelectedDate: $selectedDate")
            datesList.forEachIndexed { index, date ->
                if(DateUtils.isSameDay(selectedDate, date) && index > 0){
                    binding.wsRangeTimePickerDateList.scrollToPosition(index)
                }
            }
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

    fun setDatesByDeliveryDates(deliveryDates: List<DeliveryDates>, selectedDeliveryDate: Date? = null) {
        datesList.clear()
        stringPair.clear()
        deliveryDates.let {
            it.forEachIndexed { index, deliveryDates ->
                datesList.add(deliveryDates.from)
                val prefix = "ASAP"
                val isNow = DateUtils.isToday(deliveryDates.from) && index == 0
                var timeRangeStr = DateUtils.parseDateToStartToEnd(deliveryDates.from, deliveryDates.to)
                if(isNow){
                    timeRangeStr = "$prefix ($timeRangeStr)"
                }
                stringPair.add(Pair(null, timeRangeStr))
            }
        }
        wsTimePickerCustomAdapter?.submitList(stringPair)

        selectedDeliveryDate?.let{
            setSelectedDeliveryDate(it)
        }
    }


    private fun setSelectedDeliveryDate(selectedDeliveryDate: Date){
        Log.d(TAG, "setSelectedDeliveryDate: $selectedDeliveryDate")
        datesList.forEachIndexed { index, date ->
            val diff: Long = selectedDeliveryDate.time - date.time
            if(diff.toInt() == 0 && index > 0){
                binding.wsRangeTimePickerDateList.scrollToPosition(index)
            }
        }
    }

    fun setDatesByCookingSlots(cookingSlots: List<CookingSlot>) {
        datesList.clear()
        stringPair.clear()
        val datesAndNames = getCookingSlotsDates(cookingSlots)
        datesAndNames.forEachIndexed { index, dateAndName ->
            val currentCookingSlot = cookingSlots[index]
            cookingSlotList.add(currentCookingSlot)
            var cookingSlotName = dateAndName.second
            var cookingSlotDate = ""
            if(DateUtils.isNowInRange(currentCookingSlot.startsAt, currentCookingSlot.endsAt)){
                cookingSlotName = "Now"
            }
            cookingSlotDate = "${DateUtils.parseDateToUsTime(currentCookingSlot.startsAt)} - ${DateUtils.parseDateToUsTime(currentCookingSlot.endsAt)}"
            stringPair.add(Pair(cookingSlotName, cookingSlotDate))
        }
        wsTimePickerCustomAdapter?.submitList(stringPair)
    }

    private fun getCookingSlotsDates(cookingSlots: List<CookingSlot>): List<Pair<Date, String?>> {
        val dates = mutableListOf<Pair<Date, String?>>()
        cookingSlots.forEach { item ->
            item.orderFrom.let {
                dates.add(Pair(it, item.name))
            }
        }
        return dates
    }

    fun setSelectedCookingSlot(selectedCookingSlot: CookingSlot?){
        selectedCookingSlot?.let{
            Log.d(TAG, "setSelectedCookingSlot: $it")
            cookingSlotList.forEachIndexed { index, cookingSlot ->
                if(cookingSlot.id == selectedCookingSlot.id && index > 0){
                    binding.wsRangeTimePickerDateList.scrollToPosition(index)
                }
            }
        }
    }

    fun getChosenCookingSlot(): CookingSlot? {
        val chosenDateView = snapHelper.findSnapView(binding.wsRangeTimePickerDateList.layoutManager)
        val chosenDatePos = chosenDateView?.let { binding.wsRangeTimePickerDateList.getChildLayoutPosition(it) }
        chosenDatePos?.let {
            Log.d(TAG, "chosenCookingSlot: ${cookingSlotList[it]}")
            return cookingSlotList[it]
        }
        return null
    }

    companion object {
        const val TAG = "wowWSRangeTimePicker"
    }


}
