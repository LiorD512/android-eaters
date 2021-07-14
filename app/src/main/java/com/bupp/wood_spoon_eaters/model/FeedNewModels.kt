package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.common.Constants
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


//@JsonClass(generateAdapter = true)
//data class ServerResponse<T> (
//    var code: Int = 0,
//    var message: String? = null,
//    var data: T? = null,
//    var errors: List<WSError>? = null,
//    val meta: Pagination? = null,
//)

data class FeedSection(
    val title: String? = null,
    val href: String? = null,
    val collections: List<FeedSectionCollectionItem>? = null
)


sealed class FeedSectionCollectionItem(
    var type: FeedModelsViewType?
): Parcelable {
    abstract val items: List<Parcelable>?
    abstract val href: String?
}


@Parcelize
@JsonClass(generateAdapter = true)
data class FeedCampaignSection(
    override val href: String?,
    override val items: List<FeedCampaignSectionItem>?,
): Parcelable, FeedSectionCollectionItem(FeedModelsViewType.COUPONS)


@Parcelize
@JsonClass(generateAdapter = true)
data class FeedCampaignSectionItem(
    val title: String?,
    val subtitle: String?,
    val thumbnail_url: String?
): Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class FeedRestaurantSection(
    override val href: String?,
    override val items: List<FeedRestaurantSectionItem>?,
    @Json(name = "cook_name") val cookName: String?,
    @Json(name = "restaurant_name") val restaurantName: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "cook_id") val cookId: String?,
    @Json(name = "cook_thumbnail_url") val cookThumbnailUrl: String?,
    @Json(name = "avg_rating") val avgRating: String?,
): Parcelable, FeedSectionCollectionItem(FeedModelsViewType.RESTAURANT)


sealed class FeedRestaurantSectionItem(
    var type: FeedRestaurantSectionItemViewType?
): Parcelable {
    abstract val data: List<Parcelable>?
}


@Parcelize
@JsonClass(generateAdapter = true)
data class FeedRestaurantItemTypeDish(
    override val data: List<FeedRestaurantItemDish>?
): Parcelable, FeedRestaurantSectionItem(FeedRestaurantSectionItemViewType.DISH)


@Parcelize
@JsonClass(generateAdapter = true)
data class FeedRestaurantItemDish(
   val id: Long?,
   val name: String?,
   val thumbnail_url: String?,
   val formatted_price: String,
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedRestaurantItemTypeSeeMore(
    override val data: List<FeedRestaurantItemDish>?
): Parcelable, FeedRestaurantSectionItem(FeedRestaurantSectionItemViewType.SEE_MORE)


@Parcelize
@JsonClass(generateAdapter = true)
data class FeedRestaurantItemSeeMore(
   val title: String?,
   val thumbnail_url: String?,
   val formatted_price: String,
): Parcelable


enum class FeedModelsViewType{
    @Json(name = "available_coupons") COUPONS,
    @Json(name = "restaurant_overview") RESTAURANT,
}

enum class FeedRestaurantSectionItemViewType{
    @Json(name = "dish") DISH,
    @Json(name = "see_more") SEE_MORE,
}
