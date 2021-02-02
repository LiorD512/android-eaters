package com.bupp.wood_spoon_eaters.features.locations_and_address.select_address

import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.common.AppSettings
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.model.Address

class SelectAddressViewModel(val settings: AppSettings, val eaterDataManager: EaterDataManager, val eventsManager: EventsManager, val deliveryTimeManager: DeliveryTimeManager) : ViewModel(){

    val navigationEvent = LiveEventData<NavigationEventType>()
    enum class NavigationEventType{
        OPEN_LOCATION_PERMISSION_SCREEN,
        OPEN_GPS_SETTINGS
    }

    data class MyLocationEvent(val status: MyLocationStatus)
    enum class MyLocationStatus{
        FETCHING,
        NO_GPS_ENABLED,
        NO_PERMISSION,
    }
    val myLocationEvent: MutableLiveData<MyLocationEvent> = MutableLiveData()

    fun updateMyLocationUiState(locationEnabled: Boolean = true) {
        if(!locationEnabled){
            myLocationEvent.postValue(MyLocationEvent(MyLocationStatus.NO_GPS_ENABLED))
        }else if(!settings.hasGPSPermission){
            myLocationEvent.postValue(MyLocationEvent(MyLocationStatus.NO_PERMISSION))
        }else{
            myLocationEvent.postValue(MyLocationEvent(MyLocationStatus.FETCHING))
        }
    }

    fun onMyLocationClick() {
        when(myLocationEvent.value?.status){
            MyLocationStatus.NO_GPS_ENABLED -> {
                navigationEvent.postRawValue(NavigationEventType.OPEN_GPS_SETTINGS)
            }
            MyLocationStatus.NO_PERMISSION -> {
                navigationEvent.postRawValue(NavigationEventType.OPEN_LOCATION_PERMISSION_SCREEN)
            }
            MyLocationStatus.FETCHING -> {
                selectLocation()
            }
        }

    }

    val myAddressEvent = SingleLiveEvent<List<Address>?>()
    fun fetchAddress() {
        myAddressEvent.postValue(eaterDataManager.currentEater?.addresses)
    }

    private fun selectLocation() {

    }



    companion object{
        const val TAG = "wowSelectAddressVM"
    }


}
