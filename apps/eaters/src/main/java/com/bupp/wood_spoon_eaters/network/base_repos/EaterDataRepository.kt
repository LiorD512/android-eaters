package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.ResultManager

interface EaterDataRepositoryInterface{
    suspend fun getTraceableOrders(): ResultHandler<ServerResponse<List<Order>>>
//    suspend fun getFavorites(feedRequest: FeedRequest): ResultHandler<ServerResponse<Search>>
    suspend fun getTrigger(): ResultHandler<ServerResponse<Trigger>>
    suspend fun cancelOrder(orderId: Long, note: String?): ResultHandler<ServerResponse<Any>>
}

class EaterDataRepositoryImpl(private val service: ApiService, private val resultManager: ResultManager) : EaterDataRepositoryInterface {
    override suspend fun getTraceableOrders(): ResultHandler<ServerResponse<List<Order>>> {
        return resultManager.safeApiCall { service.getTraceableOrders() }
    }
//    override suspend fun getFavorites(feedRequest: FeedRequest): ResultHandler<ServerResponse<Search>> {
//        return resultManager.safeApiCall { service.getEaterFavorites(feedRequest.lat, feedRequest.lng, feedRequest.addressId, feedRequest.timestamp) }
//    }
    override suspend fun getTrigger(): ResultHandler<ServerResponse<Trigger>> {
        return resultManager.safeApiCall { service.getTriggers() }
    }

    override suspend fun cancelOrder(orderId: Long, note: String?): ResultHandler<ServerResponse<Any>> {
        return resultManager.safeApiCall { service.cancelOrder(orderId, note) }
    }

}