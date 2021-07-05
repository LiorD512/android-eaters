package com.bupp.wood_spoon_eaters.repositories

import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.managers.CampaignManager
import com.bupp.wood_spoon_eaters.model.Campaign
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.model.UserInteractionStatus
import com.bupp.wood_spoon_eaters.model.WSError
import com.bupp.wood_spoon_eaters.network.base_repos.CampaignRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CampaignRepository(private val campaignRepo: CampaignRepositoryImpl){//}, val repoErrorManager: RepoErrorManager) {
    //todo - think about adding a global error Handeling Manager - impliment in all repos

//    data class CampaignRepositoryResult<T>(val type: CampaignRepositoryStatus, val data: T? = null, val wsError: List<WSError>? = null)
//    enum class CampaignRepositoryStatus {
//        VALIDATE_REFERRAL_TOKEN_SUCCESS,
//        VALIDATE_REFERRAL_TOKEN_FAILED,
//
//        GET_CAMPAIGN_SUCCESS,
//        GET_CAMPAIGN_FAILED,
//
//        UPDATE_CAMPAIGN_STATUS_SUCCESS,
//        UPDATE_CAMPAIGN_STATUS_FAILED,
//
//        SERVER_ERROR,
//        SOMETHING_WENT_WRONG,
//        WS_ERROR
//    }

    suspend fun validateReferral(token: String): ResultHandler<ServerResponse<Any>> {
        return withContext(Dispatchers.IO) {
            campaignRepo.validateReferralToken(token)
        }
    }

    suspend fun fetchCampaigns(): ResultHandler<ServerResponse<List<Campaign>>> {
        return withContext(Dispatchers.IO){
            campaignRepo.getActiveCampaigns()
        }
    }

    suspend fun updateCampaignStatus(userInteractionId: Long, status: UserInteractionStatus): ResultHandler<ServerResponse<Any>> {
        return withContext(Dispatchers.IO) {
            campaignRepo.updateCampaignStatus(userInteractionId, status)
        }

        //todo - after adding globalErrorManager add these fields -
//        is ResultHandler.NetworkError -> {
//            MTLogger.c(CampaignManager.TAG, "updateCampaignStatus - NetworkError")
////                    EaterDataRepository.EaterDataRepoResult(EaterDataRepository.EaterDataRepoStatus.SERVER_ERROR)
//        }
//        is ResultHandler.GenericError -> {
//            MTLogger.c(CampaignManager.TAG, "updateCampaignStatus - GenericError")
////                    EaterDataRepository.EaterDataRepoResult(EaterDataRepository.EaterDataRepoStatus.UPDATE_CAMPAIGN_STATUS_FAILED)
//        }
//        is ResultHandler.WSCustomError -> {
//            MTLogger.c(CampaignManager.TAG, "updateCampaignStatus - wsError")
////                    EaterDataRepository.EaterDataRepoResult(EaterDataRepository.EaterDataRepoStatus.WS_ERROR, wsError = result.errors)
//        }
    }


    companion object {
        const val TAG = "wowCampaignRepository"
    }


}