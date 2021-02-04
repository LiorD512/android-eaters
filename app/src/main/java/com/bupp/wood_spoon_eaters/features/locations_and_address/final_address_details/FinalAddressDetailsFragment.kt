package com.bupp.wood_spoon_eaters.features.locations_and_address.final_address_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.FragmentFinalAddressDetailsBinding
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.model.AddressRequest
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FinalAddressDetailsFragment : Fragment(R.layout.fragment_final_address_details) {

    val mainViewModel by sharedViewModel<LocationAndAddressViewModel>()
    var binding: FragmentFinalAddressDetailsBinding? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFinalAddressDetailsBinding.bind(view)

        initUi()
        initObserver()
    }

    private fun initUi() {
        binding!!.addressDetailsDeliverToDoor.setOnClickListener{
            onDeliverToDoorClick()
        }
        binding!!.addressDetailsPickOutside.setOnClickListener{
            onPickupOutsideClick()
        }
        binding!!.addressDetailsSaveBtn.setOnClickListener {
            if(validateFields()){

            }
        }
        binding!!.addressDetailsEditBtn.setOnClickListener {
            mainViewModel.redirectFinalDetailsToMap()
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        if(binding!!.addressDetailsApt.getText().isNullOrEmpty()){
            isValid = false
            binding!!.addressDetailsApt.showError()
        }
        return isValid
    }

    private fun initObserver() {
        mainViewModel.newAddressLiveData.observe(viewLifecycleOwner, {
            updateAddressUi(it)
        })
    }

    private fun updateAddressUi(address: AddressRequest) {
        binding!!.addressDetailsStreet.setText("${address.streetNumber} ${address.streetLine1}")
        binding!!.addressDetailsCity.setText(address.cityName)
        binding!!.addressDetailsState.setText(address.stateIso)
        binding!!.addressDetailsZipcode.setText(address.zipCode)
    }

    private fun onDeliverToDoorClick() {
        mainViewModel.updateDeliveryMethod(getString(R.string.delivery_method_deliver_to_door))
        binding!!.addressDetailsDeliverToDoor.setBtnSelected(true)
        binding!!.addressDetailsPickOutside.setBtnSelected(false)
    }

    private fun onPickupOutsideClick() {
        mainViewModel.updateDeliveryMethod(getString(R.string.delivery_method_pickup_outside))
        binding!!.addressDetailsPickOutside.setBtnSelected(true)
        binding!!.addressDetailsDeliverToDoor.setBtnSelected(false)
    }



}