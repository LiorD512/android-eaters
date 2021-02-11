package com.bupp.wood_spoon_eaters.features.bottom_sheets.address_menu

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.AddressMenuBottomSheetBinding
import com.bupp.wood_spoon_eaters.dialogs.WSErrorDialog
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.ErrorEventType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddressMenuBottomSheet() : BottomSheetDialogFragment() {

    private lateinit var binding: AddressMenuBottomSheetBinding
    val viewModel by viewModel<AddressMenuViewModel>()
    val mainViewModel by sharedViewModel<LocationAndAddressViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.address_menu_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = AddressMenuBottomSheetBinding.bind(view)
        val resources = resources

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            assert(view != null)
            val parent = view?.parent as View
            val layoutParams = parent.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.setMargins(
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_horizontal_margin), // LEFT 16dp
                0,
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_horizontal_margin), // RIGHT 16dp
                0
            )
            parent.layoutParams = layoutParams
            parent.setBackgroundResource(R.drawable.floating_bottom_sheet_bkg)
        }

        val address = requireArguments().getParcelable<Address>("address")
        address?.let{
            viewModel.setCurrentAddress(it)
        }

        initUi()
        initObservers()
    }

    private fun initObservers() {
        viewModel.currentAddress.observe(viewLifecycleOwner, {
            binding.addressMenuTitle.text = it.getUserLocationStr()
        })

        viewModel.navigationEvent.observe(viewLifecycleOwner, {
            when(it){
                AddressMenuViewModel.NavigationEventType.ADDRESS_MENU_DONE -> {
                    mainViewModel.updateMyAddresses()
                    dismiss()
                }
            }
        })
        viewModel.errorEvents.observe(viewLifecycleOwner, {
            when(it){
                ErrorEventType.INVALID_PHONE -> {
                    WSErrorDialog(getString(R.string.login_error_wrong_phone), null).show(childFragmentManager, Constants.WS_ERROR_DIALOG)
                }
                ErrorEventType.WRONG_PASSWORD -> {
                    WSErrorDialog(getString(R.string.login_error_wrong_code), null).show(childFragmentManager, Constants.WS_ERROR_DIALOG)
                }
                ErrorEventType.SERVER_ERROR -> {
                    WSErrorDialog(getString(R.string.default_server_error), null).show(childFragmentManager, Constants.WS_ERROR_DIALOG)
                }
                ErrorEventType.SOMETHING_WENT_WRONG -> {
                    WSErrorDialog(getString(R.string.something_went_wrong_error), null).show(childFragmentManager, Constants.WS_ERROR_DIALOG)
                }
            }
        })
    }

    private fun initUi() {
        binding.addressMenuClose.setOnClickListener {
//            viewModel.setDeliveryTime(null)
            dismiss()
        }
        binding.addressMenuDelete.setOnClickListener {
            viewModel.deleteCurrentAddress()
        }

    }

}