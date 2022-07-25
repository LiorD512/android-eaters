package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models

import com.bupp.wood_spoon_chef.data.remote.model.Dish
import com.bupp.wood_spoon_chef.data.remote.model.Section

data class MyDishesPickerAdapterModel(
    val section: Section?,
    val dishes: List<MyDishesPickerAdapterDish>?
)

data class MyDishesPickerAdapterDish(
    val dish: Dish?,
    val isSelected: Boolean = false
)

data class DishesMenuAdapterModel(
    val section: Section?,
    val dishes: List<MenuDishItem>
)

data class MenuDishItem(
    val dish: Dish?,
    val quantity: Int = 1,
    val unitsSold: Int = 0
)

