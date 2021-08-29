package com.bupp.wood_spoon_eaters.repositories

import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.managers.GlobalErrorManager
import com.bupp.wood_spoon_eaters.model.Campaign
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.model.UserInteractionStatus
import com.bupp.wood_spoon_eaters.network.base_repos.CampaignRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CampaignRepository(private val campaignRepo: CampaignRepositoryImpl, private val globalErrorManager: GlobalErrorManager) {

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
        val result = withContext(Dispatchers.IO) {
            campaignRepo.validateReferralToken(token)
        }
        when(result){
            is ResultHandler.NetworkError -> {
                MTLogger.c(TAG, "validateReferral - NetworkError")
                globalErrorManager.postError(GlobalErrorManager.GlobalErrorType.NETWORK_ERROR)
            }
            is ResultHandler.GenericError -> {
                MTLogger.c(TAG, "validateReferral - GenericError")
                globalErrorManager.postError(GlobalErrorManager.GlobalErrorType.GENERIC_ERROR)
            }
            is ResultHandler.WSCustomError -> {
                MTLogger.c(TAG, "validateReferral - wsError")
                if(result.errors?.isNotEmpty() == true){
                    globalErrorManager.postError(GlobalErrorManager.GlobalErrorType.WS_ERROR, wsError = result.errors[0])
                }
            }
            else -> {}
        }
        return result
    }

    suspend fun fetchCampaigns(): List<Campaign>? {
        val result = withContext(Dispatchers.IO){
            campaignRepo.getActiveCampaigns()
        }
        when(result){
            is ResultHandler.Success -> {
                return result.value.data
            }
            is ResultHandler.NetworkError -> {
                MTLogger.c(TAG, "validateReferral - NetworkError")
                globalErrorManager.postError(GlobalErrorManager.GlobalErrorType.NETWORK_ERROR)
            }
            is ResultHandler.GenericError -> {
                MTLogger.c(TAG, "validateReferral - GenericError")
                globalErrorManager.postError(GlobalErrorManager.GlobalErrorType.GENERIC_ERROR)
            }
            is ResultHandler.WSCustomError -> {
                MTLogger.c(TAG, "validateReferral - wsError")
                if(result.errors?.isNotEmpty() == true){
                    globalErrorManager.postError(GlobalErrorManager.GlobalErrorType.WS_ERROR, wsError = result.errors[0])
                }
            }
        }
        return null
    }

//    suspend fun fetchCampaigns(): ResultHandler<ServerResponse<List<Campaign>>> {
//        return withContext(Dispatchers.IO){
//            campaignRepo.getActiveCampaigns()
//        }
//    }

    suspend fun updateCampaignStatus(userInteractionId: Long, status: UserInteractionStatus) {
        val result = withContext(Dispatchers.IO){
            campaignRepo.updateCampaignStatus(userInteractionId, status)
        }
        when(result){
            is ResultHandler.Success -> {
                MTLogger.c(TAG, "updateCampaignStatus - Success")
            }
            is ResultHandler.NetworkError -> {
                MTLogger.c(TAG, "updateCampaignStatus - NetworkError")
                globalErrorManager.postError(GlobalErrorManager.GlobalErrorType.NETWORK_ERROR)
            }
            is ResultHandler.GenericError -> {
                MTLogger.c(TAG, "updateCampaignStatus - GenericError")
                globalErrorManager.postError(GlobalErrorManager.GlobalErrorType.GENERIC_ERROR)
            }
            is ResultHandler.WSCustomError -> {
                MTLogger.c(TAG, "updateCampaignStatus - wsError")
                if(result.errors?.isNotEmpty() == true){
                    globalErrorManager.postError(GlobalErrorManager.GlobalErrorType.WS_ERROR, wsError = result.errors[0])
                }
            }
        }
    }


    companion object {
        const val TAG = "wowCampaignRepository"
    }


}