package com.bupp.wood_spoon_eaters.features.bottom_sheets.address_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.AddressMenuBottomSheetBinding
import com.bupp.wood_spoon_eaters.databinding.TimePickerBottomSheetBinding
import com.bupp.wood_spoon_eaters.features.bottom_sheets.address_menu.AddressMenuViewModel
import com.bupp.wood_spoon_eaters.model.Address
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddressMenuBottomSheet() : BottomSheetDialogFragment() {

    private lateinit var binding: AddressMenuBottomSheetBinding
    val viewModel by viewModel<AddressMenuViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.address_menu_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = AddressMenuBottomSheetBinding.bind(view)

        initUi()
    }

    private fun initUi() {
        binding.addressMenuClose.setOnClickListener {
            viewModel.setDeliveryTime(null)
            dismiss()
        }
        binding.addressMenuDelete.setOnClickListener {
            viewModel.setDeliveryTime(null)
            dismiss()
        }
        binding.addressMenuTitle.text = "${requireArguments().getParcelable<Address>("address")?.getUserLocationStr()}"
    }

}