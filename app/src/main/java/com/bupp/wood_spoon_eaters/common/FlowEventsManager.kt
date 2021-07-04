package com.bupp.wood_spoon_eaters.common

import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.managers.CampaignManager
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.model.CampaignData
import com.squareup.moshi.Json

class FlowEventsManager(private val campaignManager: CampaignManager, private val eventsManager: EventsManager) {

    enum class FlowEvents{
        VISIT_FEED,
        ACTION_ADD_TO_CART,
        ACTION_PURCHASE,
        ACTION_RATE_ORDER,
        ACTION_CLEAR_CART,
    }

    suspend fun fireEvent(curEvent: FlowEvents){
        campaignManager.onFlowEventFired(curEvent)
//        eventsManager.onFlowEventFired(curEvent)
    }

}