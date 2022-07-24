package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper

import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.MenuItem
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MenuDishItem
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.OperatingHours
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotDraft

class OriginalCookingSlotToDraftCookingSlotMapper {

    fun mapOriginalCookingSlotToDraft(originalCookingSlot: CookingSlot) =
        with(originalCookingSlot) {
            CookingSlotDraft(
                selectedDate = originalCookingSlot.startsAt.time,
                operatingHours = OperatingHours(
                    originalCookingSlot.startsAt.time, originalCookingSlot.endsAt.time
                ),
                lastCallForOrder = originalCookingSlot.lastCallAt?.time,
                recurringRule = null,
                menuItems = convertMenuItemsToMenuDishItem(originalCookingSlot.menuItems),
                originalCookingSlot = originalCookingSlot
            )
        }


    private fun convertMenuItemsToMenuDishItem(menuItem: List<MenuItem>): List<MenuDishItem> {
        return menuItem.map { menuItem ->
            MenuDishItem(
                menuItem.dish,
                menuItem.quantity
            )
        }.toList()
    }
}
