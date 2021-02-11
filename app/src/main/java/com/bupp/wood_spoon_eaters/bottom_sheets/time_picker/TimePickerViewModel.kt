package com.bupp.wood_spoon_eaters.bottom_sheets.time_picker

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import java.util.*


class TimePickerViewModel(
    private val deliveryTimeManager: DeliveryTimeManager
) : ViewModel() {

    fun setDeliveryTime(date: Date?) {
        deliveryTimeManager.setNewDeliveryTime(date)
    }
}