package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

data class Dish(
    @SerializedName("id") val id: Long,
    @SerializedName("cook") val cook: Cook,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Price,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("is_favorite") val isFavorite: Boolean,
    @SerializedName("description") val description: String,
    @SerializedName("free_delivery") val freeDelivery: Boolean,
    @SerializedName("upcoming_slot") val upcomingSlot: UpcomingSlot,
    @SerializedName("calorific_value") val calorificValue: Double,
    @SerializedName("proteins") val proteins: Double,
    @SerializedName("prep_time_range") val prepTimeRange: PrepTimeRange,
    @SerializedName("dish_ingredients") val dishIngredients: ArrayList<DishIngredient>,
    @SerializedName("cooking_methods") val cookingMethods: ArrayList<CookingMethods>,
    @SerializedName("carbohydrates") val carbohydrates: Double,
    @SerializedName("avg_rating") val rating: Double,
    @SerializedName("removed_ingredients") val removedIngredients: ArrayList<DishIngredient>
)


@Parcelize
data class DishIngredient(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("quantity") val quantity: Int?,
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


data class Price(
    @SerializedName("cents") val cents: Long,
    @SerializedName("value") val value: Double,
    @SerializedName("formatted") val formatedValue: String
)

data class UpcomingSlot(
    @SerializedName("id") val id: Long,
    @SerializedName("status") val status: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("starts_at") val startsAt: Date,
    @SerializedName("units_sold") val unitSold: Int
)

data class CookingSlot(
    @SerializedName("id") val id: Long,
    @SerializedName("status") val status: Int,
    @SerializedName("ends_at") val endsAt: Date,
    @SerializedName("starts_at") val startsAt: Date,
    @SerializedName("menu_items") val menuItems: ArrayList<MenuItem>
)