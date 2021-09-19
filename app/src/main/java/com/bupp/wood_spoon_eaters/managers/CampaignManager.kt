package com.bupp.wood_spoon_eaters.managers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.model.Campaign
import com.bupp.wood_spoon_eaters.model.UserInteractionStatus
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.repositories.CampaignRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CampaignManager(private val campaignRepository: CampaignRepository) {


    private val campaignLiveData = MutableLiveData<List<Campaign>?>()
    fun getCampaignLiveData() = campaignLiveData

    var curCampaigns: List<Campaign>? = null

    private var referralToken: String? = null
    suspend fun setUserReferralToken(token: String? = null) {
        MTLogger.c(TAG, "setUserReferralToken: token: $token")
        this.referralToken = token
        token?.let {
            onFlowEventFired(FlowEventsManager.FlowEvents.DEEP_LINK_TOKEN_UPDATED)
        }
    }

    suspend fun onFlowEventFired(curEvent: FlowEventsManager.FlowEvents) {
        //every time we are checking a campaign, we first check if user have a deepLink token. if have we will update server before re-fetching active campaigns
        MTLogger.c(TAG, "onFlowEventFired: event: $curEvent")
        if (validateReferral()) {
            Log.d(TAG, "validateReferral - success")
            setUserReferralToken(null)
            fetchCampaigns()
        } else {
            fetchCampaigns()
        }
        checkCampaignFor(curEvent)
    }

    private suspend fun validateReferral(): Boolean {
        //this function called on each event that is related to Campaigns. ->  we send the token from the deep link to the server.
        referralToken?.let {
            val result = withContext(Dispatchers.IO) {
                campaignRepository.validateReferral(it)
            }
            result.let {
                return when (result) {
                    is ResultHandler.Success -> {
                        MTLogger.c(TAG, "validateReferralToken - Success")
                        referralToken =  null
                        true
                    }
                    is ResultHandler.WSCustomError -> {
                        MTLogger.c(TAG, "validateReferralToken - wsError")
                        referralToken =  null
                        false
                    }
                    else -> {
                        false
                    }
                }
            }
        }
        return false
    }


    private suspend fun fetchCampaigns() {
        val result = withContext(Dispatchers.IO) {
            campaignRepository.fetchCampaigns()
        }
        this.curCampaigns = result
    }

    private suspend fun checkCampaignFor(curEvent: FlowEventsManager.FlowEvents) {
        //check if any of the active campaigns is of type "curEvent" - if so, show campaign and update campaign status
        MTLogger.c(TAG, "checkCampaignFor: $curEvent")
        curCampaigns?.let {
            val campaigns = it.filter { it.isMatchingEvent(curEvent) }
            if(campaigns.isNotEmpty()){
                MTLogger.c(TAG, "checkCampaignFor: $curEvent FOUND!")
                campaignLiveData.postValue(campaigns)

                campaigns.forEach { campaign ->
                    if(campaign.status != UserInteractionStatus.SEEN){
                        campaign.userInteractionId?.let {
                            updateCampaignStatus(it, UserInteractionStatus.SEEN)
                        }
                    }
                }
            }else{
                campaignLiveData.postValue(null)
            }
        }
    }

    suspend fun updateCampaignStatus(userInteractionId: Long, status: UserInteractionStatus) {
        MTLogger.c(TAG, "updateCampaignStatus: $userInteractionId")
        withContext(Dispatchers.IO) {
            campaignRepository.updateCampaignStatus(userInteractionId, status)
        }
    }


    companion object {
        const val TAG = "wowCampaignManager"
    }


}