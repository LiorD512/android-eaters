package com.bupp.wood_spoon_eaters.features.main.search

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.model.FeedAdapterItem
import com.bupp.wood_spoon_eaters.model.FeedAdapterViewType
import com.bupp.wood_spoon_eaters.model.FeedRestaurantSection
import com.bupp.wood_spoon_eaters.model.Order
import kotlinx.parcelize.Parcelize

sealed class SearchBaseItem(
    val type: SearchViewType
)

enum class SearchViewType{
    EMPTY,
    TITLE,
    RESTAURANT,
    RESTAURANT_SKELETON,
    SEARCH_TAGS,
    SEARCH_TAGS_SKELETON,
}

data class SearchAdapterTitle(
    val title: String
): SearchBaseItem(SearchViewType.TITLE)

data class SearchAdapterTagsSkeleton(
    val id: Long? = null
): SearchBaseItem(SearchViewType.SEARCH_TAGS_SKELETON)

data class SearchAdapterRestaurantSkeleton(
    val id: Long? = null
): SearchBaseItem(SearchViewType.RESTAURANT_SKELETON)

data class SearchAdapterEmpty(
    val id: Long? = null
): SearchBaseItem(SearchViewType.EMPTY)

data class SearchAdapterRestaurant(
    val restaurantSection: FeedRestaurantSection,
    val id: Long?,
): SearchBaseItem(SearchViewType.RESTAURANT)

data class SearchAdapterTag(
    val tag: String? = null
): SearchBaseItem(SearchViewType.SEARCH_TAGS)
