package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.safeApiCall

interface OrderRepository{
    suspend fun getFullDish(menuItemId: Long, feedRequest: FeedRequest): ResultHandler<ServerResponse<FullDish>>
    suspend fun postOrder(orderRequest: OrderRequest): ResultHandler<ServerResponse<Order>>
    suspend fun updateOrder(orderId: Long, orderRequest: OrderRequest): ResultHandler<ServerResponse<Order>>
    suspend fun checkoutOrder(orderId: Long, paymentMethodId: String?): ResultHandler<ServerResponse<Void>>
    suspend fun getTraceableOrders(): ResultHandler<ServerResponse<List<Order>>>
}

class OrderRepositoryImpl(private val service: ApiService) : OrderRepository {
    override suspend fun getFullDish(menuItemId: Long, feedRequest: FeedRequest): ResultHandler<ServerResponse<FullDish>> {
        return safeApiCall { service.getSingleDish(
            menuItemId = menuItemId,
            lat = feedRequest.lat,
            lng = feedRequest.lng,
            addressId = feedRequest.addressId,
            timestamp = feedRequest.timestamp
        ) }
    }
    override suspend fun postOrder(orderRequest: OrderRequest): ResultHandler<ServerResponse<Order>> {
        return safeApiCall { service.postOrder(orderRequest) }
    }

    override suspend fun updateOrder(orderId: Long, orderRequest: OrderRequest): ResultHandler<ServerResponse<Order>> {
        return safeApiCall { service.updateOrder(orderId, orderRequest) }
    }

    override suspend fun checkoutOrder(orderId: Long, paymentMethodId: String?): ResultHandler<ServerResponse<Void>> {
        return safeApiCall { service.checkoutOrder(orderId, paymentMethodId) }
    }

    override suspend fun getTraceableOrders(): ResultHandler<ServerResponse<List<Order>>> {
        return safeApiCall { service.getTraceableOrders() }
    }


}