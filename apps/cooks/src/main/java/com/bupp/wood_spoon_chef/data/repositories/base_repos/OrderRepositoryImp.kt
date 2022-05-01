package com.bupp.wood_spoon_chef.data.repositories.base_repos

import com.bupp.wood_spoon_chef.data.remote.model.Order
import com.bupp.wood_spoon_chef.data.remote.network.ApiService
import com.bupp.wood_spoon_chef.data.remote.network.ResponseHandler
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult

interface BaseOrderRepository {
    suspend fun getOrders(): ResponseResult<List<Order>>

    suspend fun setStatusAccept(orderId: Long): ResponseResult<Any>
    suspend fun setStatusStart(orderId: Long): ResponseResult<Any>
    suspend fun setStatusFinish(orderId: Long): ResponseResult<Any>

    suspend fun cancelOrder(orderId: Long, reasonId: Long?, notes: String?): ResponseResult<Any>
}

open class OrderRepositoryImp(private val service: ApiService, private val responseHandler: ResponseHandler) :
    BaseOrderRepository {
    override suspend fun getOrders(): ResponseResult<List<Order>> {
       return responseHandler.safeApiCall { service.getOrders() }
    }

    override suspend fun setStatusAccept(orderId: Long): ResponseResult<Any> {
        return responseHandler.safeApiCall { service.setStatusAccept(orderId) }
    }

    override suspend fun setStatusStart(orderId: Long): ResponseResult<Any> {
        return responseHandler.safeApiCall { service.setStatusStart(orderId) }
    }

    override suspend fun setStatusFinish(orderId: Long): ResponseResult<Any> {
        return responseHandler.safeApiCall { service.setStatusFinish(orderId) }
    }

    override suspend fun cancelOrder(
        orderId: Long,
        reasonId: Long?,
        notes: String?
    ): ResponseResult<Any> {
        return responseHandler.safeApiCall { service.cancelOrder(orderId, reasonId, notes) }
    }

}