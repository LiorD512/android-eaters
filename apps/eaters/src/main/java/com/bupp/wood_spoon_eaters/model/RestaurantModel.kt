package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Restaurant(
    @Json(name = "id") val id: Long,
    @Json(name = "first_name") val firstName: String?,
    @Json(name = "last_name") val lastName: String?,
    @Json(name = "restaurant_name") val restaurantName: String?,
    @Json(name = "cover") val cover: WSImage?,
    @Json(name = "thumbnail") val thumbnail: WSImage?,
    @Json(name = "video") val video: String?,
    @Json(name = "flag_url") var flagUrl: String?,
    @Json(name = "availability") val availability: RestaurantAvailability?,
    @Json(name = "country_id") val countryId: Long?,
    @Json(name = "avg_rating") val rating: Float?,
    @Json(name = "reviews_count") var reviewCount: Int = 0,
    @Json(name = "about") val about: String?,
    @Json(name = "pickup_address") var pickupAddress: Address? = null,
    @Json(name = "is_favorite") val isFavorite: Boolean? = false,
    @Json(name = "can_be_delivered") val canBeDelivered: Boolean?,
    @Json(name = "share_url" ) val shareUrl: String? = "",
    @Json(name = "share_text") val shareText: String? = "",
    @Json(name = "tags") val tags: MutableList<String> = mutableListOf(),
    @Json(name = "dishes") val dishes: MutableList<Dish> = mutableListOf(),
    @Json(name = "cooking_slots") var cookingSlots: MutableList<CookingSlot> = mutableListOf(),

    ): Parcelable {
    fun getFullName(): String{
        return "$firstName $lastName"
    }

    fun getShareTextStr(): String{
        return "$shareText \n $shareUrl"
    }

    fun getAvgRating(): String{
        rating?.let{
            return String.format("%.1f", rating)
        }
        return ""
    }
}

enum class RestaurantAvailability{
    @Json(name = "available")
    AVAILABLE,
    @Json(name = "no_cooking_slots")
    NO_COOKING_SLOT,
    @Json(name = "other_location")
    OTHER_LOCATION,
}