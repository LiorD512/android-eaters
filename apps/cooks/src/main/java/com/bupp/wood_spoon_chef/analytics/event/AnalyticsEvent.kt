package com.bupp.wood_spoon_chef.analytics.event

//import com.bupp.wood_spoon_chef.analytics.TrackedArea
//import com.bupp.wood_spoon_chef.analytics.TrackedEvent
// Commented out due to lint bug causing linter crash
interface AnalyticsEvent {
    /*@TrackedArea*/ val trackedArea: String
    /*@TrackedEvent*/ val trackedEvent: String
}