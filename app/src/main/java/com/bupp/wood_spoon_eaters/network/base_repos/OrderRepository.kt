package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.safeApiCall

interface OrderRepositoryInterface{
    suspend fun getFullDish(menuItemId: Long, feedRequest: FeedRequest): ResultHandler<ServerResponse<FullDish>>
    suspend fun getFullDishNew(menuItemId: Long): ResultHandler<ServerResponse<FullDish>>
    suspend fun postOrder(orderRequest: OrderRequest): ResultHandler<ServerResponse<Order>>
    suspend fun updateOrder(orderId: Long, orderRequest: OrderRequest): ResultHandler<ServerResponse<Order>>
    suspend fun checkoutOrder(orderId: Long, paymentMethodId: String?): ResultHandler<ServerResponse<Any>>
    suspend fun getTraceableOrders(): ResultHandler<ServerResponse<List<Order>>>
    suspend fun getOrderDeliveryTimes(orderId: Long): ResultHandler<ServerResponse<List<DeliveryDates>>>
    suspend fun getUpsShippingRates(orderId: Long): ResultHandler<ServerResponse<List<ShippingMethod>>>
    suspend fun getOrderById(orderId: Long): ResultHandler<ServerResponse<Order>>
    suspend fun getAllOrders(): ResultHandler<ServerResponse<List<Order>>>
    suspend fun postReport(orderId: Long, report: Reports): ResultHandler<ServerResponse<Any>>
    suspend fun postReview(orderId: Long, reviewRequest: ReviewRequest): ResultHandler<ServerResponse<Any>>
}

class OrderRepositoryImpl(private val service: ApiService) : OrderRepositoryInterface {
    override suspend fun getFullDish(menuItemId: Long, feedRequest: FeedRequest): ResultHandler<ServerResponse<FullDish>> {
        return safeApiCall { service.getSingleDish(
            menuItemId = menuItemId,
            lat = feedRequest.lat,
            lng = feedRequest.lng,
            addressId = feedRequest.addressId,
            timestamp = feedRequest.timestamp
        ) }
    }
    override suspend fun getFullDishNew(menuItemId: Long): ResultHandler<ServerResponse<FullDish>> {
        return safeApiCall { service.getSingleDish(
            menuItemId = menuItemId,
        ) }
    }
    override suspend fun postOrder(orderRequest: OrderRequest): ResultHandler<ServerResponse<Order>> {
        return safeApiCall { service.postOrder(orderRequest) }
    }

    override suspend fun updateOrder(orderId: Long, orderRequest: OrderRequest): ResultHandler<ServerResponse<Order>> {
        return safeApiCall { service.updateOrder(orderId, orderRequest) }
    }

    override suspend fun checkoutOrder(orderId: Long, paymentMethodId: String?): ResultHandler<ServerResponse<Any>> {
        return safeApiCall { service.checkoutOrder(orderId, paymentMethodId) }
    }

    override suspend fun getTraceableOrders(): ResultHandler<ServerResponse<List<Order>>> {
        return safeApiCall { service.getTraceableOrders() }
    }

    override suspend fun getOrderDeliveryTimes(orderId: Long): ResultHandler<ServerResponse<List<DeliveryDates>>>{
        return safeApiCall { service.getOrderDeliveryTimes(orderId) }
    }

    override suspend fun getUpsShippingRates(orderId: Long): ResultHandler<ServerResponse<List<ShippingMethod>>>{
        return safeApiCall { service.getUpsShippingRates(orderId) }
    }

    override suspend fun getOrderById(orderId: Long): ResultHandler<ServerResponse<Order>>{
        return safeApiCall { service.getOrderById(orderId) }
    }

    override suspend fun getAllOrders(): ResultHandler<ServerResponse<List<Order>>>{
        return safeApiCall { service.getOrders() }
    }

    override suspend fun postReport(orderId: Long, report: Reports): ResultHandler<ServerResponse<Any>>{
        return safeApiCall { service.postReport(orderId, report) }
    }

    override suspend fun postReview(orderId: Long, reviewRequest: ReviewRequest): ResultHandler<ServerResponse<Any>>{
        return safeApiCall { service.postReview(orderId, reviewRequest) }
    }

}