package com.bupp.wood_spoon_chef.analytics.event

import com.bupp.wood_spoon_chef.analytics.TrackedArea
import com.bupp.wood_spoon_chef.analytics.TrackedEvent

interface AnalyticsEvent {
    @TrackedArea val trackedArea: String
    @TrackedEvent val trackedEvent: String
}