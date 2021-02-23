package com.bupp.wood_spoon_eaters.features.locations_and_address

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.common.Constants
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.bupp.wood_spoon_eaters.model.ErrorEventType
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.bupp.wood_spoon_eaters.utils.GoogleAddressParserUtil
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import kotlinx.coroutines.launch

class LocationAndAddressViewModel(val eaterDataManager: EaterDataManager, val userRepository: UserRepository) : ViewModel() {

    val progressData = ProgressData()
    val errorEvents: MutableLiveData<ErrorEventType> = MutableLiveData()

    val locationPermissionActionEvent: MutableLiveData<Boolean> = MutableLiveData()
    val mainNavigationEvent = MutableLiveData<NavigationEventType>()
    enum class NavigationEventType {
        OPEN_EDIT_ADDRESS_SCREEN,
        OPEN_ADDRESS_LIST_CHOOSER,
        OPEN_ADD_NEW_ADDRESS_SCREEN,
        OPEN_ADDRESS_AUTO_COMPLETE,
        OPEN_LOCATION_PERMISSION_SCREEN,
        OPEN_MAP_VERIFICATION_SCREEN,
        OPEN_FINAL_ADDRESS_DETAILS_SCREEN,
        OPEN_MAP_VERIFICATION_FROM_FINAL_DETAILS,
        LOCATION_PERMISSION_DONE,
        LOCATION_AND_ADDRESS_DONE
    }

    fun askLocationPermission() {
        locationPermissionActionEvent.postValue(true)
    }

    fun getLocationLiveData() = eaterDataManager.getLocationData()

    fun onDoneClick() {
        mainNavigationEvent.postValue(NavigationEventType.LOCATION_AND_ADDRESS_DONE)
    }



    private var unsavedNewAddress: AddressRequest? = null
    private lateinit var currentPlaceFound: Place
    val actionEvent = MutableLiveData<ActionEvent>()
    enum class ActionEvent {
        SAVE_NEW_ADDRESS,
        FETCH_MY_ADDRESS
    }

    val addressFoundUiEvent = MutableLiveData<AddressRequest>()

    fun updateMyAddresses(){
        actionEvent.postValue(ActionEvent.FETCH_MY_ADDRESS)
    }


    fun showLocationPermissionScreen() {
        mainNavigationEvent.postValue(NavigationEventType.OPEN_LOCATION_PERMISSION_SCREEN)
    }


    fun onSearchAddressAutoCompleteClick() {
        mainNavigationEvent.postValue(NavigationEventType.OPEN_ADDRESS_AUTO_COMPLETE)
    }

    fun onSaveNewAddressClick() {
        actionEvent.postValue(ActionEvent.SAVE_NEW_ADDRESS)
    }

    fun redirectFinalDetailsToMap() {
        mainNavigationEvent.postValue(NavigationEventType.OPEN_MAP_VERIFICATION_FROM_FINAL_DETAILS)
    }

    fun openAddressMapVerificationWithMyLocation() {
        val myLocationAddress = getLocationLiveData().value
        myLocationAddress?.let{
            unsavedNewAddress = it.copy()
            mainNavigationEvent.postValue(NavigationEventType.OPEN_MAP_VERIFICATION_SCREEN)
        }
    }

    fun updateAutoCompleteAddressFound(place: Place) {
        //called when user select address via auto complete
        val address = GoogleAddressParserUtil.parsePlaceToAddressRequest(place)
        address.let{
            unsavedNewAddress = address
            mainNavigationEvent.postValue(NavigationEventType.OPEN_MAP_VERIFICATION_SCREEN)
            addressFoundUiEvent.postValue(it)
        }
    }

    fun checkIntentParam(intent: Intent?) {
        intent?.let {
            if (it.hasExtra(Constants.START_WITH)) {
                when (it.getIntExtra(Constants.START_WITH, Constants.NOTHING)) {
                    Constants.START_WITH_ADDRESS_CHOOSER -> {
                        mainNavigationEvent.postValue(NavigationEventType.OPEN_ADDRESS_LIST_CHOOSER)
                    }
                }
            }
        }
    }

    fun onLocationPermissionDone() {
        mainNavigationEvent.postValue(NavigationEventType.LOCATION_PERMISSION_DONE)
    }

    fun onAddressMapVerificationDone() {
        mainNavigationEvent.postValue(NavigationEventType.OPEN_FINAL_ADDRESS_DETAILS_SCREEN)
    }



    fun updateUnsavedAddressLatLng(currentLatLng: LatLng) {
        unsavedNewAddress?.let{
            it.lat = currentLatLng.latitude
            it.lng = currentLatLng.longitude
            unsavedNewAddress = it
        }
    }

    val newAddressLiveData = MutableLiveData<AddressRequest>()
    fun initMapLocation(){
        unsavedNewAddress?.let{
            newAddressLiveData.postValue(it)
        }
    }

    fun updateDeliveryMethod(deliveryMethod: String) {
        unsavedNewAddress?.let{
            it.dropoffLocation = deliveryMethod
        }
    }

    fun saveNewAddress(note: String?) {
        progressData.startProgress()
        unsavedNewAddress?.let{
            it.streetLine1 = "${it.streetNumber} ${it.streetLine1}"
            it.notes = note
            viewModelScope.launch {
                val userRepoResult = userRepository.addNewAddress(it)
                progressData.endProgress()
                when (userRepoResult.type) {
                    UserRepository.UserRepoStatus.SERVER_ERROR -> {
                        Log.d(TAG, "NetworkError")
                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                    UserRepository.UserRepoStatus.SOMETHING_WENT_WRONG -> {
                        Log.d(TAG, "GenericError")
                        errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
                    }
                    UserRepository.UserRepoStatus.SUCCESS -> {
                        Log.d(TAG, "Success")
                        userRepoResult.eater?.addresses?.get(0)?.let{ address ->
                            eaterDataManager.updateSelectedAddress(address)
                        }
                        mainNavigationEvent.postValue(NavigationEventType.LOCATION_AND_ADDRESS_DONE)
                    }
                    else -> {
                        Log.d(TAG, "NetworkError")
                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                }

            }
        }
    }

    companion object{
        const val TAG = "wowLocationAndAddressVM"
    }


}
