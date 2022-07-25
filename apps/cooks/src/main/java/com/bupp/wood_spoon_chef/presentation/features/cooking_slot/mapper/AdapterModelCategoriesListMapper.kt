package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper

import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.CookingSlotMenuAdapterItem

class AdapterModelCategoriesListMapper {

    fun map(
        adapterMenuItemList: List<CookingSlotMenuAdapterItem.MenuAdapterItem>
    ): MutableList<CookingSlotMenuAdapterItem> =
        mutableListOf<CookingSlotMenuAdapterItem>().apply {
            (adapterMenuItemList.groupBy { group ->
                group.menuItem.dish?.category?.name
            } as LinkedHashMap)
                .forEach { (groupName, v) ->
                    groupName?.let {
                        add(
                            CookingSlotMenuAdapterItem.SectionHeaderAdapterItem(
                                sectionTitle = groupName,
                                type = CookingSlotMenuAdapterItem.CookingSlotMenuAdapterType.TYPE_SECTION_HEADER
                            )
                        )
                    }
                    v.forEach { item ->
                        add(
                            CookingSlotMenuAdapterItem.MenuAdapterItem(
                                menuItem = item.menuItem,
                                type = CookingSlotMenuAdapterItem.CookingSlotMenuAdapterType.TYPE_MENU_ITEM
                            )
                        )
                    }
                }
        }
}