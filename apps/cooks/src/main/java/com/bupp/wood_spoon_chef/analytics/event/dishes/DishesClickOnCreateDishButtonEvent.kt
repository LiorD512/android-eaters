package com.bupp.wood_spoon_chef.analytics.event.dishes

import com.bupp.wood_spoon_chef.analytics.TrackedArea
import com.bupp.wood_spoon_chef.analytics.TrackedEvents
import com.bupp.wood_spoon_chef.analytics.event.AnalyticsEvent

class DishesClickOnCreateDishButtonEvent : AnalyticsEvent {
    override val trackedArea: String = TrackedArea.DISHES
    override val trackedEvent: String = TrackedEvents.Dishes.CLICK_ON_CREATE_DISH_BUTTON
}