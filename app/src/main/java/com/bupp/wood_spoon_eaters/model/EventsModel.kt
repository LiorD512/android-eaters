package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
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
    @Json(name = "user_interaction_status") val status: UserInteractionStatus?,
    @Json(name = "name") val name: String?,
    @Json(name = "show_after") val showAfter: CampaignShowAfter?,
    @Json(name = "view_types") val viewTypes: List<CampaignViewType>?,
    @Json(name = "header") val header: String?,
    @Json(name = "photo_small") val photoSmall: String?,
    @Json(name = "photo_large") val photoLarge: String?,
    @Json(name = "body_text1") val bodyText1: String?,
    @Json(name = "button_text") val buttonText: String?,
    @Json(name = "button_action") val buttonAction: CampaignButtonAction?,
    @Json(name = "share_text") val shareText: String?,
    @Json(name = "banner_color") val bannerColor: String?,
    @Json(name = "share_url") val shareUrl: String?,
    @Json(name = "terms_and_conditions") val termsAndConditions: String?
):Parcelable

//@Parcelize
//@JsonClass(generateAdapter = true)
//data class CampaignData(val campaigns: List<Campaign>):Parcelable

enum class UserInteractionStatus{
    @Json(name = "idle") IDLE,
    @Json(name = "seen") SEEN,
    @Json(name = "engaged ") ENGAGED,
}

enum class CampaignShowAfter{
    @Json(name = "homepage_visit") VISIT_HOME_PAGE,
    @Json(name = "feed_visit") VISIT_FEED,
    @Json(name = "add_to_cart_action ") ACTION_ADD_TO_CART,
    @Json(name = "purchase_action ") ACTION_PURCHASE,
    @Json(name = "rate_your_order_action ") ACTION_RATE_ORDER,
    @Json(name = "clear_your_cart_action  ") ACTION_CLEAR_CART,
}

enum class CampaignViewType{
    @Json(name = "banner") BANNER,
    @Json(name = "popup") POPUP,
    @Json(name = "feed") FEED,
    @Json(name = "profile") PROFILE,
}

enum class CampaignButtonAction{
    @Json(name = "share") SHARE,
    @Json(name = "acknowledge") ACKNOWLEDGE,
    @Json(name = "jump_to_link ") JUMP_TO_LINK,
}

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