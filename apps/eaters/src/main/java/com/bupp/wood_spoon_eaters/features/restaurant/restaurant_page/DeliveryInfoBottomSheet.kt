package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.abs.TopCorneredBottomSheet
import com.bupp.wood_spoon_eaters.databinding.DeliveryInfoBottomSheetBinding

class DeliveryInfoBottomSheet : TopCorneredBottomSheet() {
    private var binding: DeliveryInfoBottomSheetBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.delivery_info_bottom_sheet, container, false)
        binding = DeliveryInfoBottomSheetBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.btnOk?.setOnClickListener {
            dismiss()
        }
    }
}