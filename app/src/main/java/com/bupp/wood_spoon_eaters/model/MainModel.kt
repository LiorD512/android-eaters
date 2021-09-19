package com.bupp.wood_spoon_eaters.model

import android.net.Uri
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*


@Parcelize
@JsonClass(generateAdapter = true)
data class WoodUnit(
    @Json(name = "id") val id: Long?,
    @Json(name = "name") val name: String?,
    @Json(name = "displayName") val displayName: String?,
    @Json(name = "definition") val definition: String?
): Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class Diet(
    @Json(name = "id") val id: Long?,
    @Json(name = "name") val name: String
) : Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class Client(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "first_name") val firstName: String = "",
    @Json(name = "last_name") val lastName: String = "",
    @Json(name = "thumbnail") var thumbnail: String? = "",
    @Json(name = "temp_thumbnail") var tempThumbnail: Uri? = null,
    @Json(name = "temp_video_uri") var tempVideoUri: Uri? = null,
    @Json(name = "video") var video: String? = "",
    @Json(name = "phone_number") val phoneNumber: String? = "",
    @Json(name = "email") val email: String = "",
//        val inAppToken: String,
    @Json(name = "account_status") val accountStatus: String? = "",
    @Json(name = "status_updated_at") val statusUpdatedAt: Date? = null,
    @Json(name = "last_activity_at") val lastActivityAt: Date? = null,
    @Json(name = "profession") val profession: String? = "",
    @Json(name = "birthdate") val birthdate: String? = "",
    @Json(name = "place_of_birth") val placeOfBirth: Country? = null,
    @Json(name = "about") val about: String? = "",
    @Json(name = "weekly_orders_count") val weeklyOrdersCount: Int? = null,
    @Json(name = "total_orders_count") val totalOrdersCount: Int? = null,
    @Json(name = "total_dishes_count") val totalDishesCount: Int? = null,
    @Json(name = "ordered_dishes_count") val orderedDishesCount: Int? = null,
    @Json(name = "daily_revenue") val dailyRevenue: Int? = null,
    @Json(name = "daily_profit") val dailyProfit: Int? = null,
    @Json(name = "total_revenue") val totalRevenue: Int? = null,
    @Json(name = "total_profit") val totalProfit: Int? = null,
    @Json(name = "avg_profit_per_dish") val avgProfitPerDish: Int? = null,
    @Json(name = "pickup_address") var pickupAddress: Address? = null,
    @Json(name = "certificates") val certificates: List<String>? = arrayListOf(),
    @Json(name = "cuisine_ids") val cuisineIds: List<Int>? = arrayListOf(),
    @Json(name = "diet_ids") var dietIds: List<Int>? = null,
    @Json(name = "country_id") var countryId: Int? = null
) : Parcelable
