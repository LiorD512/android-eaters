package com.bupp.wood_spoon_eaters.features.locations_and_address

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.model.Address
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.features.locations_and_address.select_address.SelectAddressViewModel
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.location.LocationLiveData
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.bupp.wood_spoon_eaters.utils.GoogleAddressParserUtil
import com.bupp.wood_spoon_eaters.utils.LocationUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place

class LocationAndAddressViewModel(val eaterDataManager: EaterDataManager, val settings: AppSettings) : ViewModel() {

    val locationPermissionActionEvent: MutableLiveData<Boolean> = MutableLiveData()
    val mainNavigationEvent = MutableLiveData<NavigationEventType>()
    enum class NavigationEventType {
        OPEN_EDIT_ADDRESS_SCREEN,
        OPEN_ADDRESS_LIST_CHOOSER,
        OPEN_ADD_NEW_ADDRESS_SCREEN,
        OPEN_ADDRESS_AUTO_COMPLETE,
        DONE_WITH_LOCATION_AND_ADDRESS,
        OPEN_LOCATION_PERMISSION_SCREEN,
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
//    val myLocationUpdateEvent: MutableLiveData<>
//    fun onMyLocationUpdate(locationDataWrapper: LocationLiveData.LocationDataWrapper?) {
//
//    }
//    data class MyLocationEvent(val status: MyLocationStatus, val address: Address? = null)
//    enum class MyLocationStatus{
//        FETCHING,
//        NO_PERMISSION,
//        HAS_LOCATION
//    }
//    val myLocationEvent: MutableLiveData<MyLocationEvent> = MutableLiveData()
//
//    fun onLocationUpdate(locationData: LocationLiveData.LocationDataWrapper) {
//        val address = locationData.address
//        if(locationData.isFinal){
//            myLocationEvent.postValue(MyLocationEvent(MyLocationStatus.HAS_LOCATION, address))
//        }
//    }
//
//    fun fetchMyLocation() {
//        if(!settings.hasGPSPermission){
//            myLocationEvent.postValue(MyLocationEvent(MyLocationStatus.NO_PERMISSION))
//        }else{
//            myLocationEvent.postValue(MyLocationEvent(MyLocationStatus.FETCHING))
////            val myLocation = getLocationLiveData().value
////            if (myLocation != null) {
//////            eaterDataManager.setUserChooseSpecificAddress(false)
////                myLocationEvent.postValue( MyLocationEvent(MyLocationStatus.HAS_LOCATION, myLocation.address) )
////            } else {
////                Log.d(TAG, "no location found")
////            }
//        }
//
//    }


    private var unsavedNewAddress: AddressRequest? = null
    private lateinit var currentPlaceFound: Place
    val actionEvent = MutableLiveData<ActionEvent>()
    enum class ActionEvent {
        SAVE_NEW_ADDRESS,
    }

//    data class NavigationEvent(val type: NavigationEventType, val address: Address? = null)
//    data class AddressFoundEvent(val address: Address? = null)
    val addressFoundEvent = MutableLiveData<AddressRequest>()




    fun showLocationPermissionScreen() {
        mainNavigationEvent.postValue(NavigationEventType.OPEN_LOCATION_PERMISSION_SCREEN)
    }

    fun onChangeLocationClick() {
        mainNavigationEvent.postValue(NavigationEventType.OPEN_ADDRESS_LIST_CHOOSER)
    }

    fun onAddressChooserDone() {
        mainNavigationEvent.postValue(NavigationEventType.DONE_WITH_LOCATION_AND_ADDRESS)
    }

    fun onEditAddressClick(address: Address) {
//        mainNavigationEvent.postValue(NavigationEvent(NavigationEventType.OPEN_EDIT_ADDRESS_SCREEN, address))
    }

    fun onAddNewAddressClick() {
        mainNavigationEvent.postValue(NavigationEventType.OPEN_ADD_NEW_ADDRESS_SCREEN)
    }

    fun onSearchAddressAutoCompleteClick() {
        mainNavigationEvent.postValue(NavigationEventType.OPEN_ADDRESS_AUTO_COMPLETE)
    }

    fun onSaveNewAddressClick() {
        actionEvent.postValue(ActionEvent.SAVE_NEW_ADDRESS)
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


    fun updateAutoCompleteAddressFound(place: Place) {
        //called when user select address via auto complete
        val address = GoogleAddressParserUtil.parseLocationToAddress(place)
        address?.let{
            unsavedNewAddress = address
            addressFoundEvent.postValue(it)
        }
    }

    val markerDistanceEvent = MutableLiveData<Boolean>()
    fun checkCenterLatLngPosition(centerLatLng: LatLng) {
            unsavedNewAddress?.let{ addressPosition ->
                addressPosition.lat?.let { lat ->
                    addressPosition.lng?.let { lng ->
                        val isInRadius = LocationUtils.isLocationsNear(lat, lng, centerLatLng.latitude, centerLatLng.longitude, 20)
                        markerDistanceEvent.postValue(isInRadius)
                    }
                }
            }

    }


}
