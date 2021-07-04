package com.bupp.wood_spoon_eaters.managers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.base_repos.CampaignRepositoryImpl
import com.bupp.wood_spoon_eaters.network.base_repos.EaterDataRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.repositories.EaterDataRepository
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CampaignManager( private val eaterDataManager: EaterDataManager, private val campaignRepo: CampaignRepositoryImpl) {

    private val campaignLiveData = MutableLiveData<CampaignData>()
    fun getCampaignLiveData() = campaignLiveData

    private var userDidDismissBanner = false

    var referralToken: String? = null
    suspend fun setUserReferralToken(token: String? = null) {
        MTLogger.d(TAG, "setUserReferralToken: token: $token")
        this.referralToken = token
        onFlowEventFired(FlowEventsManager.FlowEvents.DEEP_LINK_TOKEN_UPDATED)
    }

    suspend fun onFlowEventFired(curEvent: FlowEventsManager.FlowEvents) {
        //every time we are checking a campaign, we first check if user have a deepLink token. if have we will update server before re-fetching active campaigns
        MTLogger.d(TAG, "onFlowEventFired: event: $curEvent")
        if(validateReferral()){
            Log.d(TAG, "validateReferral - success")
            referralToken = null
            fetchCampaigns(curEvent)
        }else{
            fetchCampaigns(curEvent)
        }
    }

    private suspend fun validateReferral(): Boolean {
        //this function called on each event that is related to Campaigns. ->  we send the token from the deep link to the server.
        referralToken?.let {
            val result = withContext(Dispatchers.IO){
                campaignRepo.validateReferralToken(it)
            }
            result.let{
                return  when (result) {
                    is ResultHandler.Success -> {
                        MTLogger.c(TAG,"validateReferralToken - Success")
                        true
                    }
                    is ResultHandler.NetworkError -> {
                        MTLogger.c(TAG,"validateReferralToken - NetworkError")
                        false
                    }
                    is ResultHandler.GenericError -> {
                        MTLogger.c(TAG,"validateReferralToken - GenericError")
                        false
                    }
                    is ResultHandler.WSCustomError -> {
                        MTLogger.c(TAG,"validateReferralToken - wsError")
                        false
                    }
                }
            }
        }
        return false
    }


    private suspend fun fetchCampaigns(curEvent: FlowEventsManager.FlowEvents) {
        val result = withContext(Dispatchers.IO){
            campaignRepo.checkForCampaigns()
        }
        result.let{
            when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.d(EaterDataRepository.TAG,"checkForCampaigns - NetworkError")
                }
                is ResultHandler.GenericError -> {
                    MTLogger.d(EaterDataRepository.TAG,"checkForCampaigns - GenericError")
                }
                is ResultHandler.Success -> {
                    MTLogger.d(TAG, "fetchCampaigns - success")
                    checkCampaignFor(result.value.data, curEvent)
                }
                is ResultHandler.WSCustomError -> {
                    MTLogger.d(EaterDataRepository.TAG,"checkForCampaigns - something went wrong")
                }
            }
        }
    }

    private suspend fun checkCampaignFor(currentCampaigns: List<Campaign>?, curEvent: FlowEventsManager.FlowEvents) {
        //check if any of the active campaigns is of type "curEvent" - if so, show campaign and update campaign status
        MTLogger.d(TAG, "checkCampaign: $curEvent")
        currentCampaigns?.let {
            val campaign = it.find { it.showAfter.toString() == curEvent.toString() }
            campaign?.let { currentCampaign ->
                if(!userDidDismissBanner){
                    val eater = eaterDataManager.currentEater
                    campaignLiveData.postValue(CampaignData(eater, currentCampaign))
                    currentCampaign.userInteractionId?.let{
                        updateCampaignStatus(it, UserInteractionStatus.SEEN)
                    }
                }
            }
        }
    }

    suspend fun updateCampaignStatus(userInteractionId: Long, status: UserInteractionStatus) {
        val result = withContext(Dispatchers.IO){
            campaignRepo.updateCampaignStatus(userInteractionId, status)
        }
        result.let{
             when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.c(TAG,"updateCampaignStatus - NetworkError")
//                    EaterDataRepository.EaterDataRepoResult(EaterDataRepository.EaterDataRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    MTLogger.c(TAG,"updateCampaignStatus - GenericError")
//                    EaterDataRepository.EaterDataRepoResult(EaterDataRepository.EaterDataRepoStatus.UPDATE_CAMPAIGN_STATUS_FAILED)
                }
                is ResultHandler.Success -> {
                    MTLogger.c(TAG,"updateCampaignStatus - Success")
//                    EaterDataRepository.EaterDataRepoResult(EaterDataRepository.EaterDataRepoStatus.UPDATE_CAMPAIGN_STATUS_SUCCESS)
                }
                is ResultHandler.WSCustomError -> {
                    MTLogger.c(TAG,"updateCampaignStatus - wsError")
//                    EaterDataRepository.EaterDataRepoResult(EaterDataRepository.EaterDataRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }







//    suspend fun updateCampaignStatus(userInteractionId: Long?, status: UserInteractionStatus) {
//        userInteractionId?.let {
//            val result = updateCampaignStatus(it, status)
//            when (result.type) {
//                EaterDataRepository.EaterDataRepoStatus.UPDATE_CAMPAIGN_STATUS_SUCCESS -> {
//                    MTLogger.d(TAG, "updateCampaignStatus - success")
////                    fetchCampaigns(curEvent)
//                }
//                else -> {
//                    MTLogger.d(TAG, "updateCampaignStatus - failed")
//                }
//            }
//        }
//    }

//    override fun handleCampaignAction(campaign: Campaign) {
//        when(campaign.buttonAction){
//            CampaignButtonAction.SHARE -> {
//                campaign.shareUrl?.let{
//                    Utils.shareText(this, it)
//                }
//            }
//            CampaignButtonAction.ACKNOWLEDGE -> {
//                //do nothing
//            }
//            CampaignButtonAction.JUMP_TO_LINK -> {
//                //todo = add webView
//            }
//        }
//        viewModel.updateCampaignStatus(campaign, UserInteractionStatus.ENGAGED)
//
//    }


    companion object {
        const val TAG = "wowCampaignManager"
    }


}