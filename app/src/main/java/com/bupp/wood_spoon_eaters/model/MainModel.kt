package com.bupp.wood_spoon_eaters.model

import android.net.Uri
import android.os.Parcelable
import com.bupp.wood_spoon_eaters.utils.Utils
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*



data class Suggestion(
    val id: Long,
    val eater: Eater,
    val dishName: String,
    val dishDescription: String
)



//data class Ingredient(
//        val id: Long,
//
//
//        val name: String,
//        val baseUnit: String,
//        val unitPrice: Price,
//        val calorificValue: Int
//)


@Parcelize
data class WoodUnit(
    val id: Long,
    val name: String,
    val displayName: String,
    val definition: String
): Parcelable

//data class DishCookingMethod(
//    val id: Long,
//    val dish: Dish,
//    val cookingMethod: CookingMethod
//)

data class ReportTopic(
    val id: Long,
    val name: String
)

data class ReportIssue(
    val id: Long,
    val reportTopic: ReportTopic,
    val name: String
)

data class Report(
    val id: Long,
    val order: Order,
    val reportTopic: ReportTopic,
    val reportIssue: ReportIssue,
    val body: String
)

data class CookCuisine(
    val id: Long,
    val client: Client,
    val cuisine: Cuisine
)

data class DishCuisine(
    val id: Long,
    val dish: Dish,
    val cuisine: Cuisine
)

@Parcelize
data class Diet(
    val id: Long?,
    val name: String
) : Parcelable

data class CookDiet(
    val id: Long,
    val client: Client,
    val diet: Diet
)

data class DishDiet(
    val id: Long,
    val dish: Dish,
    val diet: Diet
)

//@Parcelize
//data class Dish(
//    var id: Long? = null,
//    var client: Client? = null,
//    var name: String = "",
//    var description: String = "",
//    var thumbnail: String = "",
//    var tempThumbnail: Uri? = null,
//    var dishType: Int = -1,
//    var minPrepTime: Int = -1,
//    var maxPrepTime: Int = -1,
//    var price: Price? = null,
//    var freeDelivery: Boolean = true,
//    var status: String = "",
//    var statusUpdatedAt: Date = Date(),
//    @SerializedName("cooking_time") var cookingTime: String = "",
//    @SerializedName("cuisine_ids") var cuisineIds: ArrayList<Long> = arrayListOf(),
//    @SerializedName("diet_ids") var dietaryIds: ArrayList<Long> = arrayListOf(),
//    @SerializedName("dish_ingredients") var dishIngredients: ArrayList<DishIngredient>? = arrayListOf(),
//    @SerializedName("cooking_method_ids") var cookingMethodIds: ArrayList<Long> = arrayListOf(),
//    @SerializedName("prep_time_range_id") var prepTimeRangeId: Long = -1,
////        var ancestorId: Long,
//    var calorificValue: Int = -1,
//    var unitSold: Int = -1,
//    var ordersCount: Int = -1,
//    var offersCount: Int = -1,
//    var totalRevenue: Int = -1,
//    var totalProfit: Int = -1,
//    var avgRating: Double = -1.0,
//    var ingredients: ArrayList<Ingredient> = arrayListOf()
//) : Parcelable

data class PromoCode(
    val id: Long,
    val expiresAt: Date,
    val code: String,
    val value: Int,
    val status: Int
)

data class CancellationReason(
    val id: Long,
    val name: String
)

@Parcelize
data class Client(
    val id: Long? = null,
    @SerializedName("first_name") val firstName: String = "",
    @SerializedName("last_name") val lastName: String = "",
    var thumbnail: String? = "",
    var tempThumbnail: Uri? = null,
    var tempVideoUri: Uri? = null,
    var video: String? = "",
    @SerializedName("phone_number") val phoneNumber: String? = "",
    val email: String = "",
//        val inAppToken: String,
    @SerializedName("account_status") val accountStatus: String? = "",
    @SerializedName("status_updated_at") val statusUpdatedAt: Date? = null,
    @SerializedName("last_activity_at") val lastActivityAt: Date? = null,
    val profession: String? = "",
    val birthdate: String? = "",
    @SerializedName("place_of_birth") val placeOfBirth: Country? = null,
    val about: String? = "",
    @SerializedName("weekly_orders_count") val weeklyOrdersCount: Int? = null,
    @SerializedName("total_orders_count") val totalOrdersCount: Int? = null,
    @SerializedName("total_dishes_count") val totalDishesCount: Int? = null,
    @SerializedName("ordered_dishes_count") val orderedDishesCount: Int? = null,
    @SerializedName("daily_revenue") val dailyRevenue: Int? = null,
    @SerializedName("daily_profit") val dailyProfit: Int? = null,
    @SerializedName("total_revenue") val totalRevenue: Int? = null,
    @SerializedName("total_profit") val totalProfit: Int? = null,
    @SerializedName("avg_profit_per_dish") val avgProfitPerDish: Int? = null,
    @SerializedName("pickup_address") var pickupAddress: Address? = null,
    val certificates: ArrayList<String>? = arrayListOf(),
    @SerializedName("cuisine_ids") val cuisineIds: ArrayList<Int>? = arrayListOf(),
    @SerializedName("diet_ids") var dietIds: ArrayList<Int>? = null,
    @SerializedName("country_id") var countryId: Int? = null
) : Parcelable

data class Certificate(
    val id: Long,
    val name: String,
    val client: Client
)

data class Order(
    val id: Long,
    val created_at: Date,
    val updated_at: Date,
    val orderItems: ArrayList<OrderItem>,
    val eater: Eater,
    val orderNumber: String,
    val client: Client,
    val status: Int,
    val statusUpdatedAt: Date,
    val cancellationReason: ArrayList<CancellationReason>,
    val deliverAt: Date,
    val deliverAddress: Address,
    val courier: Courier,
    val deliveryStatus: Int,
    val deliveryStatusUpdatedAt: Date,
    val itemsCount: Int,
    val subtotal: Int,
    val tax: Int,
    val serviceFee: Int,
    val deliveryFee: Int,
    val tip: Int,
    val PromoCode: PromoCode,
    val discount: Int,
    val total: Int,
    val transactionId: String,
    val notes: String
)

data class Courier(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val thumbnail: String,
    val phoneNumber: String,
    val email: String,
    val inAppToken: String,
    val accountStatus: Int,
    val statusUpdatedAt: Date,
    val lastActivityAt: Date,
    val primaryAddress: Address,
    val deliveryMethod: DeliveryMethod,
    val totalOrdersCount: Int
)

data class DeliveryMethod(
    val id: Long,
    val name: String
)



data class Metric(
    val id: Long,
    val name: String,
    val description: String,
    val weight: Double
)

data class DishMetric(
    val id: Long,
    val metric: Metric,
    val dish: Dish,
    val avgRating: Double
)



//data class CookingSlot(
//    val id: Long,
//    val client: Client,
//    val startsAt: Date,
//    val endsAt: Date,
//    val status: Int,
//    val menusCount: Int,
//    val totalProfit: Price,
//    val menuItems: ArrayList<MenuItem>,
//    val orders: ArrayList<Order>
//)

data class Favorite(
    val id: Long,
    val eater: Eater,
    val dish: Dish
)

data class Review(
    val id: Long,
    val eater: Eater,
//        val reviewableId: Client,
//        val reviewableType: Client,
    val body: String
)

@Parcelize
data class Cuisine(
    val id: Long?,
    val name: String,
    val body: String?,
    val icon: String,
    val dishesCount: Int?
) : Parcelable

//@Parcelize
//data class Ingredient(
//    override var id: Long,
//    override var name: String,
//    var baseUnit: String,
//    var unitPrice: Price,
//    var calorificValue: Int,
//    var adjustable: Boolean?,
//    var units: ArrayList<WoodUnit>
//) : SelectableString, Parcelable
//
//@Parcelize
//data class CookingMethod(
//    override val id: Long,
//    override val name: String
//) : SelectableString, Parcelable




///TODO::::DELTEE
@Parcelize
data class DishIngredient2(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("quantity") val quantity: Int?=null,
    @SerializedName("is_adjustable") val isAdjustable: Boolean?= null,
    @SerializedName("unit") val unit: WoodUnit?=null,
    @SerializedName("ingredient") val ingredient: Ingredient?,
    @SerializedName("_remove") val _removeId: Long? = null
): Parcelable

///TODO::::DELTEE


data class Dish2(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("cook") val cook: Cook2? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("price") val price: Price? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("is_favorite") val isFavorite: Boolean? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("free_delivery") val freeDelivery: Boolean? = null,
    @SerializedName("upcoming_slot") val upcomingSlot: UpcomingSlot? = null,
    @SerializedName("calorific_value") val calorificValue: Double? = null,
    @SerializedName("proteins") val proteins: Double? = null,
    @SerializedName("prep_time_range") val prepTimeRange: PrepTimeRange? = null,
    @SerializedName("dish_ingredients") val dishIngredients: ArrayList<DishIngredient>? = null,
    @SerializedName("cooking_methods") val cookingMethods: ArrayList<CookingMethods>? = null,
    @SerializedName("carbohydrates") val carbohydrates: Double? = null,
    @SerializedName("avg_rating") val rating: Double? = null,
    @SerializedName("removed_ingredients") val removedIngredients: ArrayList<DishIngredient2>? = null
)
///TODO::::DELTEE


data class OrderItem2(
    val id: Long,
    val order: Order? = null,
    val dish: Dish2,
    val quantity: Int,
    val price: Price? = null,
    val notes: String? = null
)
///TODO::::DELTEE

data class Order2(
    var id: Long? = null,
    var created_at: Date? = null,
    var updated_at: Date? = null,
    var orderItems: ArrayList<OrderItem2>? = null,
    var eater: Eater? = null,
    var orderNumber: String? = null,
    var client: Client? = null,
    var status: Int? = null,
    var statusUpdatedAt: Date? = null,
    var cancellationReason: ArrayList<CancellationReason>? = null,
    var deliverAt: Date? = null,
    var deliverAddress: Address? = null,
    var courier: Courier? = null,
    var deliveryStatus: Int? = null,
    var deliveryStatusUpdatedAt: Date? = null,
    var itemsCount: Int? = null,
    var subtotal: Int? = null,
    var tax: Int? = null,
    var serviceFee: Int? = null,
    var deliveryFee: Int? = null,
    var tip: Int? = null,
    var PromoCode: PromoCode? = null,
    var discount: Int? = null,
    var total: Int? = null,
    var transactionId: String? = null,
    var notes: String? = null
)
///TODO::::DELTEE
data class Cook2(
    @SerializedName("id") val id: Long?=null,
    @SerializedName("first_name") val firstName: String?=null,
    @SerializedName("last_name") val lastName: String?=null,
    @SerializedName("thumbnail") val thumbnail: String?=null,
    @SerializedName("video") val video: String?=null,
    @SerializedName("profession") val profession: String?=null,
    @SerializedName("about") val about: String?=null,
    @SerializedName("birthdate") val birthdate: Date?=null,
    @SerializedName("place_of_birth") val country: Country?=null,
    @SerializedName("certificates") val certificates: ArrayList<String>?=null,
    @SerializedName("cuisines") val cuisines: ArrayList<CuisineLabel>?=null,
    @SerializedName("diets") val diets: ArrayList<DietaryIcon>?=null,
    @SerializedName("avg_rating") val rating: Double?=null,
    @SerializedName("dishes") val dishes: ArrayList<Dish>?=null
){
    fun getFullName(): String{
        return "$firstName $lastName"
    }

    fun getAge(): Int {
        //todo: get birthday from server and set age
        return Utils.getDiffYears(Date())
    }
}