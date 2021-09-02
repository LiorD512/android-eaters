package com.bupp.wood_spoon_eaters.features.locations_and_address

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.Constants.Companion.EVENT_LOCATION_PERMISSION
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.managers.location.LocationManager
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.bupp.wood_spoon_eaters.model.ErrorEventType
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.bupp.wood_spoon_eaters.utils.google_api_utils.GeoCoderUtil
import com.bupp.wood_spoon_eaters.utils.google_api_utils.GoogleAddressParserUtil
import com.bupp.wood_spoon_eaters.utils.google_api_utils.LoadDataCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import kotlinx.coroutines.launch

class LocationAndAddressViewModel(val eaterDataManager: EaterDataManager, private val userRepository: UserRepository, private val eventsManager: EventsManager, private val flowEventsManager: FlowEventsManager) : ViewModel() {


    val progressData = ProgressData()
    val errorEvents: MutableLiveData<ErrorEventType> = MutableLiveData()

    val mainNavigationEvent = MutableLiveData<NavigationEventType>()
    enum class NavigationEventType {
        OPEN_ADDRESS_LIST_CHOOSER,
        OPEN_ADDRESS_AUTO_COMPLETE,
        OPEN_LOCATION_PERMISSION_SCREEN,
        OPEN_MAP_VERIFICATION_SCREEN,
        OPEN_FINAL_ADDRESS_DETAILS_SCREEN,
        OPEN_MAP_VERIFICATION_FROM_FINAL_DETAILS,
        LOCATION_PERMISSION_GUARENTEED,
        LOCATION_AND_ADDRESS_DONE
    }

    fun getLocationLiveData() = eaterDataManager.getLocationData()


    fun onDoneClick(selectedAddress: Address?) {
        selectedAddress?.let{
            eaterDataManager.updateSelectedAddress(selectedAddress, LocationManager.AddressDataType.FULL_ADDRESS)
            eaterDataManager.refreshSegment()
        }
        mainNavigationEvent.postValue(NavigationEventType.LOCATION_AND_ADDRESS_DONE)
    }

    private var unsavedNewAddress: AddressRequest? = null
    val actionEvent = MutableLiveData<ActionEvent>()
    enum class ActionEvent {
        SAVE_NEW_ADDRESS,
        FETCH_MY_ADDRESS,
        REFRESH_MY_LOCATION_STATE,
    }

    val addressFoundUiEvent = MutableLiveData<AddressRequest>()

    fun updateMyAddresses(){
        actionEvent.postValue(ActionEvent.FETCH_MY_ADDRESS)
    }
    fun updateMyLocationStats(){
        actionEvent.postValue(ActionEvent.REFRESH_MY_LOCATION_STATE)
    }

    fun showLocationPermissionScreen() {
        mainNavigationEvent.postValue(NavigationEventType.OPEN_LOCATION_PERMISSION_SCREEN)
    }

    fun onSearchAddressAutoCompleteClick() {
        mainNavigationEvent.postValue(NavigationEventType.OPEN_ADDRESS_AUTO_COMPLETE)
    }

    fun redirectFinalDetailsToMap() {
        mainNavigationEvent.postValue(NavigationEventType.OPEN_MAP_VERIFICATION_FROM_FINAL_DETAILS)
    }

    fun openAddressMapVerificationWithMyLocation() {
        val myLocationAddress = getLocationLiveData().value
        myLocationAddress?.let{
            unsavedNewAddress = it.copy()
            mainNavigationEvent.postValue(NavigationEventType.OPEN_MAP_VERIFICATION_SCREEN)
            addressFoundUiEvent.postValue(it)
        }
    }

    fun updateAutoCompleteAddressFound(context: Context, place: Place) {
        //called when user select address via auto complete
        val address = GoogleAddressParserUtil.parsePlaceToAddressRequest(place)
        if(validateAddressParams(address)){
            saveAndPostAddress(address)
        }else{
            geoCodeAddress(context, address)
        }

    }

    private fun geoCodeAddress(context: Context, address: AddressRequest) {
        address.lat?.let{
            address.lng?.let{
                GeoCoderUtil.execute(context, address, object :
                    LoadDataCallback<AddressRequest> {
                    override fun onDataLoaded(response: AddressRequest) {
                        if(validateAddressParams(response)){
                            saveAndPostAddress(response)
                        }else{
                            val dummyAddress = GeoCoderUtil.parseDummyAddress(address)
                            saveAndPostAddress(dummyAddress)
                        }
                    }
                    override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                        val dummyAddress = GeoCoderUtil.parseDummyAddress(address)
                        saveAndPostAddress(dummyAddress)
                    }
                })
            }
        }
    }

    private fun validateAddressParams(address: AddressRequest): Boolean {
        return address.streetLine1 != null && address.streetNumber != null
    }

    fun saveAndPostAddress(address: AddressRequest){
        address.let{
            unsavedNewAddress = address
            mainNavigationEvent.postValue(NavigationEventType.OPEN_MAP_VERIFICATION_SCREEN)
            addressFoundUiEvent.postValue(it)
        }
    }

    fun onLocationPermissionDone() {
        mainNavigationEvent.postValue(NavigationEventType.LOCATION_PERMISSION_GUARENTEED)
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

    fun saveNewAddress(note: String?, apt: String?, city: String?, state: String?, zipCode: String?) {
        progressData.startProgress()
        unsavedNewAddress?.let{
            it.streetLine1 = "${it.streetNumber} ${it.streetLine1}"
            it.streetLine2 = apt
            it.notes = note
            if(it.cityName == null){
                it.cityName = city
            }
            if(it.stateIso == null){
                it.stateIso = state
            }
            if(it.zipCode == null){
                it.zipCode = zipCode
            }
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
                            eaterDataManager.updateSelectedAddress(address, LocationManager.AddressDataType.FULL_ADDRESS)
                            eaterDataManager.refreshSegment()
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

    fun onReEnterAddressClick() {
        mainNavigationEvent.postValue(NavigationEventType.OPEN_ADDRESS_LIST_CHOOSER)
    }

    fun locationPermissionEvent(isAllowPermission: Boolean) {
        eventsManager.logEvent(EVENT_LOCATION_PERMISSION, getEventData(isAllowPermission))
    }


    private fun getEventData(isAllow: Boolean): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["permitted"] = isAllow.toString()
        return data
    }

    fun logPageEvent(eventType: FlowEventsManager.FlowEvents) {
        flowEventsManager.logPageEvent(eventType)
    }

    companion object{
        const val TAG = "wowLocationAndAddressVM"
    }


}
