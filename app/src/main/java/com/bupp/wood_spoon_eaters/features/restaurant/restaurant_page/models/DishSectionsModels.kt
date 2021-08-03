package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models

import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapterItem
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionsViewType.*
import com.bupp.wood_spoon_eaters.model.Dish

sealed class DishSections(
    val viewType: DishSectionsViewType
) : SwipeableAdapterItem()

data class DishSectionAvailableHeader(
    val header: String,
    override val dish: Dish? = null,
    override var quantity: Int = 0
) : DishSections(viewType = AVAILABLE_HEADER) {
    companion object {
        val viewType = AVAILABLE_HEADER
    }
}

class DishSectionUnavailableHeader(
    override val dish: Dish? = null,
    override var quantity: Int = 0
) : DishSections(viewType = UNAVAILABLE_HEADER) {
    companion object {
        val viewType = UNAVAILABLE_HEADER
    }
}

data class DishSectionSingleDish(
    override val dish: Dish,
    override var quantity: Int = 0
) : DishSections(viewType = SINGLE_DISH) {
    companion object {
        val viewType = SINGLE_DISH
    }
}

class DishSectionSkeleton(
    override val dish: Dish? = null,
    override var quantity: Int = 0
) : DishSections(viewType = SKELETON) {
    companion object {
        val viewType = SKELETON
    }
}

enum class DishSectionsViewType {
    AVAILABLE_HEADER,
    UNAVAILABLE_HEADER,
    SINGLE_DISH,
    SKELETON
}
