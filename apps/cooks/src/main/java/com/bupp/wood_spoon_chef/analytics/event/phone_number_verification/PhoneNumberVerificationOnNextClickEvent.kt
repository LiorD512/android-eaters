package com.bupp.wood_spoon_chef.analytics.event.phone_number_verification

import com.bupp.wood_spoon_chef.analytics.TrackedArea
import com.bupp.wood_spoon_chef.analytics.TrackedEvents
import com.bupp.wood_spoon_chef.analytics.event.AnalyticsEvent

class PhoneNumberVerificationOnNextClickEvent(
    val isSuccess: Boolean
): AnalyticsEvent {
    override val trackedArea: String = TrackedArea.PHONE_VERIFICATION
    override val trackedEvent: String = TrackedEvents.PhoneNumber.CLICK_ON_NEXT_BUTTON
}