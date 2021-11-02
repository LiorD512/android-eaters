package com.bupp.wood_spoon_eaters.features.main.search

import com.bupp.wood_spoon_eaters.model.FeedRestaurantSection
import com.bupp.wood_spoon_eaters.model.Order

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
    RECENT_ORDER,
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


data class SearchAdapterRecentOrder(
    val order: Order? = null
): SearchBaseItem(SearchViewType.RECENT_ORDER)
