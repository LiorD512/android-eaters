package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
@JsonClass(generateAdapter = true)
data class CookingSlot(
    @Json(name = "id") val id: Long,
    @Json(name = "ends_at") val endsAt: Date,
    @Json(name = "starts_at") val startsAt: Date,
    @Json(name = "order_from") val orderFrom: Date,
    @Json(name = "last_call_at") val lastCallAt: Date?,
    @Json(name = "delivery_fee") val deliveryFee: Price?,
    @Json(name = "free_delivery") val freeDelivery: Boolean,
    @Json(name = "nationwide_shipping") val isNationwide: Boolean?,
    @Json(name = "menu_items") val menuItems: List<MenuItem>?,
    val availableDishes: MutableList<Dish> = mutableListOf(),
    val unAvailableDishes: MutableList<Dish> = mutableListOf(),
): Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class MenuItem(
    @Json(name = "id") val id: Long,
    @Json(name = "price") val price: Price? = null,
    @Json(name = "quantity") val quantity: Int = 0,
    @Json(name = "units_sold") val unitsSold: Int = 0,
    @Json(name = "order_at") val orderAt: Date? = null,
    @Json(name = "cooking_slot") val cookingSlot: CookingSlot?
): Parcelable{
    fun getQuantityLeftString(): String{
        val left = quantity - unitsSold
        return "$left Left"
    }
    fun getQuantityCount(): Int{
        return quantity - unitsSold
    }
}

@JsonClass(generateAdapter = true)
data class FullDish(
    @Json(name = "id") val id: Long,
    @Json(name = "cook") val cook: Cook,
    @Json(name = "name") val name: String,
    @Json(name = "price") val price: Price,
    @Json(name = "avg_rating") val rating: Double?,
    @Json(name = "proteins") val proteins: Double,
    @Json(name = "thumbnail") val thumbnail: String,
    @Json(name = "video") val video: String? = null,
    @Json(name = "status") var status: String? = null,
    @Json(name = "is_favorite") val isFavorite: Boolean?,
    @Json(name = "description") val description: String,
    @Json(name = "matching_menu") var menuItem: MenuItem?,
    @Json(name = "matching_slot") val matchingSlot: String?,
    @Json(name = "cooking_time") var cookingTime: String = "",
    @Json(name = "instructions") var instruction: String? = null,
    @Json(name = "ingredients") var ingredients: String? = null,
    @Json(name = "carbohydrates") val carbohydrates: Double,
    @Json(name = "cuisines") val cuisines: List<CuisineLabel>,
    @Json(name = "calorific_value") val calorificValue: Double,
    @Json(name = "door_to_door_time") val doorToDoorTime: String,
    @Json(name = "portion_size") var portionSize: String? = null,
    @Json(name = "nationwide_shipping") val isNationwide: Boolean?,
    @Json(name = "prep_time_range") val prepTimeRange: PrepTimeRange,
    @Json(name = "available_at") val availableMenuItems: List<MenuItem>,
    @Json(name = "dietary_accommodations") var accommodations: String? = null,
    @Json(name = "image_gallery") val imageGallery: List<String>? = null,
    @Json(name = "cooking_methods") val cookingMethods: List<CookingMethods>,
    @Json(name = "dish_ingredients") val dishIngredients: List<DishIngredient>?
){
    fun getPriceObj(): Price {
        if(availableMenuItems?.size > 0 && availableMenuItems[0].price != null){
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

    fun getAdditionalDishes(curCookingSlotId: Long? = null): ArrayList<Dish>{
        val availableArr = arrayListOf<Dish>()
        if (curCookingSlotId != null) {
            availableArr.addAll(cook.dishes.filter { it.menuItem?.cookingSlot?.id == curCookingSlotId })
        } else {
            //todo - first case when entering screen and there is not cooking slot yet for order.
            cook.dishes.forEach { dish ->
                dish.menuItem?.let {
                    availableArr.add(dish)
                }
            }
        }
        return availableArr
    }
}

data class MediaList(
    val media: String,
    val isImage: Boolean
)


@Parcelize
@JsonClass(generateAdapter = true)
data class DishIngredient(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "quantity") val quantity: Double?,
    @Json(name = "is_adjustable") val isAdjustable: Boolean?,
    @Json(name = "unit") val unit: WoodUnit?,
    @Json(name = "ingredient") val ingredient: Ingredient?,
    @Json(name = "_remove") val _removeId: Long? = null
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Ingredient(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "units") val unit: List<WoodUnit>?
): Parcelable

@JsonClass(generateAdapter = true)
data class CookingMethods(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String
)

@Parcelize
@JsonClass(generateAdapter = true)
data class Price(
    @Json(name = "cents") val cents: Long?,
    @Json(name = "value") val value: Double?,
    @Json(name = "formatted") val formatedValue: String?
): Parcelable

@JsonClass(generateAdapter = true)
data class UpcomingSlot(
    @Json(name = "id") val id: Long,
    @Json(name = "status") val status: Int,
    @Json(name = "quantity") val quantity: Int,
    @Json(name = "starts_at") val startsAt: Date,
    @Json(name = "units_sold") val unitSold: Int
)

