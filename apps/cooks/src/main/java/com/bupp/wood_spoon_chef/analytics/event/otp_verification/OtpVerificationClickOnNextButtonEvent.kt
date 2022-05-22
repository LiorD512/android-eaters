package com.bupp.wood_spoon_chef.analytics.event.otp_verification

import com.bupp.wood_spoon_chef.analytics.TrackedArea
import com.bupp.wood_spoon_chef.analytics.TrackedEvents
import com.bupp.wood_spoon_chef.analytics.event.AnalyticsEvent

class OtpVerificationClickOnNextButtonEvent(
    val isSuccess: Boolean
): AnalyticsEvent {
    override val trackedArea: String = TrackedArea.OTP_VERIFICATION
    override val trackedEvent: String = TrackedEvents.OTPVerification.CLICK_ON_NEXT_BUTTON
}