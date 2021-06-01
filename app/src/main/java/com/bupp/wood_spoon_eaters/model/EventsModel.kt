package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.util.*


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
    @Json(name = "feed") val feed: List<Feed>,
    @Json(name = "pickup_at") val pickupAt: Date,
    @Json(name = "delivery_fee") val deliveryFee: Price
):Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Campaign(
    @Json(name = "user_interaction_id") val userInteractionId: Long?,
    @Json(name = "user_interaction_status") val status: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "show_after") val showAfter: String?,
    @Json(name = "view_types") val viewTypes: String?,
    @Json(name = "header") val header: String?,
    @Json(name = "photo_small") val photoSmall: String?,
    @Json(name = "photo_large") val photoLarge: String?,
    @Json(name = "body_text1") val bodyText1: String?,
    @Json(name = "body_text2") val bodyText2: String?,
    @Json(name = "button_text") val buttonText: String?,
    @Json(name = "button_action") val buttonAction: String?,
    @Json(name = "share_text") val shareText: String?,
    @Json(name = "banner_color") val bannerColor: String?,
    @Json(name = "share_url") val shareUrl: String?, //
    @Json(name = "terms_and_conditions") val termsAndConditions: String?
):Parcelable

//@Parcelize
//data class Campaign(
//    @Json(name = "id") val id: Long,
//    @Json(name = "name") val name: String,
//    @Json(name = "description") val description: String,
//    @Json(name = "thumbnail") val thumbnail: String,
//    @Json(name = "share_button_text") val shareBtnText: String?,
//    @Json(name = "share_text") val shareText: String?,
//    @Json(name = "invite_url") val inviteUrl: String?
//):Parcelable

@JsonClass(generateAdapter = true)
data class ActiveCampaign(
    @Json(name = "id") val id: Long,
    @Json(name = "banner_title") val title: String?,
    @Json(name = "banner_image") val image: String?,
    @Json(name = "banner_terms") val terms: String?,
    @Json(name = "banner_color") val color: String?
)