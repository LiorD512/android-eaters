package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.RestaurantInitParams
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
    var title: String? = null,
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

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedIsEmptySection(
    override val href: String? = null,
    override val items: List<Campaign>? = null,
    @Json(name = "title") val title: String?,
    @Json(name = "subtitle") val subtitle: String?,
    @Json(name = "action") val action: String?
): Parcelable, FeedSectionCollectionItem(FeedModelsViewType.EMPTY_FEED)

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedSingleEmptySection(
    override val href: String? = null,
    override val items: List<Campaign>? = null,
    @Json(name = "title") val title: String?,
    @Json(name = "subtitle") val subtitle: String?,
    @Json(name = "action") val action: String?
): Parcelable, FeedSectionCollectionItem(FeedModelsViewType.EMPTY_SECTION)


@Parcelize
@JsonClass(generateAdapter = true)
data class FeedNoChefSectionTest(
    @Json(name = "title") val title: String?,
    @Json(name = "subtitle") val subtitle: String?,
    @Json(name = "action") val action: String?
): Parcelable



@Parcelize
@JsonClass(generateAdapter = true)
data class FeedRestaurantSection(
    override var href: String?,
    override val items: List<FeedRestaurantSectionItem>?,
    @Json(name = "chef_name") val chefName: String?,
    @Json(name = "restaurant_name") val restaurantName: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "chef_id") val chefId: Long?,
    @Json(name = "chef_thumbnail") val chefThumbnail: WSImage?,
    @Json(name = "chef_cover") val chefCover: WSImage?,
    @Json(name = "avg_rating") val avgRating: Double?,
) : Parcelable, FeedSectionCollectionItem(FeedModelsViewType.RESTAURANT) {
    fun toRestaurantInitParams(): RestaurantInitParams {
        return RestaurantInitParams(
            chefId,
            chefThumbnail,
            chefCover,
            avgRating,
            restaurantName,
            chefName,
            false
        )
    }
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
    val thumbnail: WSImage?,
    val formatted_price: String?,
    val tags: List<String>?
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
    val thumbnail: WSImage?,
    val formatted_price: String?,
) : Parcelable


enum class FeedModelsViewType {
    @Json(name = "available_coupons")
    COUPONS,
    @Json(name = "restaurant_overview")
    RESTAURANT,
    @Json(name = "feed_empty_no_chefs")
    EMPTY_FEED,
    @Json(name = "section_empty_no_chefs")
    EMPTY_SECTION,
}

enum class FeedRestaurantSectionItemViewType{
    @Json(name = "dish") DISH,
    @Json(name = "see_more") SEE_MORE,
}

sealed class FeedAdapterItem(
    var type: FeedAdapterViewType?
) : Parcelable{
    abstract val id: Long?
}

enum class FeedAdapterViewType {
    TITLE,
    COUPONS,
    RESTAURANT,
    RESTAURANT_LARGE,
    EMPTY_FEED,
    EMPTY_SECTION,
    SKELETON,
    HREF
}

@Parcelize
data class FeedAdapterSkeleton(
    override var id: Long? = null
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.SKELETON)

@Parcelize
data class FeedAdapterHref(
    val href: String? = null, override val id: Long?
): Parcelable, FeedAdapterItem(FeedAdapterViewType.HREF)

@Parcelize
data class FeedAdapterTitle(
    val title: String, override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.TITLE)

@Parcelize
data class FeedAdapterCoupons(
    val couponSection: FeedCampaignSection, override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.COUPONS)

@Parcelize
data class FeedAdapterRestaurant(
    val restaurantSection: FeedRestaurantSection, override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.RESTAURANT)

@Parcelize
data class FeedAdapterEmptyFeed(
    val emptyFeedSection: FeedIsEmptySection, override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.EMPTY_FEED)

@Parcelize
data class FeedAdapterEmptySection(
    val emptySection: FeedSingleEmptySection, override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.EMPTY_SECTION)

@Parcelize
data class FeedAdapterLargeRestaurant(
    val restaurantSection: FeedRestaurantSection, override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.RESTAURANT_LARGE)

@Parcelize
data class Tag(
    val id: Long?,
    val text: String,
    val icon_url: String? = null
) : Parcelable
