package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetWarningLastCallBinding

interface OnWarningLastCallBottomSheetListener {
    fun onContinueClicked()
}

class WarningLastCallBottomSheet(
    val listener: OnWarningLastCallBottomSheetListener
) : TopCorneredBottomSheet() {

    private var binding: BottomSheetWarningLastCallBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_warning_last_call, container, false)
        binding = BottomSheetWarningLastCallBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnContinue.setOnClickListener {
                listener.onContinueClicked()
            }
        }
    }

    override fun clearClassVariables() {}
}
