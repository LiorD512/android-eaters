package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models

import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionsViewType.*
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapterItem

sealed class DishSections(
    val viewType: DishSectionsViewType
) : SwipeableAdapterItem()

data class DishSectionAvailableHeader(
    val header: String,
    val subtitle: String,
    override val menuItem: MenuItem? = null,
    override var cartQuantity: Int = 0,
    override val isSwipeable: Boolean = false
) : DishSections(viewType = AVAILABLE_HEADER) {
    companion object {
        val viewType = AVAILABLE_HEADER
    }
}

class DishSectionUnavailableHeader(
    override val menuItem: MenuItem? = null,
    override var cartQuantity: Int = 0,
    override val isSwipeable: Boolean = false
) : DishSections(viewType = UNAVAILABLE_HEADER) {
    companion object {
        val viewType = UNAVAILABLE_HEADER
    }
}

data class DishSectionSingleDish(
    override val menuItem: MenuItem,
    override var cartQuantity: Int = 0,
    override val isSwipeable: Boolean = true
) : DishSections(viewType = SINGLE_DISH) {
    companion object {
        val viewType = SINGLE_DISH
    }
}

class DishSectionSkeleton(
    override val menuItem: MenuItem? = null,
    override var cartQuantity: Int = 0,
    override val isSwipeable: Boolean = false
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
