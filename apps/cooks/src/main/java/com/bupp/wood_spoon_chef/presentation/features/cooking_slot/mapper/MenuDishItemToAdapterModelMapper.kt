package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper

import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MenuDishItem
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.CookingSlotMenuAdapterItem

class MenuDishItemToAdapterModelMapper {

    fun map(menuItemList: MenuDishItem): CookingSlotMenuAdapterItem.MenuAdapterItem =
        CookingSlotMenuAdapterItem.MenuAdapterItem(
            menuItemList,
            CookingSlotMenuAdapterItem.CookingSlotMenuAdapterType.TYPE_MENU_ITEM
        )
}