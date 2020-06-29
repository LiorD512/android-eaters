package com.bupp.wood_spoon_eaters.model

import android.net.Uri
import android.os.Parcelable
import com.bupp.wood_spoon_eaters.utils.Utils
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*



@Parcelize
data class WoodUnit(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("displayName") val displayName: String,
    @SerializedName("definition") val definition: String
): Parcelable


@Parcelize
data class Diet(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String
) : Parcelable


@Parcelize
data class Client(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("first_name") val firstName: String = "",
    @SerializedName("last_name") val lastName: String = "",
    @SerializedName("thumbnail") var thumbnail: String? = "",
    @SerializedName("temp_thumbnail") var tempThumbnail: Uri? = null,
    @SerializedName("temp_video_uri") var tempVideoUri: Uri? = null,
    @SerializedName("video") var video: String? = "",
    @SerializedName("phone_number") val phoneNumber: String? = "",
    @SerializedName("email") val email: String = "",
//        val inAppToken: String,
    @SerializedName("account_status") val accountStatus: String? = "",
    @SerializedName("status_updated_at") val statusUpdatedAt: Date? = null,
    @SerializedName("last_activity_at") val lastActivityAt: Date? = null,
    @SerializedName("profession") val profession: String? = "",
    @SerializedName("birthdate") val birthdate: String? = "",
    @SerializedName("place_of_birth") val placeOfBirth: Country? = null,
    @SerializedName("about") val about: String? = "",
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
    @SerializedName("certificates") val certificates: ArrayList<String>? = arrayListOf(),
    @SerializedName("cuisine_ids") val cuisineIds: ArrayList<Int>? = arrayListOf(),
    @SerializedName("diet_ids") var dietIds: ArrayList<Int>? = null,
    @SerializedName("country_id") var countryId: Int? = null
) : Parcelable
