package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetTimePickerBinding
import java.util.*

class TimePickerBottomSheet(
    private val timePickerState: TimePickerState
) : TopCorneredBottomSheet() {

    private var binding: BottomSheetTimePickerBinding? = null

    enum class TimePickerState {
        START_TIME,
        END_TIME
    }

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

        initUi()
    }

    private fun initUi() {
        when (timePickerState) {
            TimePickerState.START_TIME -> {
                handleTimePickerStartTime()
            }
            TimePickerState.END_TIME -> {
                handleTimePickerEndTime()
            }
        }
    }

    private fun handleTimePickerStartTime() {
        binding?.apply {
            timePickerTitle.text = getString(R.string.opening_time)
            timePickerActionBtn.setText(getString(R.string.code_fragment_next_btn))
            timePickerActionBtn.setOnClickListener {
                setFragmentResult(START_TIME_KEY, bundleOf(START_TIME_VALUE to getTimeInMillis()))
                dismiss()
            }
        }
    }

    private fun handleTimePickerEndTime() {
        binding?.apply {
            timePickerTitle.text = getString(R.string.closing_time)
            timePickerActionBtn.setText(getString(R.string.set_time))
            timePickerActionBtn.setOnClickListener {
                setFragmentResult(END_TIME_KEY, bundleOf(END_TIME_VALUE to getTimeInMillis()))
                dismiss()
            }
        }
    }

    override fun clearClassVariables() {
        //Do nothing
    }

    private fun getTimeInMillis(): Long {
        val calendar = Calendar.getInstance()
        binding?.apply {
            calendar.set(
                0, 0, 0,
                timePickerTimePick.hour, timePickerTimePick.minute, 0
            )
        }

        return calendar.timeInMillis
    }

    companion object {
        const val START_TIME_KEY = "startTimeKey"
        const val END_TIME_KEY = "endTimeKey"
        const val START_TIME_VALUE = "startTimeValue"
        const val END_TIME_VALUE = "endTimeValue"

        fun show(fragment: Fragment, listener: ((Long) -> Unit)) {
            TimePickerBottomSheet(TimePickerState.START_TIME).show(
                fragment.childFragmentManager,
                TimePickerBottomSheet::class.simpleName
            )
            fragment.setStartTimeResultListener(listener)
        }
    }
}

private fun Fragment.setStartTimeResultListener(listener: ((Long) -> Unit)) {
    childFragmentManager.setFragmentResultListener(
        TimePickerBottomSheet.START_TIME_KEY,
        this
    ) { _, bundle ->
        val result = bundle.getLong(TimePickerBottomSheet.START_TIME_VALUE)
        listener.invoke(result)
    }
}