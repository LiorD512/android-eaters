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
    @SerializedName("user_interaction_id") val userInteractionId: Long,
    @SerializedName("user_interaction_status") val status: String,
    @SerializedName("name") val name: String,
    @SerializedName("show_after") val showAfter: String,
    @SerializedName("view_types") val viewTypes: String,
    @SerializedName("header") val header: String?,
    @SerializedName("photo_small") val photoSmall: String?,
    @SerializedName("photo_large") val photoLarge: String?,
    @SerializedName("body_text1") val bodyText1: String?,
    @SerializedName("body_text2") val bodyText2: String?,
    @SerializedName("button_text") val buttonText: String?,
    @SerializedName("button_action") val buttonAction: String?,
    @SerializedName("share_text") val shareText: String?,
    @SerializedName("banner_color") val bannerColor: String?,
    @SerializedName("terms_and_conditions") val termsAndConditions: String?
):Parcelable

//@Parcelize
//data class Campaign(
//    @SerializedName("id") val id: Long,
//    @SerializedName("name") val name: String,
//    @SerializedName("description") val description: String,
//    @SerializedName("thumbnail") val thumbnail: String,
//    @SerializedName("share_button_text") val shareBtnText: String?,
//    @SerializedName("share_text") val shareText: String?,
//    @SerializedName("invite_url") val inviteUrl: String?
//):Parcelable

data class ActiveCampaign(
    @SerializedName("id") val id: Long,
    @SerializedName("banner_title") val title: String?,
    @SerializedName("banner_image") val image: String?,
    @SerializedName("banner_terms") val terms: String?,
    @SerializedName("banner_color") val color: String?
)