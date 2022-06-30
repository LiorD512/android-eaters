package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper

import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotRequest
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.CreateCookingSlotNewState
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.RecurringRule

class CookingSlotStateMapper {

    fun mapStateToCreateCookingSlotRequest(state: CreateCookingSlotNewState) = with(state){
        CookingSlotRequest(
            eventId = null,
            startsAt = operatingHours.startTime?.div(1000),
            endsAt = operatingHours.endTime?.div(1000),
            lastCallAt = lastCallForOrder?.div(1000),
            freeDelivery = false,
            worldwide = false,
            recurringSlot = false,
            menuItems = mutableListOf(),
            recurringRule = formatRecurringRule(recurringRule),
            submit = false
        )
    }

    private fun formatRecurringRule(recurringRule: RecurringRule?): String{
        return "FREQ=${recurringRule?.frequency};COUNT=${recurringRule?.count}".uppercase()
    }
}