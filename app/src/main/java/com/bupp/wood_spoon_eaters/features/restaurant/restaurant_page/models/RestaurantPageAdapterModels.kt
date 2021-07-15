package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models

import com.bupp.wood_spoon_eaters.model.CookingSlot
import com.bupp.wood_spoon_eaters.model.CuisineLabel
import com.bupp.wood_spoon_eaters.model.Dish
import java.util.*

sealed class RestaurantPageSections(
    val viewType: Int
)

data class RPSectionMainInfo(
    val about: String,
    val cuisines: List<CuisineLabel>
) : RestaurantPageSections(viewType = 0) {
    companion object {
        const val viewType = 0
    }
}

data class DeliveryDate(val date: Date, val cookSlots: MutableList<CookingSlot>)
data class RPSectionDeliveryTiming(
    val deliveryDates: List<DeliveryDate>?
) : RestaurantPageSections(viewType = 1) {
    companion object {
        const val viewType = 1
    }
}

data class RPSectionSearch(
    val string: String
) : RestaurantPageSections(viewType = 2) {
    companion object {
        const val viewType = 2
    }
}

data class RPSectionDishList(
    val dishSectionList: List<DishListSections>
) : RestaurantPageSections(viewType = 3) {
    companion object {
        const val viewType = 3
    }
}