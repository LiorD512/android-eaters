package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

data class Dish(
    @SerializedName("id") val id: Long,
    @SerializedName("cook") val cook: Cook,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Price,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("is_favorite") val isFavorite: Boolean,
    @SerializedName("free_delivery") val freeDelivery: Boolean,
    @SerializedName("upcoming_slot") val upcomingSlot: UpcomingSlot
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