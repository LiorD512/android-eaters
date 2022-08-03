package com.bupp.wood_spoon_chef.presentation.features.cooking_slot

import com.bupp.wood_spoon_chef.analytics.ChefAnalyticsTracker
import com.bupp.wood_spoon_chef.analytics.trackEvent
import com.eatwoodspoon.analytics.events.ChefsCookingSlotsEvent

class CookingSlotReportEventUseCase(private val chefAnalyticsTracker: ChefAnalyticsTracker) {
    fun reportEvent(event: ChefsCookingSlotsEvent) {
        chefAnalyticsTracker.trackEvent(event)
    }
}