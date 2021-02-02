package com.bupp.wood_spoon_eaters.features.bottom_sheets.time_picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.TimePickerBottomSheetBinding
import com.bupp.wood_spoon_eaters.features.bottom_sheets.address_menu.AddressMenuViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class TimePickerBottomSheet() : BottomSheetDialogFragment() {

    private lateinit var binding: TimePickerBottomSheetBinding
    val viewModel by viewModel<AddressMenuViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.time_picker_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = TimePickerBottomSheetBinding.bind(view)

        initUi()
    }

    private fun initUi() {
        binding.timePickerAsapBtn.setOnClickListener {
            viewModel.setDeliveryTime(null)
            dismiss()
        }
        binding.timePickerScheduleBtn.setOnClickListener {
            viewModel.setDeliveryTime(binding.singleDayPicker.date)
            dismiss()
        }
    }

}