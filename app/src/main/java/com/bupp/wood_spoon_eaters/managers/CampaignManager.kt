package com.bupp.wood_spoon_eaters.managers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.EaterDataRepository

class CampaignManager(private val eaterDataRepository: EaterDataRepository, private val eaterDataManager: EaterDataManager) {

    var currentCampaigns: List<Campaign>? = null
    private val onCampaignUpdateEvent = MutableLiveData<List<Campaign>>()
    fun getCampaignUpdateEvent() = onCampaignUpdateEvent

    private val campaignLiveData = MutableLiveData<CampaignData>()
    fun getCampaignLiveData() = campaignLiveData

    suspend fun fetchCampaigns() {
        val result = eaterDataRepository.checkForCampaign()
        when(result.type){
            EaterDataRepository.EaterDataRepoStatus.GET_CAMPAIGN_SUCCESS -> {
                MTLogger.d(TAG, "fetchCampaigns - success")
                this.currentCampaigns = result.data
                onCampaignUpdateEvent.postValue(currentCampaigns)
            }
            else -> {
                MTLogger.d(TAG, "fetchCampaigns - failed")
            }
        }
    }

    fun checkCampaignFor(showAfter: CampaignShowAfter){
        currentCampaigns?.let{
            val campaign = it.find { it.showAfter == showAfter }
            campaign?.let{ currentCampaign ->
                val eater = eaterDataManager.currentEater
                campaignLiveData.postValue(CampaignData(eater, currentCampaign))
            }
        }
    }

    suspend fun validateReferral() {
        val result = eaterDataManager.validateReferral()
        result?.let{
            when (it.type) {
                EaterDataRepository.EaterDataRepoStatus.VALIDATE_REFERRAL_TOKEN_SUCCESS -> {
                    Log.d(TAG, "validateReferral - success")
                    fetchCampaigns()
                }
                EaterDataRepository.EaterDataRepoStatus.VALIDATE_REFERRAL_TOKEN_FAILED -> {
                    Log.d(TAG, "validateReferral - failed")
                }
                EaterDataRepository.EaterDataRepoStatus.WS_ERROR -> {
                    Log.d(TAG, "validateReferral - es error")

                }
                else -> {

                }
            }
        }
    }

    suspend fun updateCampaignStatus(userInteractionId: Long?, status: UserInteractionStatus) {
        userInteractionId?.let{
            val result = eaterDataRepository.updateCampaignStatus(it, status)
            when(result.type){
                EaterDataRepository.EaterDataRepoStatus.UPDATE_CAMPAIGN_STATUS_SUCCESS -> {
                    MTLogger.d(TAG, "updateCampaignStatus - success")
                    fetchCampaigns()
                }
                else -> {
                    MTLogger.d(TAG, "updateCampaignStatus - failed")
                }
            }
        }
    }

    companion object{
        const val TAG = "wowCampaignManager"
    }


}