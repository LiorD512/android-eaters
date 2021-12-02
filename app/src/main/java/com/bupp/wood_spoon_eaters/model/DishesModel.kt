package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import android.util.Log
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class CookingSlot(
    @Json(name = "id") val id: Long,
    val name: String = "Name",
    @Json(name = "ends_at") val endsAt: Date,
    @Json(name = "starts_at") val startsAt: Date,
    @Json(name = "order_from") val orderFrom: Date,
    @Json(name = "can_order") val canOrder: Boolean?,
    @Json(name = "last_call_at") val lastCallAt: Date?,
    @Json(name = "delivery_fee") val deliveryFee: Price?,
    @Json(name = "free_delivery") val freeDelivery: Boolean,
    @Json(name = "nationwide_shipping") val isNationwide: Boolean?,
    @Json(name = "sections") val sections: List<CookingSlotSection> = listOf(),
    val unAvailableDishes: MutableList<MenuItem> = mutableListOf(),
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedDishCookingSlot(
    @Json(name = "id") val id: Long,
    @Json(name = "starts_at") val startsAt: Date,
    @Json(name = "can_order") val canOrder: Boolean?,
) : Parcelable {
    fun getAvailabilityString(): String {
        //this method is used for analytics
        if (Date().after(startsAt)) {
            // if now is after starts_at - availably now
            return "ASAP"
        } else if (Date().before(startsAt) && DateUtils.isToday(startsAt)) {
            // if now is before starts_at and in the same day
            return "Today"
        } else if (DateUtils.isTomorrow(startsAt)) {
            // if cooking slot is tomorrow -
            return "Tomorrow"
        } else if (DateUtils.isSameWeek(startsAt)) {
            // if the same week - after tomorrow and before week end
            return "Same week"
        } else {
            // if after a week from now -
            return "Next week and beyond"
        }
    }
}


@Parcelize
@JsonClass(generateAdapter = true)
data class CookingSlotSection(
    @Json(name = "title") val title: String?,
    @Json(name = "subtitle") val subtitle: String?,
    @Json(name = "menu_items") val menuItems: List<MenuItem> = listOf(),
) : Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class MenuItem(
    @Json(name = "id") val id: Long,
    @Json(name = "price") val price: Price? = null,
    @Json(name = "quantity") val quantity: Int = 0,
    @Json(name = "units_sold") val unitsSold: Int = 0,
    @Json(name = "order_at") val orderAt: Date? = null,
    @Json(name = "dish_id") val dishId: Long?,
    @Json(name = "tags") val tags: List<String>?,
    @Json(name = "cooking_slot") val cookingSlot: CookingSlot?,
    var dish: Dish? = null,
    /** when menu item is available at other times - the AvailabilityDate is not null and indicates the closest availability date **/
    var cookingSlotId: Long? = null,
    var availableLater: AvailabilityDate? = null,

    var sectionTitle: String? = null,
    var sectionOrder: Int? = null,
    var dishOrderInSection: Int? = null,
) : Parcelable {

    fun getQuantityCount(): Int {
        return quantity - unitsSold
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class AvailabilityDate(
    @Json(name = "starts_at") val startsAt: Date,
    @Json(name = "ends_at") val endsAt: Date
) : Parcelable {
    fun getStartEndAtTag(): String {
        return DateUtils.parseDateToStartAndEnd(startsAt, endsAt)
    }
}

@JsonClass(generateAdapter = true)
data class FullDish(
    @Json(name = "id") val id: Long,
//    @Json(name = "cook") val cook: Cook,
    @Json(name = "cook") val restaurant: Restaurant,
    @Json(name = "name") val name: String,
    @Json(name = "price") val price: Price,
    @Json(name = "thumbnail") val thumbnail: WSImage,
    @Json(name = "video") val video: String? = null,
    @Json(name = "diets") val dietaries: List<DietaryIcon>?,
    @Json(name = "description") val description: String,
    @Json(name = "instructions") var instruction: String? = null,
    @Json(name = "ingredients") var ingredients: String? = null,
    @Json(name = "portion_size") var portionSize: String? = null,
    @Json(name = "dietary_accommodations") var accommodations: String? = null,
    @Json(name = "image_gallery") val imageGallery: List<String>? = null,
    @Json(name = "available_times") val availableTimes: List<AvailabilityDate>,
    //old model
    @Json(name = "avg_rating") val rating: Double?,
    @Json(name = "proteins") val proteins: Double?,
    @Json(name = "status") var status: String? = null,
    @Json(name = "is_favorite") val isFavorite: Boolean?,
    @Json(name = "matching_menu") var menuItem: MenuItem?,
    @Json(name = "matching_slot") val matchingSlot: String?,
    @Json(name = "cooking_time") var cookingTime: String = "",
    @Json(name = "carbohydrates") val carbohydrates: Double?,
    @Json(name = "cuisines") val cuisines: List<CuisineLabel>?,
    @Json(name = "calorific_value") val calorificValue: Double?,
    @Json(name = "door_to_door_time") val doorToDoorTime: String?,
    @Json(name = "nationwide_shipping") val isNationwide: Boolean?,
    @Json(name = "prep_time_range") val prepTimeRange: PrepTimeRange?,
    @Json(name = "available_at") val availableMenuItems: List<MenuItem> = listOf(),
    @Json(name = "cooking_methods") val cookingMethods: List<CookingMethods>?,
    @Json(name = "dish_ingredients") val dishIngredients: List<DishIngredient>?
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
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Ingredient(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "units") val unit: List<WoodUnit>?
) : Parcelable

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
) : Parcelable

@JsonClass(generateAdapter = true)
data class UpcomingSlot(
    @Json(name = "id") val id: Long,
    @Json(name = "status") val status: Int,
    @Json(name = "quantity") val quantity: Int,
    @Json(name = "starts_at") val startsAt: Date,
    @Json(name = "units_sold") val unitSold: Int
)

