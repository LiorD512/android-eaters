package com.eatwoodspoon.analytics

import com.eatwoodspoon.analytics.events.AnalyticsEvent

interface AnalyticsEventReporter {
    fun reportEvent(event: AnalyticsEvent)
}