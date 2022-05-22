package com.bupp.wood_spoon_chef.analytics.event.onboarding

import com.bupp.wood_spoon_chef.analytics.TrackedArea
import com.bupp.wood_spoon_chef.analytics.TrackedEvents
import com.bupp.wood_spoon_chef.analytics.event.AnalyticsEvent

class OnboardingClickOnGetStartedEvent: AnalyticsEvent {
    override val trackedArea: String = TrackedArea.ONBOARDING
    override val trackedEvent: String = TrackedEvents.Onboarding.CLICK_ON_GET_STARTED_BUTTON
}