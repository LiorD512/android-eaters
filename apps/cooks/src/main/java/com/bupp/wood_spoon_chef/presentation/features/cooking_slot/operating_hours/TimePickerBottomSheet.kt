package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.operating_hours

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetTimePickerBinding
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.create_cooking_slot.OperatingHours
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

class TimePickerBottomSheet: TopCorneredBottomSheet() {

    private var binding: BottomSheetTimePickerBinding? = null
    private var operatingHours: OperatingHours? = null
    private var selectedDate: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_time_picker, container, false)
        binding = BottomSheetTimePickerBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        operatingHours = requireArguments().getParcelable(OPERATING_HOUR_ARGS_KEY)
        selectedDate = requireArguments().getLong(SELECTED_DATE_ARGS_KEY)
        handleTimePickerStartTime()
    }

    private fun handleTimePickerStartTime() {
        binding?.apply {
            setStartTimeValue()
            timePickerActionNextBtn.setOnClickListener {
                timePickerSwitcher.showNext()
                val startTime = getTimeInMillis(timePickerStartTimePick)
                handleTimePickerEndTime(startTime)
            }
        }
    }

    private fun handleTimePickerEndTime(startTime: Long) {
        binding?.apply {
            timePickerEndTimePick.hour = DateTime(startTime).plusHours(2).hourOfDay
            timePickerEndTimePick.minute = DateTime(startTime).minuteOfHour
            timePickerActionSaveBtn.setOnClickListener {
                setFragmentResult(
                    TIME_KEY, bundleOf(
                        TIME_VALUE to OperatingHours(
                            startTime, getTimeInMillis(timePickerEndTimePick)
                        )
                    )
                )
                dismiss()
            }
        }
    }

    private fun setStartTimeValue() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
        if (operatingHours?.startTime == null) {
            calendar.time = dateFormat.parse("12:00 PM") as Date
            binding?.timePickerStartTimePick?.hour = DateTime(calendar.timeInMillis).hourOfDay
            binding?.timePickerStartTimePick?.minute = DateTime(calendar.timeInMillis).minuteOfHour
        } else {
            binding?.timePickerStartTimePick?.hour = DateTime(operatingHours?.startTime).hourOfDay
            binding?.timePickerStartTimePick?.minute =
                DateTime(operatingHours?.startTime).minuteOfHour
        }
    }

    override fun clearClassVariables() {
        binding = null
    }

    private fun getTimeInMillis(timePicker: TimePicker): Long {
        val calendar = Calendar.getInstance()
        calendar.time = Date(selectedDate)
        binding?.apply {
            calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                timePicker.hour,
                timePicker.minute,
                Calendar.SECOND
            )
        }

        return calendar.timeInMillis
    }

    companion object {
        const val TIME_KEY = "timeKey"
        const val TIME_VALUE = "timeValue"
        const val SELECTED_DATE_ARGS_KEY = "selectedDateArgsKey"
        const val OPERATING_HOUR_ARGS_KEY = "operatingHoursArgsKey"

        fun show(
            fragment: Fragment,
            selectedDate: Long,
            operatingHours: OperatingHours?,
            listener: ((OperatingHours) -> Unit)
        ) {
            TimePickerBottomSheet().apply {
                arguments = bundleOf(
                    SELECTED_DATE_ARGS_KEY to selectedDate,
                    OPERATING_HOUR_ARGS_KEY to operatingHours
                )
            }.show(
                fragment.childFragmentManager,
                TimePickerBottomSheet::class.simpleName
            )
            fragment.setStartTimeResultListener(listener)
        }
    }
}

private fun Fragment.setStartTimeResultListener(listener: ((OperatingHours) -> Unit)) {
    childFragmentManager.setFragmentResultListener(
        TimePickerBottomSheet.TIME_KEY,
        this
    ) { _, bundle ->
        val result = bundle.getParcelable<OperatingHours>(TimePickerBottomSheet.TIME_VALUE)
        result?.let {
            listener.invoke(it)
        }
    }
}