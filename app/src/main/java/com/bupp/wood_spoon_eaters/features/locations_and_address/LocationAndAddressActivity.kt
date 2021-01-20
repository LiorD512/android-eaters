package com.bupp.wood_spoon_eaters.features.locations_and_address

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.model.Address
import com.facebook.FacebookSdk
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_location_and_address.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationAndAddressActivity : AppCompatActivity(), HeaderView.HeaderViewListener {

    @SuppressLint("LongLogTag")
    private val autoCompleteAddressSearchForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.d(TAG,"Activity For Result")
        val data = result.data
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    Log.i(TAG, "Place: ${place.name}, ${place.addressComponents}, ${place.latLng}")
                    viewModel.updateAutoCompleteAddressFound(place)
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                data?.let {
                    val status = Autocomplete.getStatusFromIntent(data)
                    Log.i(TAG, status.statusMessage)
                }
            }
            Activity.RESULT_CANCELED -> {
                // The user canceled the operation.
            }
        }

    }

    val viewModel by viewModel<LocationAndAddressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_and_address)

        viewModel.checkIntentParam(intent)

        initUi()
        initObservers()
    }

    private fun initUi() {
        Places.initialize(this, getString(R.string.google_api_key))
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
                LocationAndAddressViewModel.NavigationEventType.OPEN_ADDRESS_AUTO_COMPLETE -> {
                    redirectToAutoCompleteSearch()
                }
                LocationAndAddressViewModel.NavigationEventType.DONE_WITH_LOCATION_AND_ADDRESS -> {
                    finish()
                }
            }
        })
    }

    private fun redirectToAutoCompleteSearch() {
        val fields = listOf(Place.Field.NAME, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this)
        autoCompleteAddressSearchForResult.launch(intent)
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