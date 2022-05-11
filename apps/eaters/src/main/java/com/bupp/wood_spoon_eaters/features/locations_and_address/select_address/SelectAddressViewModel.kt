package com.bupp.wood_spoon_eaters.features.locations_and_address.select_address

import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.common.UserSettings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.AddressRequest
import kotlinx.coroutines.launch

class SelectAddressViewModel(val settings: UserSettings, val eaterDataManager: EaterDataManager, val eatersAnalyticsTracker: EatersAnalyticsTracker) : ViewModel(){

    fun getFinalAddressParams() = eaterDataManager.getFinalAddressLiveDataParam()

    val navigationEvent = LiveEventData<NavigationEventType>()
    enum class NavigationEventType{
        OPEN_LOCATION_PERMISSION_SCREEN,
        OPEN_GPS_SETTINGS,
        OPEN_MAP_VERIFICATION_WITH_MY_LOCATION
    }

    val myLocationEvent: MutableLiveData<MyLocationStatus> = MutableLiveData()
    enum class MyLocationStatus{
        FETCHING,
        NO_GPS_ENABLED,
        NO_PERMISSION,
        READY
    }

    fun updateMyLocationUiState(locationEnabled: Boolean = true) {
        if(!locationEnabled){
            myLocationEvent.postValue(MyLocationStatus.NO_GPS_ENABLED)
        }else if(!settings.hasGPSPermission){
            myLocationEvent.postValue(MyLocationStatus.NO_PERMISSION)
        }else{
            myLocationEvent.postValue(MyLocationStatus.FETCHING)
        }
    }

    fun onMyLocationClick() {
        when(myLocationEvent.value){
            MyLocationStatus.NO_GPS_ENABLED -> {
                navigationEvent.postRawValue(NavigationEventType.OPEN_GPS_SETTINGS)
            }
            MyLocationStatus.NO_PERMISSION -> {
                navigationEvent.postRawValue(NavigationEventType.OPEN_LOCATION_PERMISSION_SCREEN)
            }
            MyLocationStatus.FETCHING -> {
                //do nothing
            }
            MyLocationStatus.READY -> {
                navigationEvent.postRawValue(NavigationEventType.OPEN_MAP_VERIFICATION_WITH_MY_LOCATION)
            }
        }

    }

    data class AddressAdapterWrapper(val address: List<Address>?, val currentAddress: Address? = null)
    val myAddressesEvent = SingleLiveEvent<AddressAdapterWrapper>()
    fun fetchAddress() {
        if(eaterDataManager.currentEater == null){
            viewModelScope.launch {
                eaterDataManager.refreshCurrentEater()
                val currentAddress = eaterDataManager.getLastChosenAddress()
                val addresses = eaterDataManager.currentEater?.addresses
                myAddressesEvent.postValue(AddressAdapterWrapper(addresses, currentAddress))
            }
        }else{
            val currentAddress = eaterDataManager.getLastChosenAddress()
            val addresses = eaterDataManager.currentEater?.addresses
            myAddressesEvent.postValue(AddressAdapterWrapper(addresses, currentAddress))
        }
    }

    fun onMyLocationReceived(addressRequest: AddressRequest) {
        myLocationEvent.postValue(MyLocationStatus.READY)
        eaterDataManager.updateLocationIfNeeded(addressRequest)
    }

//    fun onAddressSelected(selectedAddress: Address) {
//        eaterDataManager.updateSelectedAddress(selectedAddress)
//    }

    fun updateSelectedAddressUi(selectedAddressId: Long?) {
//        fetchAddress(selectedAddressId)
    }




    companion object{
        const val TAG = "wowSelectAddressVM"
    }


}
