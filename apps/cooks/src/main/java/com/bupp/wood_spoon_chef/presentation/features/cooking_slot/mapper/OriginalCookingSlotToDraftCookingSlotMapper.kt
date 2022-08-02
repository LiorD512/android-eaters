package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper

import com.bupp.wood_spoon_chef.data.local.model.CookingSlotDraftData
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.OperatingHours

class OriginalCookingSlotToDraftCookingSlotMapper(
    private val menuItemToMenuDishItemMapper: MenuItemToMenuDishItemMapper
) {

    fun mapOriginalCookingSlotToDraft(originalCookingSlot: CookingSlot) = CookingSlotDraftData(
        selectedDate = originalCookingSlot.startsAt.time,
        operatingHours = OperatingHours(
            originalCookingSlot.startsAt.time, originalCookingSlot.endsAt.time
        ),
        lastCallForOrder = originalCookingSlot.lastCallAt?.time,
        recurringRule = originalCookingSlot.recurringRule,
        menuItems = originalCookingSlot.menuItems
            .map { menuItemToMenuDishItemMapper.map(it) }
            .toList(),
        originalCookingSlot = originalCookingSlot
    )
}