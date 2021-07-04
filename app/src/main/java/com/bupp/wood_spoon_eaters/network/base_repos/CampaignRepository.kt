package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.safeApiCall

interface CampaignRepositoryInterface{
    suspend fun checkForCampaigns(): ResultHandler<ServerResponse<List<Campaign>>>
    suspend fun validateReferralToken(token: String): ResultHandler<ServerResponse<Any>>
    suspend fun updateCampaignStatus(userInteractionId: Long, status: UserInteractionStatus): ResultHandler<ServerResponse<Any>>

}

class CampaignRepositoryImpl(private val service: ApiService) : CampaignRepositoryInterface {

    override suspend fun checkForCampaigns(): ResultHandler<ServerResponse<List<Campaign>>> {
        return safeApiCall { service.getUserCampaign() }
    }

    override suspend fun validateReferralToken(token: String): ResultHandler<ServerResponse<Any>> {
        return safeApiCall { service.validateReferralToken(token) }
    }

    override suspend fun updateCampaignStatus(userInteractionId: Long, status: UserInteractionStatus): ResultHandler<ServerResponse<Any>> {
        return safeApiCall { service.updateCampaignStatus(userInteractionId, status.name.toLowerCase()) }
    }


}