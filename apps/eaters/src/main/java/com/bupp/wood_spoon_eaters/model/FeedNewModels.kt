package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.squareup.moshi.*
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
    var collections: MutableList<FeedSectionCollectionItem>? = null,
    @Json(name = "section_type")
    var sectionType: String? = null
): Parcelable

sealed class FeedSectionCollectionItem(
    @Json(name = "type") var type: FeedModelsViewType?
) : Parcelable {
    abstract val items: List<Parcelable>?
    abstract val full_href: String?
}

@Parcelize
//@JsonClass(generateAdapter = true)
data class FeedUnknownSection(
    override val full_href: String? = null,
    override val items: List<Campaign>? = null,
    val unknownTypeValue: String?
): Parcelable, FeedSectionCollectionItem(FeedModelsViewType.UNKNOWN)

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
data class FeedHeroItemSection(
    override val full_href: String? = null,
    override val items: List<FeedRestaurantSectionItem>? = null,

    var id: String?,
    var title: String?,
    var text: String?,
    var url: String?,
    var image: WSImage?,
) : Parcelable, FeedSectionCollectionItem(FeedModelsViewType.HERO)

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedChefItemSection(
    override val full_href: String? = null,
    override val items: List<FeedRestaurantSectionItem>? = null,
    @Json(name = "cook") val cook: Cook?,
    @Json(name = "cooking_slot") val cookingSlot: FeedDishCookingSlot?,
) : Parcelable, FeedSectionCollectionItem(FeedModelsViewType.CHEF)

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedDishItemSection(
    override val full_href: String? = null,
    override val items: List<FeedRestaurantSectionItem>? = null,
    @Json(name = "id") val dishId: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "price") val price: String?,
    @Json(name = "cook") val cook: Cook?,
    @Json(name = "cooking_slot") val cookingSlot: FeedDishCookingSlot?,
    @Json(name = "thumbnail") val thumbnail: WSImage?,
    @Json(name = "tags") val tags: List<String>?
) : Parcelable, FeedSectionCollectionItem(FeedModelsViewType.DISH)

//@Parcelize
//@JsonClass(generateAdapter = true)
//data class QuickLinkItem(
//    override val items: List<FeedRestaurantSectionItem>? = null,
//    override val full_href: String? = null,
//    var title: String?,
//    var text: String?,
//    var url: String?,
//    var image: WSImage?,
//): Parcelable, FeedSectionCollectionItem(FeedModelsViewType.QUICK_LINK)

//@Parcelize
//@JsonClass(generateAdapter = true)
//data class ReviewItem(
//    override val items: List<FeedRestaurantSectionItem>? = null,
//    override val full_href: String? = null,
//): Parcelable, FeedSectionCollectionItem(FeedModelsViewType.REVIEW)

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

    fun toRestaurantInitParams(
        sectionTitle: String? = null,
        sectionOrder: Int? = null,
        restaurantOrderInSection: Int? = null,
        dishIndexInRestaurant: Int? = null,
        isFromSearch: Boolean = false,
        itemType: String? = null
    ) = RestaurantInitParams(
        chefId,
        chefThumbnail,
        chefCover,
        getAvgRating(),
        restaurantName,
        chefName,
        false,
        null,
        isFromSearch,
        false,
        cookingSlot,
        sectionTitle,
        sectionOrder,
        restaurantOrderInSection,
        dishIndexInRestaurant,
        selectedDishId = null,
        itemType
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
    val thumbnail: WSImage?,
    val formatted_price: String?,
    val tags: List<String>?
) : Parcelable

@Parcelize
data class FeedRestaurantItemCover(
    val thumbnail: WSImage?
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class FeedRestaurantItemTypeSeeMore(
    override val data: FeedRestaurantItemSeeMore?
) : Parcelable, FeedRestaurantSectionItem(FeedRestaurantSectionItemViewType.SEE_MORE)

@Parcelize
//@JsonClass(generateAdapter = true)
// This class is currently not network data object. We "inject" this object locally; See MB-829
data class FeedRestaurantItemTypeCover(
    override val data: FeedRestaurantItemCover?
) : Parcelable, FeedRestaurantSectionItem(FeedRestaurantSectionItemViewType.COVER)


@Parcelize
data class FeedRestaurantUnknownSection(
    val unknownTypeValue: String?,
    override val data: Parcelable? = null
) : Parcelable, FeedRestaurantSectionItem(FeedRestaurantSectionItemViewType.UNKNOWN)



@Parcelize
@JsonClass(generateAdapter = true)
data class FeedRestaurantItemSeeMore(
    val title: String?,
    val thumbnail: WSImage?,
    val formatted_price: String?,
) : Parcelable


enum class FeedModelsViewType(val value: String) {
    COUPONS(value = "available_coupons"),
    RESTAURANT(value = "restaurant_overview"),
    EMPTY_FEED(value = "feed_empty_no_chefs"),
    EMPTY_SECTION(value = "section_empty_no_chefs"),
    EMPTY_SEARCH(value = "section_empty_no_matches"),
    COMING_SONG(value = "coming_soon"),
    HERO(value = "hero"),
//    QUICK_LINK(value = "quick_link"),
//    REVIEW(value = "review"),
    CHEF(value = "chef"),
    DISH(value = "dish"),
    UNKNOWN(value = "unknown");

    companion object {
        fun fromString(value: String): FeedModelsViewType {
            return values().firstOrNull { it.value == value } ?: UNKNOWN
        }
    }
 }

class FeedModelsViewTypeAdapter {
    @ToJson
    fun toJson(type: FeedModelsViewType): String = type.value

    @FromJson
    fun fromJson(value: String): FeedModelsViewType = FeedModelsViewType.fromString(value)
}

enum class FeedRestaurantSectionItemViewType(val value: String){
    DISH(value = "dish"),
    SEE_MORE(value = "see_more"),
    COVER(value = "cover"), // We are not actually receive these from BE
    UNKNOWN(value = "unknown");

    companion object {
        fun fromString(value: String): FeedRestaurantSectionItemViewType {
            return values().firstOrNull { it.value == value } ?: UNKNOWN
        }
    }
}

class FeedRestaurantSectionItemViewTypeAdapter {
    @ToJson
    fun toJson(type: FeedRestaurantSectionItemViewType): String = type.value

    @FromJson
    fun fromJson(value: String): FeedRestaurantSectionItemViewType = FeedRestaurantSectionItemViewType.fromString(value)
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
    HERO,
    CHEF,
    DISH,
    QUICK_LINK,
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
data class FeedAdapterHero(
    val heroList: MutableList<FeedHeroItemSection>, override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.HERO)

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
data class FeedAdapterChefSection(
    val chefSection: MutableList<FeedChefItemSection>, override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.CHEF)

@Parcelize
data class FeedAdapterDishSection(
    val dishSection: MutableList<FeedDishItemSection>, override val id: Long?
) : Parcelable, FeedAdapterItem(FeedAdapterViewType.DISH)

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