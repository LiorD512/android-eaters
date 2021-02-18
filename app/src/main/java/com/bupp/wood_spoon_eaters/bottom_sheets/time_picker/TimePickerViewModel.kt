package com.bupp.wood_spoon_eaters.bottom_sheets.time_picker

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import java.util.*


class TimePickerViewModel(
    private val deliveryTimeManager: DeliveryTimeManager
) : ViewModel() {

    fun setDeliveryTime(date: Date?, isTempDeliveryTime: Boolean = false) {
        if(isTempDeliveryTime){
            //set temporary time when user change deliveryTime from singleDishInfo - set temp so that global time wont change (and mess feed)
            deliveryTimeManager.setTemporaryDeliveryTimeDate(date)
        }else{
            deliveryTimeManager.setNewDeliveryTime(date)
        }
    }
}