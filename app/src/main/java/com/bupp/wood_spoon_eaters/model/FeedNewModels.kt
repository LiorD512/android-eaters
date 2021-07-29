package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
data class FeedResult(
    val sections: List<FeedSection>
)

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedSection(
    var id: Long? = null,
    val title: String? = null,
    var href: String? = null,
    var collections: MutableList<FeedSectionCollectionItem>? = null
): Parcelable


sealed class FeedSectionCollectionItem(
    @Json(name = "type") var type: FeedModelsViewType?
) : Parcelable {
    abstract val items: List<Parcelable>?
    abstract val href: String?
}


@Parcelize
@JsonClass(generateAdapter = true)
data class FeedCampaignSection(
    override val href: String?,
    override val items: List<Campaign>?,
): Parcelable, FeedSectionCollectionItem(FeedModelsViewType.COUPONS)

//@Parcelize
//@JsonClass(generateAdapter = true)
//data class FeedCampaignSectionItem(
//    val title: String?,
//    val subtitle: String?,
//    val thumbnail_url: String?
//): Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class FeedRestaurantSection(
    override var href: String?,
    override val items: List<FeedRestaurantSectionItem>?,
    @Json(name = "chef_name") val chefName: String?,
    @Json(name = "restaurant_name") val restaurantName: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "chef_id") val chefId: String?,
    @Json(name = "chef_thumbnail_url") val chefThumbnailUrl: String?,
    @Json(name = "avg_rating") val avgRating: String?,
) : Parcelable, FeedSectionCollectionItem(FeedModelsViewType.RESTAURANT) {
    fun getCook(): Cook =
        Cook(
            id = chefId?.toLong() ?: 0,
            firstName = chefName ?: "",
            lastName = "",
            thumbnail = chefThumbnailUrl ?: "",
            video = null,
            profession = null,
            about = null,
            birthdate = null,
            pickupAddress = null,
            country = null,
            certificates = listOf(),
            cuisines = mutableListOf(),
            diets = mutableListOf(),
            rating = avgRating?.toDouble(),
            reviewCount = 0,
            dishes = mutableListOf()
        )
}


sealed class FeedRestaurantSectionItem(
    @Json(name = "type") val type: FeedRestaurantSectionItemViewType? = null
) : Parcelable {
    abstract val data: Parcelable?
}


@Parcelize
@JsonClass(generateAdapter = true)
data class FeedRestaurantItemTypeDish(
    override val data: FeedRestaurantItemDish?
) : Parcelable, FeedRestaurantSectionItem(FeedRestaurantSectionItemViewType.DISH)


@Parcelize
@JsonClass(generateAdapter = true)
data class FeedRestaurantItemDish(
    val id: Long?,
    val name: String?,
    val thumbnail_url: String?,
    val formatted_price: String?,
    val tags: List<Tag>?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedRestaurantItemTypeSeeMore(
    override val data: FeedRestaurantItemSeeMore?
) : Parcelable, FeedRestaurantSectionItem(FeedRestaurantSectionItemViewType.SEE_MORE)


@Parcelize
@JsonClass(generateAdapter = true)
data class FeedRestaurantItemSeeMore(
    val title: String?,
    val thumbnail_url: String?,
    val formatted_price: String?,
) : Parcelable


enum class FeedModelsViewType {
    @Json(name = "available_coupons")
    COUPONS,
    @Json(name = "restaurant_overview")
    RESTAURANT,
}

enum class FeedRestaurantSectionItemViewType{
    @Json(name = "dish") DISH,
    @Json(name = "see_more") SEE_MORE,
}

sealed class FeedAdapterItem(
    var type: FeedAdapterViewType?
) : Parcelable

enum class FeedAdapterViewType {
    TITLE,
    COUPONS,
    RESTAURANT,
    SKELETON,
    HREF
}

@Parcelize
data class FeedAdapterSkeleton(
    val id: Long? = null
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.SKELETON)

@Parcelize
data class FeedAdapterHref(
    val href: String? = null
): Parcelable, FeedAdapterItem(FeedAdapterViewType.HREF)

@Parcelize
data class FeedAdapterTitle(
    val title: String
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.TITLE)

@Parcelize
data class FeedAdapterCoupons(
    val couponSection: FeedCampaignSection
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.COUPONS)

@Parcelize
data class FeedAdapterRestaurant(
    val restaurantSection: FeedRestaurantSection
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.RESTAURANT)

@Parcelize
data class Tag(
    val id: Long?,
    val text: String,
    val icon_url: String? = null
) : Parcelable
