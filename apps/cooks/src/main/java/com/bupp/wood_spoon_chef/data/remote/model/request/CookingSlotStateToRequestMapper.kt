package com.bupp.wood_spoon_chef.data.remote.model.request

import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotRequest
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.CreateCookingSlotNewState

class CookingSlotStateToRequestMapper {

    fun mapStateToCreateCookingSlotRequest(
        startsTime: Long?,
        endTime: Long?,
        lastCallForOrder: Long?,
        recurringRule: String?
    ) = CookingSlotRequest(
        eventId = null,
        startsAt = startsTime?.div(1000),
        endsAt = endTime?.div(1000),
        lastCallAt = lastCallForOrder?.div(1000),
        freeDelivery = false,
        worldwide = false,
        recurringSlot = false,
        menuItems = mutableListOf(),
        recurringRule = recurringRule,
        submit = false
    )

}