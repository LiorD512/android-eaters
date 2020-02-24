package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Event(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("location") val location: Address,
    @SerializedName("starts_at") val startsAt: Date,
    @SerializedName("ends_at") val endsAt: Date,
    @SerializedName("feed") val feed: ArrayList<Feed>,
    @SerializedName("pickup_at") val pickupAt: Date,
    @SerializedName("delivery_fee") val deliveryFee: Price
):Parcelable