package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models

import com.bupp.wood_spoon_eaters.model.CuisineLabel
import com.bupp.wood_spoon_eaters.model.Dish

sealed class DishListSections(
    val viewType: Int
)

data class DishSectionAvailableHeader(
    val header: String,
) : DishListSections(viewType = 0) {
    companion object {
        const val viewType = 0
    }
}

class DishSectionUnavailableHeader
 : DishListSections(viewType = 1) {
    companion object {
        const val viewType = 1
    }
}

data class DishSectionSingleDish(
    val dish: Dish,
) : DishListSections(viewType = 2) {
    companion object {
        const val viewType = 2
    }
}
