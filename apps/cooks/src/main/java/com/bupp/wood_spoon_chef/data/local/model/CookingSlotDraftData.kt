package com.bupp.wood_spoon_chef.data.local.model

import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MenuDishItem
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.OperatingHours
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.RecurringRule

data class CookingSlotDraftData(
    val selectedDate: Long? = null,
    val operatingHours: OperatingHours = OperatingHours(null, null),
    val lastCallForOrder: Long? = null,
    val recurringRule: RecurringRule? = null,
    val menuItems: List<MenuDishItem> = mutableListOf(),
    val originalCookingSlot: CookingSlot?,
    val isEditing: Boolean = false
) {
    val id = originalCookingSlot?.id
}
