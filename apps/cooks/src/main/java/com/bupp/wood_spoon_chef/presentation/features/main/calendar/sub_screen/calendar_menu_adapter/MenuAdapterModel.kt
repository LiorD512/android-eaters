package com.bupp.wood_spoon_chef.presentation.features.main.calendar.sub_screen.calendar_menu_adapter

data class MenuAdapterModel(
        val type: MenuAdapterViewType,
        val dishId: Long? = null,
        val img: String? = null,
        val name: String? = null,
        var quantity: Int? = null,
        val isUnlimited: Boolean = false,
        val isExpanded: Boolean = false
)

enum class MenuAdapterViewType {
    HEADER,
    ITEM
}