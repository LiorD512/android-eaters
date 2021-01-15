package com.bupp.wood_spoon_eaters.features.locations_and_address

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.model.Address
import kotlinx.android.synthetic.main.activity_location_and_address.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationAndAddressActivity : AppCompatActivity(), HeaderView.HeaderViewListener {

    val viewModel by viewModel<LocationAndAddressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_and_address)

        viewModel.checkIntentParam(intent)

        initUi()
        initObservers()
    }

    private fun initUi() {
        locationActHeader.setHeaderViewListener(this)
    }

    @SuppressLint("LongLogTag")
    private fun initObservers() {
        viewModel.navigationEvent.observe(this, Observer{
            Log.d(TAG, it.type.name)
            when(it.type){
                LocationAndAddressViewModel.NavigationEventType.OPEN_ADDRESS_LIST_CHOOSER -> {
                    redirectToAddressListChooser()
                }
                LocationAndAddressViewModel.NavigationEventType.OPEN_ADD_NEW_ADDRESS_SCREEN -> {
                    redirectToAddAddress()
                }
                LocationAndAddressViewModel.NavigationEventType.OPEN_EDIT_ADDRESS_SCREEN -> {
                    redirectToEditAddress(it.address)
                }
                LocationAndAddressViewModel.NavigationEventType.DONE_WITH_LOCATION_AND_ADDRESS -> {
                    finish()
                }
            }
        })
    }

    private fun redirectToAddressListChooser() {
        locationActHeader.setTitle("My Address")
        findNavController(R.id.locationActContainer).navigate(R.id.action_deliveryDetailsFragment_to_addressListChooserFragment)
    }

    private fun redirectToAddAddress() {
        locationActHeader.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Add New Address")
        findNavController(R.id.locationActContainer).navigate(R.id.action_addressListChooserFragment_to_addOrEditAddressFragment)
    }

    private fun redirectToEditAddress(address: Address?) {
        locationActHeader.setTitle("Edit Address")
        val bundle = bundleOf("address" to address)
        findNavController(R.id.locationActContainer).navigate(R.id.action_addressListChooserFragment_to_addOrEditAddressFragment, bundle)
    }

    override fun onHeaderBackClick() {
        onBackPressed()
    }

    override fun onHeaderSaveClick() {
        //add new Address - Save header btn
        viewModel.onSaveNewAddressClick()
    }

    companion object{
        const val TAG = "wowLocationAndAddressAct"
    }

}