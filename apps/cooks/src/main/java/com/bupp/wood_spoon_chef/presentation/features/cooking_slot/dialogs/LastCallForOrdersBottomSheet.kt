package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetLastCallForOrdersBinding

class LastCallForOrdersBottomSheet: TopCorneredBottomSheet() {

    private var binding: BottomSheetLastCallForOrdersBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_last_call_for_orders, container, false)
        binding = BottomSheetLastCallForOrdersBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreenDialog()
    }

    override fun clearClassVariables() {
        //Do Nothing
    }
}