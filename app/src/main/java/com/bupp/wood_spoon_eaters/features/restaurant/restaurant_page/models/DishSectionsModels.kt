package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models

import com.bupp.wood_spoon_eaters.model.Dish

sealed class DishSections(
    val viewType: Int
)

data class DishSectionAvailableHeader(
    val header: String,
) : DishSections(viewType = 0) {
    companion object {
        const val viewType = 0
    }
}

class DishSectionUnavailableHeader
 : DishSections(viewType = 1) {
    companion object {
        const val viewType = 1
    }
}

data class DishSectionSingleDish(
    val dish: Dish,
    val hideSeparator: Boolean = false
) : DishSections(viewType = 2) {
    companion object {
        const val viewType = 2
    }
}
