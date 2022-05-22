package com.bupp.wood_spoon_chef.analytics.event.calendar

import com.bupp.wood_spoon_chef.analytics.TrackedArea
import com.bupp.wood_spoon_chef.analytics.TrackedEvents
import com.bupp.wood_spoon_chef.analytics.event.AnalyticsEvent

class CalendarCreateCookingSlotEvent : AnalyticsEvent {
    override val trackedArea: String = TrackedArea.CALENDAR
    override val trackedEvent: String = TrackedEvents.Calendar.CLICK_ON_ITEM_CREATE_COOKING_SLOT
}