package com.bupp.wood_spoon_eaters.features.main.order_history

import com.bupp.wood_spoon_eaters.model.Order

sealed class OrderHistoryBaseItem(
    val type: OrderHistoryViewType
)

enum class OrderHistoryViewType{
    ACTIVE_ORDER,
    TITLE,
    ORDER
}

data class OrderAdapterItemActiveOrder(
    val order: Order
): OrderHistoryBaseItem(OrderHistoryViewType.ACTIVE_ORDER)

data class OrderAdapterItemOrder(
    val order: Order
): OrderHistoryBaseItem(OrderHistoryViewType.ORDER)

data class OrderAdapterItemTitle(
    val title: String
): OrderHistoryBaseItem(OrderHistoryViewType.TITLE)
