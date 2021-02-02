package com.bupp.wood_spoon_eaters.features.locations_and_address.delivery_details

import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.model.Address
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import java.util.*

class DeliveryDetailsViewModel(val settings: AppSettings, val eaterDataManager: EaterDataManager, val eventsManager: EventsManager, val deliveryTimeManager: DeliveryTimeManager) : ViewModel(){

    data class LastDataEvent(val address: Address?, val time: Date?)
    val lastDeliveryDetails: SingleLiveEvent<LastDataEvent> = SingleLiveEvent()

    fun getLastDeliveryDetails() {
        val address = eaterDataManager.getLastChosenAddress()
        val time = deliveryTimeManager.getDeliveryTimeDate()
        lastDeliveryDetails.postValue(LastDataEvent(address, time))
    }

    fun setDeliveryTime(time: Date) {
        eaterDataManager.setUserChooseSpecificTime(true)
//        eaterDataManager.orderTime = time
        deliveryTimeManager.setNewDeliveryTime(time)
        eventsManager.logUxCamEvent(Constants.UXCAM_EVENT_FUTURE_DELIVERY)
    }

    fun setDeliveryTimeAsap() {
        eaterDataManager.setUserChooseSpecificTime(false)
//        eaterDataManager.orderTime = null
        deliveryTimeManager.setNewDeliveryTime(null)
    }

}
