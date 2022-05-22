package com.bupp.wood_spoon_chef.analytics.event.bottomTab

import com.bupp.wood_spoon_chef.analytics.TrackedArea
import com.bupp.wood_spoon_chef.analytics.TrackedEvents.BottomTabs.CLICK_ON_BOTTOM_TAB_ORDERS
import com.bupp.wood_spoon_chef.analytics.event.AnalyticsEvent

class BottomTabOrdersEvent: AnalyticsEvent {
    override val trackedArea: String = TrackedArea.BOTTOM_TABS
    override val trackedEvent: String = CLICK_ON_BOTTOM_TAB_ORDERS
}