package com.bupp.wood_spoon_chef.data.remote.model.request

import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotRequest

class CookingSlotRequestMapper {

    fun mapCookingSlotToRequest(
        startsTime: Long?,
        endTime: Long?,
        lastCallForOrderShift: Long?,
        recurringRule: String?
    ) = CookingSlotRequest(
        eventId = null,
        startsAt = startsTime?.div(1000),
        endsAt = endTime?.div(1000),
        lastCallAt = endTime?.div(1000)?.minus(lastCallForOrderShift?.div(1000) ?: 0),
        freeDelivery = false,
        worldwide = false,
        menuItems = mutableListOf(),
        recurringRule = recurringRule,
        submit = false
    )

}