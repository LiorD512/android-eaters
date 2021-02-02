package com.bupp.wood_spoon_eaters.features.bottom_sheets.address_menu

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.model.Country
import com.bupp.wood_spoon_eaters.model.State
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import java.util.*


class AddressMenuViewModel(
    private val deliveryTimeManager: DeliveryTimeManager
) : ViewModel() {

    fun setDeliveryTime(date: Date?) {
        deliveryTimeManager.setNewDeliveryTime(date)
    }
}