package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Dish(
    @SerializedName("id") val id: Long,
    @SerializedName("cook") val cook: Cook,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Price,
    @SerializedName("avg_rating") val rating: Double,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("is_favorite") val isFavorite: Boolean,
    @SerializedName("is_recurring") val isRecurring: Boolean,
    @SerializedName("matching_menu") val menuItem: MenuItem?,
    @SerializedName("matching_slot") val matchingSlot: String,
    @SerializedName("door_to_door_time") val doorToDoorTime: String?
): Parcelable {
    fun getPriceObj(): Price {
        return if(menuItem?.price != null){
            menuItem.price
        }else{
            price
        }
    }
}

@Parcelize
data class MenuItem(
    @SerializedName("id") val id: Long,
    @SerializedName("price") val price: Price? = null,
    @SerializedName("quantity") val quantity: Int = 0,
    @SerializedName("units_sold") val unitsSold: Int = 0,
    @SerializedName("order_at") val orderAt: Date? = null,
    @SerializedName("cooking_slot") val cookingSlot: CookingSlot
): Parcelable{
    fun getQuantityLeft(): String{
        val left = quantity - unitsSold
        return "$left Left"
    }
}

@Parcelize
data class CookingSlot(
    @SerializedName("id") val id: Long,
    @SerializedName("ends_at") val endsAt: Date,
    @SerializedName("starts_at") val startsAt: Date,
    @SerializedName("free_delivery") val freeDelivery: Boolean
): Parcelable

data class FullDish(
    @SerializedName("id") val id: Long,
    @SerializedName("cook") val cook: Cook,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Price,
    @SerializedName("video") val video: String? = null,
    @SerializedName("avg_rating") val rating: Double,
    @SerializedName("proteins") val proteins: Double,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("is_favorite") val isFavorite: Boolean,
    @SerializedName("description") val description: String,
    @SerializedName("matching_menu") var menuItem: MenuItem?,
    @SerializedName("matching_slot") val matchingSlot: String,
    @SerializedName("carbohydrates") val carbohydrates: Double,
    @SerializedName("calorific_value") val calorificValue: Double,
    @SerializedName("door_to_door_time") val doorToDoorTime: String,
    @SerializedName("prep_time_range") val prepTimeRange: PrepTimeRange,
    @SerializedName("available_at") val availableMenuItems: ArrayList<MenuItem>,
    @SerializedName("image_gallery") val imageGallery: ArrayList<String>? = null,
    @SerializedName("cooking_methods") val cookingMethods: ArrayList<CookingMethods>,
    @SerializedName("dish_ingredients") val dishIngredients: ArrayList<DishIngredient>
){
    fun getPriceObj(): Price {
        if(availableMenuItems[0].price != null){
            return availableMenuItems[0].price!!
        }else{
            return price
        }
    }

    fun getMediaList(): List<MediaList> {
        val mediaList = arrayListOf<MediaList>()
        imageGallery?.forEach {
            mediaList.add(MediaList(it, true))
        }
        video?.let{
            mediaList.add(MediaList(it, false))
        }
        return mediaList
    }
}

data class MediaList(
    val media: String,
    val isImage: Boolean
)

@Parcelize
data class DishIngredient(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("quantity") val quantity: Double?,
    @SerializedName("is_adjustable") val isAdjustable: Boolean?,
    @SerializedName("unit") val unit: WoodUnit?,
    @SerializedName("ingredient") val ingredient: Ingredient?,
    @SerializedName("_remove") val _removeId: Long? = null
): Parcelable

@Parcelize
data class Ingredient(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("units") val unit: ArrayList<WoodUnit>
): Parcelable

data class CookingMethods(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String
)

@Parcelize
data class Price(
    @SerializedName("cents") val cents: Long,
    @SerializedName("value") val value: Double,
    @SerializedName("formatted") val formatedValue: String
): Parcelable

data class UpcomingSlot(
    @SerializedName("id") val id: Long,
    @SerializedName("status") val status: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("starts_at") val startsAt: Date,
    @SerializedName("units_sold") val unitSold: Int
)

