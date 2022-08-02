package com.bupp.wood_spoon_chef.data.remote.model

import android.net.Uri
import android.os.Parcelable
import com.bupp.wood_spoon_chef.di.abs.SerializeNulls
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class Eater(
    @Json(name = "id") val id: Long?,
    @Json(name = "first_name") val firstName: String?,
    @Json(name = "last_name") val lastName: String?,
    @Json(name = "thumbnail") val thumbnail: String?
) : Parcelable {
    fun getFullName(): String {
        return "$firstName $lastName"
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class Suggestion(
    @Json(name = "id") val id: Long,
    @Json(name = "eater") val eater: Eater,
    @Json(name = "dish_name") val dishName: String,
    @Json(name = "dish_description") val dishDescription: String
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Price(
    @Json(name = "formatted") var formattedValue: String = "",
    @Json(name = "values") var value: Double? = null,
    @Json(name = "cents") var cents: Long? = null
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class WoodUnit(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String?,
    @Json(name = "display_name") val displayName: String?,
    @Json(name = "definition") val definition: String?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class DishCookingMethod(
    @Json(name = "id") val id: Long,
    @Json(name = "dish") val dish: Dish,
    @Json(name = "cooking_method") val cookingMethod: CookingMethod
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ReportTopic(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ReportIssue(
    @Json(name = "id") val id: Long,
    @Json(name = "report_topic") val reportTopic: ReportTopic,
    @Json(name = "name") val name: String
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Report(
    @Json(name = "id") val id: Long,
    @Json(name = "order") val order: Order,
    @Json(name = "report_topic") val reportTopic: ReportTopic,
    @Json(name = "report_issue") val reportIssue: ReportIssue,
    @Json(name = "body") val body: String
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class CookCuisine(
    @Json(name = "id") val id: Long,
    @Json(name = "cook") val cook: Cook,
    @Json(name = "cuisine") val cuisine: Cuisine
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class DishCuisine(
    @Json(name = "id") val id: Long,
    @Json(name = "dish") val dish: Dish,
    @Json(name = "cuisine") val cuisine: Cuisine
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Diet(
    @Json(name = "id") val id: Long?,
    @Json(name = "name") val name: String
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class CookDiet(
    @Json(name = "id") val id: Long,
    @Json(name = "cook") val cook: Cook,
    @Json(name = "diet") val diet: Diet
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class DishDiet(
    @Json(name = "id") val id: Long,
    @Json(name = "dish") val dish: Dish,
    @Json(name = "diet") val diet: Diet
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class PromoCode(
    @Json(name = "id") val id: Long,
    @Json(name = "expires_at") val expiresAt: Date,
    @Json(name = "code") val code: String,
    @Json(name = "values") val value: Int,
    @Json(name = "status") val status: Int
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class CancellationReason(
    @Json(name = "id") val id: Long?,
    @Json(name = "name") val name: String?
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class WSImage(
    @Json(name = "url") val url: String?,
):Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Cook(
    val id: Long? = null,
    @Json(name = "first_name") val firstName: String? = null,
    @Json(name = "last_name") val lastName: String? = "",
    @Json(name = "restaurant_name") val restaurantName : String? = "",
    @Json(name = "thumbnail") var thumbnail: WSImage? = null,
    @Json(name = "cover") var cover: WSImage? = null,
    @Json(name = "phone_number") val phoneNumber: String? = "",
    @Json(name = "account_status") val accountStatus: String? = "",
    @Json(name = "invite_url") val inviteUrl: String? = "",
    @Json(name = "email") val email: String? = "",
    @Json(name = "pickup_address") var pickupAddress: Address? = null,
    @Json(name = "about") val about: String? = "",
    @Json(name = "country_id") var countryId: Long? = null,
    @Json(name = "place_of_birth") val placeOfBirth: Country? = null,
    @Json(name = "video") var video: String? = "",
    @Json(name = "reviews_count") var reviewCount: Int = 0,
    @Json(name = "avg_rating") var rating: Double? = 0.0,
    @Json(name = "join_date") var joinDate: Date?,
    @Json(name = "orders_count") var ordersCount: Long = 0,
) : Parcelable {

    fun getFullName(): String {
        return "$firstName $lastName"
    }

    fun getRating(): String {
        if (rating == null) {
            return "- -"
        }
        return rating.toString()
    }


//    fun getAge(): Int {
//        return DateUtils.getDiffYears(birthdate)
//    }

}

@Parcelize
@JsonClass(generateAdapter = true)
data class CookRequest(
    @Json(name = "first_name") var firstName: String? = null,
    @Json(name = "last_name") var lastName: String? = null,
    @Json(name = "email") var email: String? = null,
    @Json(name = "thumbnail") var thumbnail: String? = null,
    @Json(name = "restaurant_name") var restaurantName: String? = null,
    @Json(name = "cover") var restaurantCover: String? = null,
    @Json(name = "profession") var profession: String? = null,
    @Json(name = "about") var about: String? = null,
    @Json(name = "birthdate") var birthdate: String? = null,
    @Json(name = "ssn") var ssn: String? = null,
    @Json(name = "place_of_birth_id") var countryId: Long? = null,
    @Json(name = "certificates") var certificates: List<String>? = null,
    @Json(name = "diet_ids") var dietIds: List<Long>? = null,
    @Json(name = "cuisine_ids") var cuisineIds: List<Long>? = null,
    @Json(name = "video") @SerializeNulls var video: String? = null,
    @Json(name = "pickup_address") var pickupAddress: AddressRequest? = null,
    var tempThumbnail: Uri? = null,
    var tempCover: Uri? = null,
    var tempVideoUri: Uri? = null,
    var tempStreet2: String? = null,
//    var tempCountryFlag: SelectableString? = null,
//    var tempCuisines:  List<SelectableIcon>? = null,
//    var tempDietary:  List<SelectableIcon>? = null
//)
) : Parcelable


data class MediaRequests(var tempThumbnail: Uri? = null, var tempCover: Uri? = null, var tempVideoUri: Uri? = null)

//data class Certificate(
//        val id: Long,
//        val name: String,
//        val cook: Cook
//)

@Parcelize
@JsonClass(generateAdapter = true)
data class Order(
    @Json(name = "id") val id: Long,
    @Json(name = "order_number") val orderNumber: String?,
    @Json(name = "deliver_at") val deliverAt: Date?,
    @Json(name = "pickup_at") val pickupAt: Date?,
    @Json(name = "cook_earnings") val cookEarnings: Price?,
    @Json(name = "status") val status: String?,
    @Json(name = "disposable_tableware") val disposableTableware: Boolean = false,
    @Json(name = "free_delivery") val freeDelivery: Boolean = false,
    @Json(name = "preparation_status") var preparationStatus: String?,
    @Json(name = "estimated_delivery_time") val estimatedDeliveryTime: Date?,
    @Json(name = "eta_text") val estimatedDeliveryTimeText: Date?,
    @Json(name = "delivery_status") val deliveryStatus: String?,
    @Json(name = "total") val total: Price?,
    @Json(name = "service_fee") val serviceFee: Price?,
    @Json(name = "eater") val eater: Eater,
    @Json(name = "order_items") val orderItems: List<OrderItem>?,
    @Json(name = "cooking_slot") val cookingSlot: CookingSlot
) : Parcelable

//data class Courier(
//        val id: Long,
//        val firstName: String,
//        val lastName: String,
//        val thumbnail: String,
//        val phoneNumber: String,
//        val email: String,
//        val inAppToken: String,
//        val accountStatus: Int,
//        val statusUpdatedAt: Date,
//        val lastActivityAt: Date,
//        val primaryAddress: Address,
//        val deliveryMethod: DeliveryMethod,
//        val totalOrdersCount: Int
//)

@Parcelize
@JsonClass(generateAdapter = true)
data class DeliveryMethod(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class OrderItem(
    @Json(name = "id") val id: Long,
    @Json(name = "dish") val dish: OrderItemDish,
    @Json(name = "removed_ingredients") val removedIngredient: List<Ingredient>,
    @Json(name = "quantity") val quantity: Int,
    @Json(name = "price") val price: Price,
    @Json(name = "cook_earnings") val cookEarnings: Price?,
    @Json(name = "notes") val notes: String?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class OrderItemDish(
    @Json(name = "id") var id: Long? = null,
    @Json(name = "name") var name: String = "",
    @Json(name = "thumbnail") var thumbnail: String = ""
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Metric(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "weight") val weight: Double
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class DishMetric(
    @Json(name = "id") val id: Long,
    @Json(name = "metric") val metric: Metric,
    @Json(name = "dish") val dish: Dish,
    @Json(name = "avgRating") val avgRating: Double
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class MenuItem(
    @Json(name = "id") val id: Long,
    @Json(name = "cooking_slot") val cookingSlot: CookingSlot?,
    @Json(name = "dish") val dish: Dish,
    @Json(name = "quantity") var quantity: Int,
    @Json(name = "units_sold") val unitsSold: Int,
    @Json(name = "_destroy") var _destroy: Boolean?
) : Parcelable {
    fun parseToMenuItemRequest(): MenuItemRequest {
        return MenuItemRequest(id, dish?.id, quantity, _destroy)
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class MenuItemRequest(
    @Json(name = "id") var id: Long? = null,
    @Json(name = "dish_id") var dishId: Long? = null,
    @Json(name = "quantity") var quantity: Int? = null,
    @Json(name = "_destroy") var _destroy: Boolean? = false
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class CookingSlotRequest(
    @Json(name = "event_id") var eventId: Long? = null,
    @Json(name = "starts_at") var startsAt: Long? = null,
    @Json(name = "ends_at") var endsAt: Long? = null,
    @Json(name = "last_call_at") var lastCallAt: Long? = null,
    @Json(name = "free_delivery") var freeDelivery: Boolean = false,
    @Json(name = "nationwide_shipping") var worldwide: Boolean = false,
//    @Json(name = "recurring_slot") var recurringSlot: Boolean = false,
    @Json(name = "menu_items") var menuItems: List<MenuItemRequest> = mutableListOf(),
    @Json(name = "rrule") val recurringRule: String? = null,
    @Json(name = "submit") var submit: Boolean? = null,
    @Json(name = "detach") var detach: Boolean? = null
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class CookingSlotSlim(
    @Json(name = "id") val id: Long,
    @Json(name = "status") val status: String,
    @Json(name = "starts_at") val startsAt: Date,
    @Json(name = "ends_at") val endsAt: Date,
    @Json(name = "is_available") val isAvailable: Boolean,
    @Json(name = "name") val name: String,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class CookingSlot(
    val id: Long?,
    @Json(name = "event") val event: Event?,
    @Json(name = "starts_at") val startsAt: Date,
    @Json(name = "ends_at") val endsAt: Date,
    @Json(name = "last_call_at") val lastCallAt: Date?,
    @Json(name = "is_available") val isAvailable: Boolean?,
    @Json(name = "free_delivery") val freeDelivery: Boolean,
    @Json(name = "nationwide_shipping") val isNationwide: Boolean,
    @Json(name = "total_earnings") val totalEarnings: Price?,
    @Json(name = "potential_earnings") val potentialEarnings: Price?,
    @Json(name = "status") val status: String?,
    @Json(name = "total_profit") val totalProfit: Price?,
    @Json(name = "menu_items") val menuItems: List<MenuItem> = mutableListOf(),
    @Json(name = "rrule") val recurringRule: String? = null,
    val orders: List<Order>?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Favorite(
    @Json(name = "id") val id: Long,
    @Json(name = "eater") val eater: Eater,
    @Json(name = "dish") val dish: Dish
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Cuisine(
    @Json(name = "id") val id: Long?,
    @Json(name = "name") val name: String,
    @Json(name = "body") val body: String?,
    @Json(name = "icon") val icon: String,
    @Json(name = "dishesCount") val dishesCount: Int?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Ingredient(
    @Json(name = "id") override var id: Long,
    @Json(name = "name") override var name: String,
    @Json(name = "base_unit") var baseUnit: String?,
    @Json(name = "unit_price") var unitPrice: Price?,
    @Json(name = "calorific_value") var calorificValue: Int?,
    @Json(name = "units") var units: List<WoodUnit>
) : SelectableString, Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class CookingMethod(
    @Json(name = "id") override val id: Long,
    @Json(name = "name") override val name: String
) : SelectableString, Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Otl(
    @Json(name = "url") val url: String
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Earnings(
    @Json(name = "weekly_orders_count") val weeklyOrdersCount: Int?,
    @Json(name = "total_orders_count") val totalOrdersCount: Int?,
    @Json(name = "rating_percentile") val ratingPercentile: Int?,
    @Json(name = "daily_earnings") val dailyEarnings: Price?,
    @Json(name = "weekly_earnings") val weeklyEarnings: Price?,
    @Json(name = "total_earnings") val totalEarnings: Price?,
    @Json(name = "avg_earnings_per_dish") val avgEarningsPerDish: Price?,
    @Json(name = "yearly_earnings") val yearlyEarnings: List<EarningAggregator>?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EarningAggregator(
    @Json(name = "year") val year: Int,
    @Json(name = "month") val month: Int,
    @Json(name = "total_earnings") val totalEarnings: Price
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Event(
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "thumbnail") val thumbnail: String,
    @Json(name = "location") val location: Address,
    @Json(name = "starts_at") val startsAt: Date,
    @Json(name = "ends_at") val endsAt: Date,
    @Json(name = "invitation") val invitation: Invitation? = null,
    @Json(name = "pickup_at") val pickupAt: Date
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Invitation(
    val id: Long,
    val status: String
) : Parcelable