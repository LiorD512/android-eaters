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

@Parcelize
data class Campaign(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("share_button_text") val shareBtnText: String,
    @SerializedName("share_text") val shareText: String,
    @SerializedName("invite_url") val inviteUrl: String
):Parcelable

data class ActiveCampaign(
    @SerializedName("id") val id: Long,
    @SerializedName("banner_title") val title: String?,
    @SerializedName("banner_image") val image: String?,
    @SerializedName("banner_terms") val terms: String?
)