package com.bupp.wood_spoon_chef.data.remote.model.request

import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotRequest
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call.LastCall
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call.toTimestampBasedOnEndDate

class CookingSlotRequestMapper {

    fun mapCookingSlotToRequest(
        startsTime: Long?,
        endTime: Long?,
        lastCallForOrder: LastCall?,
        recurringRule: String?
    ) = CookingSlotRequest(
        eventId = null,
        startsAt = startsTime?.div(1000),
        endsAt = endTime?.div(1000),
        lastCallAt = lastCallForOrder?.toTimestampBasedOnEndDate(endTime)?.div(1000) ?: endTime?.div(1000),
        freeDelivery = false,
        worldwide = false,
        menuItems = mutableListOf(),
        recurringRule = recurringRule,
        submit = false
    )
}