package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper

import com.bupp.wood_spoon_chef.data.local.model.CookingSlotDraftData
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.create_cooking_slot.OperatingHours
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call.LastCall
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call.from

class OriginalCookingSlotToDraftCookingSlotMapper(
    private val menuItemToMenuDishItemMapper: MenuItemToMenuDishItemMapper
) {

    fun mapOriginalCookingSlotToDraft(originalCookingSlot: CookingSlot) = CookingSlotDraftData(
        selectedDate = originalCookingSlot.startsAt.time,
        operatingHours = OperatingHours(
            originalCookingSlot.startsAt.time, originalCookingSlot.endsAt.time
        ),
        lastCallForOrder = LastCall.from(originalCookingSlot.lastCallAt?.time, originalCookingSlot.endsAt?.time).takeIf { originalCookingSlot.lastCallAt?.time != originalCookingSlot.endsAt.time  },
        recurringRule = originalCookingSlot.recurringRule,
        menuItems = originalCookingSlot.menuItems
            .map { menuItemToMenuDishItemMapper.map(it) }
            .toList(),
        originalCookingSlot = originalCookingSlot
    )
}