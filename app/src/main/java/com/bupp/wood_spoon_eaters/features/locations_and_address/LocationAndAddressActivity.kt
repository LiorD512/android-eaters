package com.bupp.wood_spoon_eaters.features.locations_and_address

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.ActivityLocationAndAddressBinding
import com.bupp.wood_spoon_eaters.dialogs.WSErrorDialog
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.bupp.wood_spoon_eaters.model.ErrorEventType
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationAndAddressActivity : AppCompatActivity(), HeaderView.HeaderViewListener {

    
    private lateinit var binding: ActivityLocationAndAddressBinding

    @SuppressLint("LongLogTag")
    private val autoCompleteAddressSearchForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.d(TAG,"Activity For Result")
        handleAutocompleteResult(result)
    }

    val viewModel by viewModel<LocationAndAddressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocationAndAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Analytics.with(this).screen("Manage addresses")
//        viewModel.checkIntentParam(intent)

        initUi()
        initObservers()
    }

    private fun initUi() {
        Places.initialize(this, getString(R.string.google_api_key))
        binding.locationActHeader.setHeaderViewListener(this)

        var navController : NavController = Navigation.findNavController(this, R.id.locationActContainer)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Log.d(TAG, "onDestinationChanged: "+destination.label)
            when(destination.label){
                Constants.LOCATION_DESTINATION_SELECT_ADDRESS -> {
                    binding.locationActHeader.setType(Constants.HEADER_VIEW_TYPE_CLOSE_TITLE, "Delivery address")
                }
                Constants.LOCATION_DESTINATION_MAP_VERIFICATION -> {}
                Constants.LOCATION_DESTINATION_FINAL_DETAILS -> {}
            }
        }
    }

    @SuppressLint("LongLogTag")
    private fun initObservers() {
        viewModel.progressData.observe(this, {
            handleProgressBar(it)
        })
        viewModel.mainNavigationEvent.observe(this, {
            Log.d(TAG, it.name)
            when(it){
                LocationAndAddressViewModel.NavigationEventType.OPEN_ADDRESS_LIST_CHOOSER -> {
                    finalDetailsToSelectAddress()
                }
                LocationAndAddressViewModel.NavigationEventType.OPEN_LOCATION_PERMISSION_SCREEN -> {
                    redirectToLocationPermission()
                }
                LocationAndAddressViewModel.NavigationEventType.LOCATION_PERMISSION_GUARENTEED -> {
                    viewModel.updateMyLocationStats()
                }
                LocationAndAddressViewModel.NavigationEventType.LOCATION_AND_ADDRESS_DONE -> {
                    setResult(RESULT_OK, intent)
                    finish()
                }
                LocationAndAddressViewModel.NavigationEventType.OPEN_ADDRESS_AUTO_COMPLETE -> {
                    redirectToAutoCompleteSearch()
                }
                LocationAndAddressViewModel.NavigationEventType.OPEN_MAP_VERIFICATION_SCREEN -> {
                    redirectToAddressVerificationMap()
                }
                LocationAndAddressViewModel.NavigationEventType.OPEN_FINAL_ADDRESS_DETAILS_SCREEN -> {
                    redirectToFinalAddressDetails()
                }
                LocationAndAddressViewModel.NavigationEventType.OPEN_MAP_VERIFICATION_FROM_FINAL_DETAILS -> {
                    redirectToAddressVerificationMapFromFinalDetails()
                }
                else -> {
                    Log.d(TAG, "wow else")
                }
            }
        })
//        viewModel.locationPermissionActionEvent.observe(this, {
//            askLocationPermission()
//        })
        viewModel.addressFoundUiEvent.observe(this, {
            updateAddressHeaderUi(it)
        })
        viewModel.errorEvents.observe(this, {
            when(it){
                ErrorEventType.INVALID_PHONE -> {
                    WSErrorDialog(getString(R.string.login_error_wrong_phone), null).show(supportFragmentManager, Constants.WS_ERROR_DIALOG)
                }
                ErrorEventType.WRONG_PASSWORD -> {
                    WSErrorDialog(getString(R.string.login_error_wrong_code), null).show(supportFragmentManager, Constants.WS_ERROR_DIALOG)
                }
                ErrorEventType.SERVER_ERROR -> {
                    WSErrorDialog(getString(R.string.default_server_error), null).show(supportFragmentManager, Constants.WS_ERROR_DIALOG)
                }
                ErrorEventType.SOMETHING_WENT_WRONG -> {
                    WSErrorDialog(getString(R.string.something_went_wrong_error), null).show(supportFragmentManager, Constants.WS_ERROR_DIALOG)
                }
            }
        })
    }

    private fun handleProgressBar(shouldShow: Boolean?) {
        shouldShow?.let{
            if(it){
                binding.locationActPb.show()
            }else{
                binding.locationActPb.hide()
            }
        }
    }

    private fun updateAddressHeaderUi(address: AddressRequest?) {
        address?.let{
            binding.locationActHeader.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, address.getUserLocationStr())
        }
    }

    private fun redirectToAutoCompleteSearch() {
        val fields = listOf(Place.Field.NAME, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this)
        autoCompleteAddressSearchForResult.launch(intent)
    }

    private fun redirectToLocationPermission() {
//        binding.locationActHeader.setTitle("Location permission")
        val permissionSheet = LocationPermissionFragment()
        permissionSheet.show(supportFragmentManager, Constants.LOCATION_PERMISSION_BOTTOM_SHEET)
//        findNavController(R.id.locationActContainer).navigate(R.id.action_selectAddressFragment_to_locationPermissionFragment)
    }

    private fun redirectToAddressVerificationMap() {
        findNavController(R.id.locationActContainer).navigate(R.id.action_selectAddressFragment_to_addressVerificationMapFragment)
    }

    private fun redirectToFinalAddressDetails() {
        findNavController(R.id.locationActContainer).navigate(R.id.action_addressVerificationMapFragment_to_finalAddressDetailsFragment)
    }

    private fun redirectToAddressVerificationMapFromFinalDetails() {
        onBackPressed()
    }

    private fun finalDetailsToSelectAddress() {
        findNavController(R.id.locationActContainer).navigate(R.id.action_finalAddressDetailsFragment_to_selectAddressFragment)
    }



    @SuppressLint("LongLogTag")
    private fun handleAutocompleteResult(result: ActivityResult) {
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

    override fun onHeaderBackClick() {
        onBackPressed()
    }

    override fun onHeaderCloseClick() {
        finish()
    }

    companion object{
        const val TAG = "wowLocationAndAddresAct"
    }


}