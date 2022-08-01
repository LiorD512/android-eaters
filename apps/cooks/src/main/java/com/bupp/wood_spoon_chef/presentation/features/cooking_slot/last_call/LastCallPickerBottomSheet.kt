package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetLastCallTimePickerBinding
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.TimePickerBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call.LastCallPickerBottomSheet.Companion.TIME_KEY
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call.LastCallPickerBottomSheet.Companion.TIME_VALUE
import kotlinx.parcelize.Parcelize


@Parcelize
data class SelectedHoursAndMinutes(
    var hours: Int,
    var minutes: Int
) : Parcelable

class LastCallPickerBottomSheet : TopCorneredBottomSheet() {

    private var binding: BottomSheetLastCallTimePickerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_last_call_time_picker, container, false)
        binding = BottomSheetLastCallTimePickerBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.apply {
            timePicker.apply {
                setIs24HourView(true)
                hour = 0
                minute = 0
            }

            btnSetTime.setOnClickListener {
                setFragmentResult(
                    TimePickerBottomSheet.TIME_KEY, bundleOf(
                        TimePickerBottomSheet.TIME_VALUE to SelectedHoursAndMinutes(
                            hours = timePicker.hour,
                            minutes = timePicker.minute
                        )
                    )
                )
                dismiss()
            }
        }
    }

    companion object {
        const val TIME_KEY = "timeKey"
        const val TIME_VALUE = "timeValue"

        fun show(
            fragment: Fragment,
            listener: ((SelectedHoursAndMinutes) -> Unit)
        ) {
            LastCallPickerBottomSheet().show(
                fragment.childFragmentManager,
                LastCallPickerBottomSheet::class.simpleName
            )
            fragment.setStartTimeResultListener(listener)
        }
    }

    override fun clearClassVariables() {}
}

private fun Fragment.setStartTimeResultListener(listener: ((SelectedHoursAndMinutes) -> Unit)) {
    childFragmentManager.setFragmentResultListener(
        TIME_KEY,
        this
    ) { _, bundle ->
        val result = bundle.getParcelable<SelectedHoursAndMinutes>(TIME_VALUE)
        result?.let {
            listener.invoke(it)
        }
    }
}