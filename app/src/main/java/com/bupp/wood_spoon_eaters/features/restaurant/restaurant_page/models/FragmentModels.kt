package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.model.AvailabilityDate
import com.bupp.wood_spoon_eaters.model.CookingSlot
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.WSImage
import kotlinx.android.parcel.Parcelize
import java.util.*

data class DeliveryDate(val date: Date, val cookingSlots: MutableList<CookingSlot>)

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
data class ExtrasDishPage(
    val menuItem: MenuItem,
    val cookingSlot: CookingSlot?,
//    val currentSelectedDate: Long? = null,
//    val availability: AvailabilityDate? = null
) : Parcelable