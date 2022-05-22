package com.bupp.wood_spoon_chef.analytics.event.bottomTab

import com.bupp.wood_spoon_chef.analytics.TrackedArea
import com.bupp.wood_spoon_chef.analytics.TrackedEvents
import com.bupp.wood_spoon_chef.analytics.event.AnalyticsEvent

class BottomTabAccountEvent: AnalyticsEvent {
    override val trackedArea: String = TrackedArea.BOTTOM_TABS
    override val trackedEvent: String = TrackedEvents.BottomTabs.CLICK_ON_BOTTOM_TAB_ACCOUNT
}