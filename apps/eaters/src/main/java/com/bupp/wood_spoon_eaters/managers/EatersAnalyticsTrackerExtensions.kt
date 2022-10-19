import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker

fun EatersAnalyticsTracker.onFlowEventFired(curEvent: FlowEventsManager.FlowEvents) {
    when (curEvent) {
        FlowEventsManager.FlowEvents.PAGE_VISIT_ON_BOARDING -> {
            segment.screen("onboarding")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_GET_OTF_CODE -> {
            segment.screen("getOtpCode")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_CREATE_ACCOUNT -> {
            segment.screen("createAccount")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_FEED -> {
            segment.screen("feed")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_ACCOUNT -> {
            segment.screen("account")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_ORDERS -> {
            segment.screen("orders")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_PRIVACY_POLICY -> {
            segment.screen("privacyPolicy")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_QA -> {
            segment.screen("popularQA")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_COMMUNICATION_SETTINGS -> {
            segment.screen("communicationSettings")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_EDIT_ACCOUNT -> {
            segment.screen("editAccount")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_JOIN_HOME_CHEF -> {
            segment.screen("joinHomeChef")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_ADDRESSES -> {
            segment.screen("addresses")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_DELETE_ACCOUNT -> {
            segment.screen("deleteAccount")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_CHECKOUT -> {
            segment.screen("checkout")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_TRACK_ORDER -> {
            segment.screen("trackOrder")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_LOCATION_PERMISSION -> {
            segment.screen("locationPersuasion")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_DISH -> {
            segment.screen("dish")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_HOME_CHEF -> {
            segment.screen("homeChef")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_CART -> {
            segment.screen("cart")
        }
        FlowEventsManager.FlowEvents.PAGE_VISIT_SEARCH -> {
            segment.screen("view_search_page")
        }
        else -> {}
    }
}