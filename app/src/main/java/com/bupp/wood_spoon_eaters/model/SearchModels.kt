package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.common.Constants
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@JsonClass(generateAdapter = true)
data class SearchRequest(
    @Json(name = "lat") var lat: Double? = null,
    @Json(name = "lng") var lng: Double? = null,
    @Json(name = "address_id") var addressId: Long? = null,
    @Json(name = "timestamp") var timestamp: String? = null,
    @Json(name = "q") var q: String? = "",
    @Json(name = "cuisine_ids") var cuisineIds: MutableList<Long>? = null,
    @Json(name = "diet_ids") var dietIds: MutableList<Long>? = null,
    @Json(name = "min_price") var minPrice: Double? = null,
    @Json(name = "max_price") var maxPrice: Double? = null,
    @Json(name = "asap_only") var isAsap: Boolean? = null
)

sealed class Search(
    @Json(name = "resource") var resource: String?
): Parcelable {
    abstract val id: Long?
    abstract val results: List<Parcelable>?
    abstract val pagination: Pagination
    fun cooksCount(): Int {
        results?.let{
            when (resource) {
                Constants.RESOURCE_TYPE_COOK -> {
                    return results!!.size
                }
                else -> return 0
            }
        }
        return 0
    }

    fun dishCount(): Int {
        results?.let{
            when (resource) {
                Constants.RESOURCE_TYPE_DISH -> {
                    return results!!.size
                }
                else -> return 0
            }
        }
        return 0
    }

    fun hasCooks(): Boolean {
        return cooksCount() > 0
    }

    fun hasDishes(): Boolean {
        return dishCount() > 0
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class CookSection(
    override val id: Long?,
    override val results: List<Cook>?,
    override val pagination: Pagination

):Parcelable, Search(Constants.RESOURCE_TYPE_COOK)


@Parcelize
@JsonClass(generateAdapter = true)
data class DishSection(
    override val id: Long?,
    override val results: List<Dish>?,
    override val pagination: Pagination

):Parcelable, Search(Constants.RESOURCE_TYPE_DISH)

@Parcelize
@JsonClass(generateAdapter = true)
data class Pagination(
    @Json(name = "total_items") val totalItems: Int? = null,
    @Json(name = "item_count") val itemCount: Int? = null,
    @Json(name = "items_per_page") val itemsPerPage: Int? = null,
    @Json(name = "total_pages") val totalPages: Int? = null,
    @Json(name = "current_page") val currentPage: String? = null
):Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Cook(
    @Json(name = "id") val id: Long,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    @Json(name = "thumbnail") val thumbnail: String,
    @Json(name = "video") val video: String?,
    @Json(name = "profession") val profession: String?,
    @Json(name = "about") val about: String?,
    @Json(name = "birthdate") val birthdate: Date?,
    @Json(name = "pickup_address") var pickupAddress: Address? = null,
    @Json(name = "place_of_birth") val country: Country? = null,
    @Json(name = "certificates") val certificates: List<String>?,
    @Json(name = "cuisines") val cuisines: MutableList<CuisineLabel> = mutableListOf(),
    @Json(name = "diets") val diets: MutableList<DietaryIcon> = mutableListOf(),
    @Json(name = "avg_rating") val rating: Double?,
    @Json(name = "reviews_count") var reviewCount: Int = 0,
    @Json(name = "dishes") val dishes: MutableList<Dish> = mutableListOf()
): Parcelable {
    fun getFullName(): String{
        return "$firstName $lastName"
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class Dish(
    @Json(name = "id") val id: Long,
    @Json(name = "cook") val cook: Cook?,
    @Json(name = "name") val name: String,
    @Json(name = "price") val price: Price?,
    @Json(name = "description") val description: String,
    @Json(name = "avg_rating") val rating: Double?,
    @Json(name = "thumbnail") val thumbnail: String,
    @Json(name = "is_favorite") val isFavorite: Boolean?,
    @Json(name = "nationwide_shipping") val worldwide: Boolean?,
    @Json(name = "is_recurring") val isRecurring: Boolean?,
    @Json(name = "matching_menu") val menuItem: MenuItem?,
    @Json(name = "matching_slot") val matchingSlot: String?,
    @Json(name = "door_to_door_time") val doorToDoorTime: String?,
    @Json(name = "cuisines") val cuisines: List<CuisineLabel>?
): Parcelable{
    fun getPriceObj(): Price? {
        return if(menuItem?.price != null){
            menuItem.price
        }else{
            price
        }
    }

    fun isSoldOut(): Boolean {
        val quantityLeft = (menuItem?.quantity ?: 0) - (menuItem?.unitsSold ?: 0)
        return quantityLeft <= 0
    }
}