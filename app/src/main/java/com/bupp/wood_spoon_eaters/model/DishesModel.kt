package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Dish(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("matching_slot") val matchingSlot: String,
    @SerializedName("is_favorite") val isFavorite: Boolean,
    @SerializedName("price") val price: Price,
    @SerializedName("cook") val cook: Cook,
    @SerializedName("door_to_door_time") val doorToDoorTime: String?,
    @SerializedName("matching_menu") val menuItem: MenuItem,
    @SerializedName("avg_rating") val rating: Double
//    @SerializedName("description") val description: String,
//    @SerializedName("upcoming_slot") val upcomingSlot: UpcomingSlot,
//    @SerializedName("calorific_value") val calorificValue: Double,
//    @SerializedName("proteins") val proteins: Double,
//    @SerializedName("prep_time_range") val prepTimeRange: PrepTimeRange,
//    @SerializedName("dish_ingredients") val dishIngredients: ArrayList<DishIngredient>,
//    @SerializedName("cooking_methods") val cookingMethods: ArrayList<CookingMethods>,
//    @SerializedName("carbohydrates") val carbohydrates: Double,
): Parcelable

@Parcelize
data class MenuItem(
    @SerializedName("id") val id: Long,
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
    @SerializedName("name") val name: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("price") val price: Price,
    @SerializedName("matching_menu") var menuItem: MenuItem?,
    @SerializedName("is_favorite") val isFavorite: Boolean,
    @SerializedName("avg_rating") val rating: Double,
    @SerializedName("cook") val cook: Cook,
    @SerializedName("description") val description: String,
    @SerializedName("door_to_door_time") val doorToDoorTime: String,
    @SerializedName("calorific_value") val calorificValue: Double,
    @SerializedName("proteins") val proteins: Double,
    @SerializedName("carbohydrates") val carbohydrates: Double,
    @SerializedName("matching_slot") val matchingSlot: String,
    @SerializedName("cooking_methods") val cookingMethods: ArrayList<CookingMethods>,
    @SerializedName("prep_time_range") val prepTimeRange: PrepTimeRange,
    @SerializedName("dish_ingredients") val dishIngredients: ArrayList<DishIngredient>,
    @SerializedName("available_at") val availableMenuItems: ArrayList<MenuItem>
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

