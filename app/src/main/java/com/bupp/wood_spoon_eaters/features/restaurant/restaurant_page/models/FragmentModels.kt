package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.model.*
import kotlinx.android.parcel.Parcelize
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
    var isFavorite: Boolean
): Parcelable

@Parcelize
data class DishInitParams(
    val menuItem: MenuItem?,
    val orderItem: OrderItem?,
    val cookingSlot: CookingSlot?,
    val finishToFeed: Boolean = false
) : Parcelable