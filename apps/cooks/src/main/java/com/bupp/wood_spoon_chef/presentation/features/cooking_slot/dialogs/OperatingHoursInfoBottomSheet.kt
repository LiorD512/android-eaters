package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetOperatingHoursInfoBinding

class OperatingHoursInfoBottomSheet : TopCorneredBottomSheet() {

    private var binding: BottomSheetOperatingHoursInfoBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_operating_hours_info, container, false)
        binding = BottomSheetOperatingHoursInfoBinding.bind(view)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        binding?.apply {
            operatingHourBsOkBtn.setOnClickListener { dismiss() }
        }
    }

    override fun clearClassVariables() {
        binding = null
    }
}