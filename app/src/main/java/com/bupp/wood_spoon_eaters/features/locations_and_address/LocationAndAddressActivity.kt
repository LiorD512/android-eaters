package com.bupp.wood_spoon_eaters.features.locations_and_address

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    companion object{
        const val TAG = "wowLocationAndAddressAct"
    }
    
    private lateinit var binding: ActivityLocationAndAddressBinding

    @SuppressLint("LongLogTag")
    private val autoCompleteAddressSearchForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.d(TAG,"Activity For Result")
        handleAutocompleteResult(result)
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        viewModel.onLocationPermissionDone()
    }

    val viewModel by viewModel<LocationAndAddressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocationAndAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Analytics.with(this).screen("Manage addresses")
        viewModel.checkIntentParam(intent)

        initUi()
        initObservers()
    }

    private fun initUi() {
        Places.initialize(this, getString(R.string.google_api_key))
        binding.locationActHeader.setHeaderViewListener(this)
    }

    @SuppressLint("LongLogTag")
    private fun initObservers() {
        viewModel.progressData.observe(this, {
            handleProgressBar(it)
        })
        viewModel.mainNavigationEvent.observe(this, {
            Log.d(TAG, it.name)
            when(it){
                LocationAndAddressViewModel.NavigationEventType.OPEN_LOCATION_PERMISSION_SCREEN -> {
                    redirectToLocationPermission()
                }
                LocationAndAddressViewModel.NavigationEventType.LOCATION_PERMISSION_DONE -> {
                    onBackPressed()
                }
                LocationAndAddressViewModel.NavigationEventType.LOCATION_AND_ADDRESS_DONE -> {
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
                LocationAndAddressViewModel.NavigationEventType.DONE_WITH_LOCATION_AND_ADDRESS -> {
                    finish()
                }
//                LocationAndAddressViewModel.NavigationEventType.OPEN_ADDRESS_LIST_CHOOSER -> {
////                    redirectToAddressListChooser()
//                }
//                LocationAndAddressViewModel.NavigationEventType.OPEN_ADD_NEW_ADDRESS_SCREEN -> {
////                    redirectToAddAddress()
//                }
//                LocationAndAddressViewModel.NavigationEventType.OPEN_EDIT_ADDRESS_SCREEN -> {
////                    redirectToEditAddress(it.address)
//                }
                else -> {
                    Log.d(TAG, "wow else")
                }
            }
        })
        viewModel.locationPermissionActionEvent.observe(this, {
            askLocationPermission()
        })
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
            binding.locationActHeader.setTitle(address.getUserLocationStr())
        }
    }

    private fun redirectToAutoCompleteSearch() {
        val fields = listOf(Place.Field.NAME, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this)
        autoCompleteAddressSearchForResult.launch(intent)
    }

    private fun redirectToLocationPermission() {
        binding.locationActHeader.setTitle("Location permission")
        findNavController(R.id.locationActContainer).navigate(R.id.action_selectAddressFragment_to_locationPermissionFragment)
    }

    private fun redirectToAddressVerificationMap() {
        findNavController(R.id.locationActContainer).navigate(R.id.action_selectAddressFragment_to_addressVerificationMapFragment)
    }

    private fun redirectToFinalAddressDetails() {
        findNavController(R.id.locationActContainer).navigate(R.id.action_addressVerificationMapFragment_to_finalAddressDetailsFragment)
    }

    private fun redirectToAddressVerificationMapFromFinalDetails() {
        onBackPressed()
//        findNavController(R.id.locationActContainer).navigate(R.id.action_finalAddressDetailsFragment_to_addressVerificationMapFragment)
    }


//    private fun redirectToAddressListChooser() {
//        locationActHeader.setTitle("My Address")
//        findNavController(R.id.locationActContainer).navigate(R.id.action_deliveryDetailsFragment_to_addressListChooserFragment)
//    }
//
//    private fun redirectToAddAddress() {
//        locationActHeader.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Add New Address")
//        findNavController(R.id.locationActContainer).navigate(R.id.action_addressListChooserFragment_to_addOrEditAddressFragment)
//    }
//
//    private fun redirectToEditAddress(address: Address?) {
//        locationActHeader.setTitle("Edit Address")
//        val bundle = bundleOf("address" to address)
//        findNavController(R.id.locationActContainer).navigate(R.id.action_addressListChooserFragment_to_addOrEditAddressFragment, bundle)
//    }

    private fun askLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG,"location grated")
                viewModel.onLocationPermissionDone()
            }
            shouldShowRequestPermissionRationale() -> {
                Log.d(TAG,"shouldShowRequestPermissionRationale")
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected.
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            }
            else -> {
                Log.d(TAG,"asking for permission")
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            }
        }

    }

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

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

    override fun onHeaderSaveClick() {
        //add new Address - Save header btn
        viewModel.onSaveNewAddressClick()
    }




}