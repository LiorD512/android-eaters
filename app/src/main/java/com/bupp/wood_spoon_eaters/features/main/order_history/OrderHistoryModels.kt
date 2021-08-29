package com.bupp.wood_spoon_eaters.features.main.order_history

import com.bupp.wood_spoon_eaters.model.Order

sealed class OrderHistoryBaseItem(
    val type: OrderHistoryViewType
)

enum class OrderHistoryViewType{
    SKELETON,
    ACTIVE_ORDER,
    TITLE,
    ORDER
}

data class OrderAdapterItemSkeleton(
    val id: Long? = null
): OrderHistoryBaseItem(OrderHistoryViewType.SKELETON)

data class OrderAdapterItemActiveOrder(
    var order: Order,
    var isLast: Boolean = false
): OrderHistoryBaseItem(OrderHistoryViewType.ACTIVE_ORDER)

data class OrderAdapterItemOrder(
    var order: Order
): OrderHistoryBaseItem(OrderHistoryViewType.ORDER)

data class OrderAdapterItemTitle(
    val title: String
): OrderHistoryBaseItem(OrderHistoryViewType.TITLE)
