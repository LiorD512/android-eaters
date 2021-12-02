package com.bupp.wood_spoon_eaters.common

import android.util.Log
import com.bupp.wood_spoon_eaters.managers.CampaignManager
import com.bupp.wood_spoon_eaters.managers.EventsManager

class FlowEventsManager(private val campaignManager: CampaignManager, private val eventsManager: EventsManager) {

    enum class FlowEvents{
        DEEP_LINK_TOKEN_UPDATED,
        VISIT_FEED,
        VISIT_PROFILE,


        PAGE_VISIT_ON_BOARDING,
        PAGE_VISIT_GET_OTF_CODE,
        PAGE_VISIT_VERIFY_OTF_CODE,
        PAGE_VISIT_CREATE_ACCOUNT,
        PAGE_VISIT_FEED,
        PAGE_VISIT_SEARCH,
        PAGE_VISIT_ACCOUNT,
        PAGE_VISIT_ORDERS,
        PAGE_VISIT_PRIVACY_POLICY,
        PAGE_VISIT_QA,
        PAGE_VISIT_COMMUNICATION_SETTINGS,
        PAGE_VISIT_SUPPORT_CENTER,
        PAGE_VISIT_EDIT_ACCOUNT,
        PAGE_VISIT_JOIN_HOME_CHEF,
        PAGE_VISIT_ADDRESSES,
        PAGE_VISIT_DELETE_ACCOUNT,
        PAGE_VISIT_CHECKOUT,
        PAGE_VISIT_TRACK_ORDER,
        PAGE_VISIT_LOCATION_PERMISSION,
        PAGE_VISIT_HOME_CHEF,
        PAGE_VISIT_DISH,
        PAGE_VISIT_CART,

    }

    fun logPageEvent(curEvent: FlowEvents){
        Log.d(TAG, "current event fired: $curEvent")
        eventsManager.onFlowEventFired(curEvent)
    }

    suspend fun fireEvent(curEvent: FlowEvents){
        campaignManager.onFlowEventFired(curEvent)
        eventsManager.onFlowEventFired(curEvent)
    }

    companion object{
        const val TAG = "wowFlowEventsManager"
    }

}