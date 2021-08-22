package com.bupp.wood_spoon_eaters.bottom_sheets.time_picker

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import java.util.*


class TimePickerViewModel(
    private val deliveryTimeManager: DeliveryTimeManager,
    private val eaterDataManager: EaterDataManager
) : ViewModel() {

    fun setDeliveryTime(date: Date?, isTempDeliveryTime: Boolean = false) {
        MTLogger.c(TAG, "changing delivery time -> date: $date, isTemp: $isTempDeliveryTime")
        if(isTempDeliveryTime){
            //set temporary time when user change deliveryTime from singleDishInfo - set temp so that global time wont change (and mess feed)
            deliveryTimeManager.setTemporaryDeliveryTimeDate(date)
        }else{
            deliveryTimeManager.setNewDeliveryTime(date)
        }
        date?.let{
            eaterDataManager.logUxCamEvent(Constants.EVENT_FUTURE_DELIVERY, getEventData(date))
        }
    }

    private fun getEventData(time: Date): Map<String, String> {
        val data = mutableMapOf<String, String>("delivery_time" to time.toString())
        return data
    }

    companion object{
        const val TAG = "wowTimePickerVM"
    }
}