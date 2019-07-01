package com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.utils.AppSettings

class AddAddressViewModel(private val appSettings: AppSettings) : ViewModel() {

    data class DeliveryDetails(
        var address: Address,
        var apartmentInfo: String,
        var deliveryNote: String,
        var isDelivery: Boolean
    )

    val deliveryDetails: SingleLiveEvent<DeliveryDetails> = SingleLiveEvent()


}