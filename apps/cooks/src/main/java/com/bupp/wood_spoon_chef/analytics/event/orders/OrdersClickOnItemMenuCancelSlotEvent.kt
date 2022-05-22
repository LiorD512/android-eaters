package com.bupp.wood_spoon_chef.analytics.event.orders

import com.bupp.wood_spoon_chef.analytics.TrackedArea
import com.bupp.wood_spoon_chef.analytics.TrackedEvents
import com.bupp.wood_spoon_chef.analytics.event.AnalyticsEvent

class OrdersClickOnItemMenuCancelSlotEvent: AnalyticsEvent {
    override val trackedArea: String = TrackedArea.ORDERS
    override val trackedEvent: String = TrackedEvents.Orders.CLICK_ON_ITEM_MENU_CANCEL_SLOT
}