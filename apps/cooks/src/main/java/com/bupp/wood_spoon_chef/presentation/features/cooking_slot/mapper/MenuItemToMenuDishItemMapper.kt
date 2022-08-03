package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper

import com.bupp.wood_spoon_chef.data.remote.model.MenuItem
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MenuDishItem

class MenuItemToMenuDishItemMapper {

    fun map(menuItem: MenuItem) = MenuDishItem(
        menuItemId = menuItem.id,
        dish = menuItem.dish,
        quantity = menuItem.quantity,
        unitsSold = menuItem.unitsSold ?: 0
    )
}