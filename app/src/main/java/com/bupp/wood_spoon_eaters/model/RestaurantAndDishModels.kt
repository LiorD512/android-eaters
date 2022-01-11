package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

data class SortedCookingSlots(val date: Date, val cookingSlots: MutableList<CookingSlot>)

@Parcelize
data class RestaurantInitParams(
    var restaurantId: Long?,
    var chefThumbnail: WSImage?,
    var coverPhoto: WSImage?,
    var rating: String?,
    var restaurantName: String?,
    var chefName: String?,
    var isFavorite: Boolean,
    var query: String? = null,
    var isFromSearch: Boolean = false,

    /** ANALYTICS PARAMS **/
    val cookingSlot: FeedDishCookingSlot? = null,
    val sectionTitle: String? = null,
    val sectionOrder: Int? = null,
    val restaurantOrderInSection: Int? = null,
    val dishIndexInRestaurant: Int? = null,
): Parcelable

@Parcelize
data class DishInitParams(
    val quantityInCart: Int = 0,
    val menuItem: MenuItem? = null,
    val orderItem: OrderItem? = null,
    val cookingSlot: CookingSlot? = null,

    /** ANALYTICS PARAMS **/
    val dishSectionTitle: String? = null,
    val dishOrderInSection: Int? = null,
) : Parcelable