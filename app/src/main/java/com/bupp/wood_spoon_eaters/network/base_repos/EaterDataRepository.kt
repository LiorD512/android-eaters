package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.safeApiCall

interface EaterDataRepositoryInterface{
    suspend fun getTraceableOrders(): ResultHandler<ServerResponse<List<Order>>>
    suspend fun getFavorites(feedRequest: FeedRequest): ResultHandler<ServerResponse<Search>>
    suspend fun getTrigger(): ResultHandler<ServerResponse<Trigger>>
    suspend fun cancelOrder(orderId: Long, note: String?): ResultHandler<ServerResponse<Any>>
//    suspend fun checkForCampaigns(): ResultHandler<ServerResponse<List<Campaign>>>
//    suspend fun validateReferralToken(token: String): ResultHandler<ServerResponse<Any>>
//    suspend fun updateCampaignStatus(userInteractionId: Long, status: UserInteractionStatus): ResultHandler<ServerResponse<Any>>

}

class EaterDataRepositoryImpl(private val service: ApiService) : EaterDataRepositoryInterface {
    override suspend fun getTraceableOrders(): ResultHandler<ServerResponse<List<Order>>> {
        return safeApiCall { service.getTraceableOrders() }
    }
    override suspend fun getFavorites(feedRequest: FeedRequest): ResultHandler<ServerResponse<Search>> {
        return safeApiCall { service.getEaterFavorites(feedRequest.lat, feedRequest.lng, feedRequest.addressId, feedRequest.timestamp) }
    }
    override suspend fun getTrigger(): ResultHandler<ServerResponse<Trigger>> {
        return safeApiCall { service.getTriggers() }
    }

    override suspend fun cancelOrder(orderId: Long, note: String?): ResultHandler<ServerResponse<Any>> {
        return safeApiCall { service.cancelOrder(orderId, note) }
    }

//    override suspend fun checkForCampaigns(): ResultHandler<ServerResponse<List<Campaign>>> {
//        return safeApiCall { service.getUserCampaign() }
//    }
//
//    override suspend fun validateReferralToken(token: String): ResultHandler<ServerResponse<Any>> {
//        return safeApiCall { service.validateReferralToken(token) }
//    }
//
//    override suspend fun updateCampaignStatus(userInteractionId: Long, status: UserInteractionStatus): ResultHandler<ServerResponse<Any>> {
//        return safeApiCall { service.updateCampaignStatus(userInteractionId, status.name.toLowerCase()) }
//    }


}