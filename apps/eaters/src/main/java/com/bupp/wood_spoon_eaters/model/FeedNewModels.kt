package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.features.main.search.SearchBaseItem
import com.bupp.wood_spoon_eaters.features.main.search.SearchViewType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
data class FeedResult(
    var sections: List<FeedSection>
)

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedSection(
    var id: Long? = null,
    var title: String? = null,
    var full_href: String? = null,
    var collections: MutableList<FeedSectionCollectionItem>? = null
): Parcelable

sealed class FeedSectionCollectionItem(
    @Json(name = "type") var type: FeedModelsViewType?
) : Parcelable {
    abstract val items: List<Parcelable>?
    abstract val full_href: String?
}

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedCampaignSection(
    override val full_href: String?,
    override val items: List<Campaign>?,
): Parcelable, FeedSectionCollectionItem(FeedModelsViewType.COUPONS)

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedIsEmptySection(
    override val full_href: String? = null,
    override val items: List<Campaign>? = null,
    @Json(name = "title") val title: String?,
    @Json(name = "subtitle") val subtitle: String?,
    @Json(name = "action") val action: String?
): Parcelable, FeedSectionCollectionItem(FeedModelsViewType.EMPTY_FEED)

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedSingleEmptySection(
    override val full_href: String? = null,
    override val items: List<Campaign>? = null,
    @Json(name = "title") val title: String?,
    @Json(name = "subtitle") val subtitle: String?,
    @Json(name = "action") val action: String?
): Parcelable, FeedSectionCollectionItem(FeedModelsViewType.EMPTY_SECTION)

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedSearchEmptySection(
    override val full_href: String? = null,
    override val items: List<Campaign>? = null,
    @Json(name = "title") val title: String?,
    @Json(name = "subtitle") val subtitle: String?,
    @Json(name = "action") val action: String?
): Parcelable, FeedSectionCollectionItem(FeedModelsViewType.EMPTY_SEARCH)

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedComingSoonSection(
    override val full_href: String? = null,
    override val items: List<Campaign>? = null,
    @Json(name = "title") val title: String?,
    @Json(name = "subtitle") val subtitle: String?,
    @Json(name = "success_subtitle") val successSubtitle: String?,
    @Json(name = "city_photo") val cityPhoto: WSImage?,
    @Json(name = "operational_zone_id") val operationalZoneId: Long?
): Parcelable, FeedSectionCollectionItem(FeedModelsViewType.COMING_SONG)

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedSearchTagsEmptySection(
    val id: Long = -1
): Parcelable


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
    override var full_href: String?,
    override val items: List<FeedRestaurantSectionItem>?,
    @Json(name = "chef_name") val chefName: String?,
    @Json(name = "restaurant_name") val restaurantName: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "chef_id") val chefId: Long?,
    @Json(name = "country_id") val countryId: Long?,
    @Json(name = "chef_thumbnail") val chefThumbnail: WSImage?,
    @Json(name = "chef_cover") val chefCover: WSImage?,
    @Json(name = "avg_rating") val avgRating: Float?,
    @Json(name = "cooking_slot") val cookingSlot: FeedDishCookingSlot?,
//    @Json(name = "availability") val availability: RestaurantAvailability?,
    var flagUrl: String?,
    var countryIso: String?,
) : Parcelable, FeedSectionCollectionItem(FeedModelsViewType.RESTAURANT) {

    fun isAvailable(): Boolean{
        return cookingSlot?.canOrder?: false
    }

    fun getAvgRating(): String{
        avgRating?.let{
            return String.format("%.1f", avgRating)
        }
        return ""
    }

    fun toRestaurantInitParams(sectionTitle: String? = null, sectionOrder: Int? = null, restaurantOrderInSection: Int? = null, dishIndexInRestaurant: Int? = null, isFromSearch: Boolean = false): RestaurantInitParams {
        return RestaurantInitParams(
            chefId,
            chefThumbnail,
            chefCover,
            getAvgRating(),
            restaurantName,
            chefName,
            false,
            null,
            isFromSearch,
            cookingSlot,
            sectionTitle,
            sectionOrder,
            restaurantOrderInSection,
            dishIndexInRestaurant
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
    @Json(name = "section_empty_no_matches")
    EMPTY_SEARCH,
    @Json(name = "coming_soon")
    COMING_SONG,
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
    SEARCH_TITLE,
    COUPONS,
    RESTAURANT,
    RESTAURANT_LARGE,
    EMPTY_FEED,
    EMPTY_SECTION,
    EMPTY_SEARCH,
    EMPTY_SEARCH_TAGS,
    SEARCH_TAGS,
    NO_NETWORK_SECTION,
    SKELETON,
    SKELETON_SEARCH,
    COMING_SOON,
    HREF
}

@Parcelize
data class FeedAdapterSkeleton(
    override var id: Long? = null
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.SKELETON)

@Parcelize
data class FeedAdapterSearchSkeleton(
    override var id: Long? = null
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.SKELETON_SEARCH)

@Parcelize
data class FeedAdapterHref(
    val full_href: String? = null, override val id: Long?
): Parcelable, FeedAdapterItem(FeedAdapterViewType.HREF)

@Parcelize
data class FeedAdapterTitle(
    val title: String, override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.TITLE)

@Parcelize
data class FeedAdapterSearchTitle(
    val title: String, override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.SEARCH_TITLE)

@Parcelize
data class FeedAdapterCoupons(
    val couponSection: FeedCampaignSection, override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.COUPONS)

@Parcelize
data class FeedAdapterRestaurant(
    val restaurantSection: FeedRestaurantSection, override val id: Long?,

    /** ANALYTICS PARAMS **/
    val sectionTitle: String? = null,
    val sectionOrder: Int? = null,
    val restaurantOrderInSection: Int? = null,
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.RESTAURANT)

@Parcelize
data class FeedAdapterEmptyFeed(
    val emptyFeedSection: FeedIsEmptySection, override val id: Long?,
    val shouldShowBtn: Boolean = true
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.EMPTY_FEED)

@Parcelize
data class FeedAdapterEmptySection(
    val emptySection: FeedSingleEmptySection, override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.EMPTY_SECTION)

@Parcelize
data class FeedAdapterComingSoonSection(
    val comingSoonSection: FeedComingSoonSection, override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.COMING_SOON)

@Parcelize
data class FeedAdapterNoNetworkSection(
    override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.NO_NETWORK_SECTION)

@Parcelize
data class FeedAdapterLargeRestaurant(
    val restaurantSection: FeedRestaurantSection, override val id: Long?,

    /** ANALYTICS PARAMS **/
    val sectionTitle: String? = null,
    val sectionOrder: Int? = null,
    val restaurantOrderInSection: Int? = null,
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.RESTAURANT_LARGE)

@Parcelize
data class FeedAdapterEmptySearch(
    val emptySection: FeedSearchEmptySection, override val id: Long? = null
): Parcelable, FeedAdapterItem(FeedAdapterViewType.EMPTY_SEARCH)

@Parcelize
data class FeedAdapterEmptySearchTags(
    override val id: Long? = null
): Parcelable, FeedAdapterItem(FeedAdapterViewType.EMPTY_SEARCH_TAGS)

@Parcelize
data class FeedAdapterSearchTag(
    override val id: Long? = null,
    val tags: List<String>? = null
): Parcelable, FeedAdapterItem(FeedAdapterViewType.SEARCH_TAGS)