package com.bupp.wood_spoon_eaters.features.locations_and_address.final_address_details

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.FragmentFinalAddressDetailsBinding
import com.bupp.wood_spoon_eaters.dialogs.WrongAddressDialog
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.bupp.wood_spoon_eaters.views.WSEditText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FinalAddressDetailsFragment : Fragment(R.layout.fragment_final_address_details), WrongAddressDialog.WrongAddressDialogListener,
    WSEditText.WSEditTextListener {

    val mainViewModel by sharedViewModel<LocationAndAddressViewModel>()
    val binding: FragmentFinalAddressDetailsBinding by viewBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObserver()
    }



    private fun initUi() {
        with(binding){
            addressDetailsDeliverToDoor.setOnClickListener{
                onDeliverToDoorClick()
            }
            addressDetailsPickOutside.setOnClickListener{
                onPickupOutsideClick()
            }
            addressDetailsSaveBtn.setOnClickListener {
                if(validateFields()){
                    val apt = addressDetailsApt.getText()
                    val note = addressDetailsNote.getText()
                    val city = addressDetailsCity.getText()
                    val state = addressDetailsState.getText()
                    val zipCode = addressDetailsZipcode.getText()
                    mainViewModel.saveNewAddress(note, apt, city, state, zipCode)
                }
            }
            addressDetailsEditBtn.setOnClickListener {
                mainViewModel.redirectFinalDetailsToMap()
            }
            addressDetailsDeliverToDoor.performClick()
        }

    }

    private fun validateFields(): Boolean {
        var isValid = true
        if(binding.addressDetailsApt.getText().isNullOrEmpty()){
            isValid = false
            binding.addressDetailsApt.showError()
        }
        if(binding.addressDetailsZipcode.getText().isNullOrEmpty()){
            isValid = false
            binding.addressDetailsZipcode.showError()
        }
        return isValid
    }

    private fun initObserver() {
        mainViewModel.newAddressLiveData.observe(viewLifecycleOwner, {
            updateAddressUi(it)
        })
    }

    private fun updateAddressUi(address: AddressRequest) {
        with(binding){
            addressDetailsStreet.setText("${address.streetNumber} ${address.streetLine1}")
            address.cityName?.let{
                addressDetailsCity.setIsEditable(false, this@FinalAddressDetailsFragment)
                addressDetailsCity.setText(address.cityName)
            }
            address.stateIso?.let{
                addressDetailsState.setIsEditable(false, this@FinalAddressDetailsFragment)
                addressDetailsState.setText(address.stateIso)
            }
            address.zipCode?.let{
                addressDetailsZipcode.setIsEditable(false, this@FinalAddressDetailsFragment)
                addressDetailsZipcode.setText(address.zipCode)

            }
        }
    }


    private fun onDeliverToDoorClick() {
        mainViewModel.updateDeliveryMethod(getString(R.string.delivery_method_deliver_to_door))
        binding.addressDetailsDeliverToDoor.setBtnSelected(true)
        binding.addressDetailsPickOutside.setBtnSelected(false)
    }

    private fun onPickupOutsideClick() {
        mainViewModel.updateDeliveryMethod(getString(R.string.delivery_method_pickup_outside))
        binding.addressDetailsPickOutside.setBtnSelected(true)
        binding.addressDetailsDeliverToDoor.setBtnSelected(false)
    }

    override fun onReEnterAddressClick() {
        mainViewModel.onReEnterAddressClick()
    }

    override fun onWSEditUnEditableClick() {
        WrongAddressDialog().setWrongAddressDialogListener(this).show(childFragmentManager, Constants.WRONG_ADDRESS_DIALOG)
    }



}