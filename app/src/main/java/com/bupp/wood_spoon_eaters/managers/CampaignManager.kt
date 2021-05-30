package com.bupp.wood_spoon_eaters.managers

import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.model.Campaign
import com.bupp.wood_spoon_eaters.model.CampaignData
import com.bupp.wood_spoon_eaters.model.CampaignShowAfter
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.repositories.EaterDataRepository

class CampaignManager(private val eaterDataRepository: EaterDataRepository, private val eaterDataManager: EaterDataManager) {

    var currentCampaigns: List<Campaign>? = null


    private val campaignLiveData = MutableLiveData<CampaignData>()
    fun getCampaignLiveData() = campaignLiveData

    suspend fun fetchCampaigns() {
        val result = eaterDataRepository.checkForCampaign()
        when(result.type){
            EaterDataRepository.EaterDataRepoStatus.GET_CAMPAIGN_SUCCESS -> {
                MTLogger.d(TAG, "fetchCampaigns - success")
                this.currentCampaigns = result.data
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

    companion object{
        const val TAG = "wowCampaignManager"
    }


}