package com.bupp.wood_spoon_chef.analytics.event.orders

import com.bupp.wood_spoon_chef.analytics.TrackedArea
import com.bupp.wood_spoon_chef.analytics.TrackedEvents
import com.bupp.wood_spoon_chef.analytics.event.AnalyticsEvent

class OrdersEmptyListOpenedEvent : AnalyticsEvent {
    override val trackedArea: String = TrackedArea.ORDERS
    override val trackedEvent: String = TrackedEvents.Orders.OPENED_EMPTY_LISTS
}