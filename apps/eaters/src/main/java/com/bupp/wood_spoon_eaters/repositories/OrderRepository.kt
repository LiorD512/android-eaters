package com.bupp.wood_spoon_eaters.repositories

import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.ReviewRequest
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.base_repos.OrderRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepository(val apiService: OrderRepositoryImpl, val eaterDataManager: EaterDataManager) {

    data class OrderRepoResult<T>(
        val type: OrderRepoStatus,
        val data: T? = null,
        val wsError: List<WSError>? = null
    )

    enum class OrderRepoStatus {
        FULL_DISH_SUCCESS,
        FULL_DISH_FAILED,
        ADD_NEW_DISH_SUCCESS,
        ADD_NEW_DISH_FAILED,
        POST_ORDER_SUCCESS,
        UPDATE_ORDER_FAILED,
        UPDATE_ORDER_SUCCESS,
        GET_ORDER_BY_ID_FAILED,
        GET_ORDER_BY_ID_SUCCESS,
        FINALIZE_ORDER_FAILED,
        FINALIZE_ORDER_SUCCESS,
        GET_DELIVERY_DATES_FAILED,
        GET_DELIVERY_DATES_SUCCESS,
        GET_SHIPPING_METHOD_FAILED,
        GET_SHIPPING_METHOD_SUCCESS,
        REPORT_ISSUE_FAILED,
        REPORT_ISSUE_SUCCESS,
        POST_REVIEW_FAILED,
        POST_REVIEW_SUCCESS,
        POST_ORDER_FAILED,
        GET_All_ORDERS_FAILED,
        GET_All_ORDERS_SUCCESS,
        SERVER_ERROR,
        SOMETHING_WENT_WRONG,
        WS_ERROR
    }

    suspend fun getFullDishByMenuItem(menuItemId: Long): OrderRepoResult<FullDish> {
        val result = withContext(Dispatchers.IO) {
            apiService.getFullDishByMenuItem(menuItemId)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.c(TAG, "getFullDish - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    MTLogger.c(TAG, "getFullDish - GenericError")
                    OrderRepoResult(OrderRepoStatus.FULL_DISH_FAILED)
                }
                is ResultHandler.Success -> {
                    MTLogger.c(TAG, "getFullDish - Success")
                    OrderRepoResult(OrderRepoStatus.FULL_DISH_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
                    MTLogger.c(TAG, "getFullDish - WSError")
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    suspend fun getFullDishByDish(menuItemId: Long): OrderRepoResult<FullDish> {
        val result = withContext(Dispatchers.IO) {
            apiService.getFullDishByDish(menuItemId)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.c(TAG, "getFullDish - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    MTLogger.c(TAG, "getFullDish - GenericError")
                    OrderRepoResult(OrderRepoStatus.FULL_DISH_FAILED)
                }
                is ResultHandler.Success -> {
                    MTLogger.c(TAG, "getFullDish - Success")
                    OrderRepoResult(OrderRepoStatus.FULL_DISH_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
                    MTLogger.c(TAG, "getFullDish - WSError")
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }


    suspend fun addNewDish(orderRequest: OrderRequest): OrderRepoResult<Order> {
        val result = withContext(Dispatchers.IO) {
            apiService.postOrder(orderRequest)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.c(TAG, "addNewDish - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    MTLogger.c(TAG, "addNewDish - GenericError")
                    OrderRepoResult(OrderRepoStatus.ADD_NEW_DISH_FAILED)
                }
                is ResultHandler.Success -> {
                    MTLogger.c(TAG, "addNewDish - Success")
                    OrderRepoResult(OrderRepoStatus.ADD_NEW_DISH_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
                    MTLogger.c(TAG, "addNewDish - wsError")
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    @Deprecated("old function - changed with addNewDish")
    suspend fun postNewOrder(orderRequest: OrderRequest): OrderRepoResult<Order> {
        val result = withContext(Dispatchers.IO) {
            apiService.postOrder(orderRequest)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.c(TAG, "postNewOrder - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    MTLogger.c(TAG, "postNewOrder - GenericError")
                    OrderRepoResult(OrderRepoStatus.POST_ORDER_FAILED)
                }
                is ResultHandler.Success -> {
                    MTLogger.c(TAG, "postNewOrder - Success")
                    OrderRepoResult(OrderRepoStatus.POST_ORDER_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
                    MTLogger.c(TAG, "postNewOrder - wsError")
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    suspend fun updateOrderGift(orderId: Long, orderGiftRequest: OrderGiftRequest) =
        updateOrder(orderId) {
            apiService.updateOrderGift(orderId, orderGiftRequest)
        }

    suspend fun updateOrder(orderId: Long, orderRequest: OrderRequest) =
        updateOrder(orderId) {
            apiService.updateOrder(orderId, orderRequest)
        }

    private suspend fun updateOrder(
        orderId: Long,
        apiCall: suspend (() -> ResultHandler<ServerResponse<Order>>)
    ): OrderRepoResult<Order> {
        val result = withContext(Dispatchers.IO) {
            apiCall.invoke()
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.c(TAG, "updateOrder - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    MTLogger.c(TAG, "updateOrder - GenericError")
                    OrderRepoResult(OrderRepoStatus.UPDATE_ORDER_FAILED)
                }
                is ResultHandler.Success -> {
                    MTLogger.c(TAG, "updateOrder - Success")
                    OrderRepoResult(OrderRepoStatus.UPDATE_ORDER_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    suspend fun finalizeOrder(orderId: Long, paymentMethodId: String?): OrderRepoResult<Any> {
        val result = withContext(Dispatchers.IO) {
            apiService.checkoutOrder(orderId, paymentMethodId)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.c(TAG, "finalizeOrder - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    MTLogger.c(TAG, "finalizeOrder - GenericError")
                    OrderRepoResult(OrderRepoStatus.FINALIZE_ORDER_FAILED)
                }
                is ResultHandler.Success -> {
                    MTLogger.c(TAG, "finalizeOrder - Success")
                    OrderRepoResult(OrderRepoStatus.FINALIZE_ORDER_SUCCESS)
                }
                is ResultHandler.WSCustomError -> {
                    MTLogger.c(TAG, "finalizeOrder - wsError")
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    suspend fun getOrderDeliveryTimes(orderId: Long): OrderRepoResult<List<DeliveryDates>> {
        val result = withContext(Dispatchers.IO) {
            apiService.getOrderDeliveryTimes(orderId)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.c(TAG, "getOrderDeliveryTimes - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    MTLogger.c(TAG, "getOrderDeliveryTimes - GenericError")
                    OrderRepoResult(OrderRepoStatus.GET_DELIVERY_DATES_FAILED)
                }
                is ResultHandler.Success -> {
                    MTLogger.c(TAG, "getOrderDeliveryTimes - Success")
                    OrderRepoResult(OrderRepoStatus.GET_DELIVERY_DATES_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
                    MTLogger.c(TAG, "getOrderDeliveryTimess - wsError")
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    suspend fun getUpsShippingRates(orderId: Long): OrderRepoResult<List<ShippingMethod>> {
        val result = withContext(Dispatchers.IO) {
            apiService.getUpsShippingRates(orderId)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.c(TAG, "getUpsShippingRates - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    MTLogger.c(TAG, "getUpsShippingRates - GenericError")
                    OrderRepoResult(OrderRepoStatus.GET_SHIPPING_METHOD_FAILED)
                }
                is ResultHandler.Success -> {
                    MTLogger.c(TAG, "getUpsShippingRates - Success")
                    OrderRepoResult(OrderRepoStatus.GET_SHIPPING_METHOD_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
                    MTLogger.c(TAG, "getUpsShippingRates - wsError")
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    suspend fun getAllOrders(): OrderRepoResult<List<Order>> {
        val result = withContext(Dispatchers.IO) {
            apiService.getAllOrders()
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.c(TAG, "getAllOrders - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    MTLogger.c(TAG, "getAllOrders - GenericError")
                    OrderRepoResult(OrderRepoStatus.GET_All_ORDERS_FAILED)
                }
                is ResultHandler.Success -> {
                    MTLogger.c(TAG, "getAllOrders - Success")
                    OrderRepoResult(OrderRepoStatus.GET_All_ORDERS_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    suspend fun getOrderById(orderId: Long): OrderRepoResult<Order> {
        val result = withContext(Dispatchers.IO) {
            apiService.getOrderById(orderId)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.c(TAG, "getOrderById - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    MTLogger.c(TAG, "getOrderById - GenericError")
                    OrderRepoResult(OrderRepoStatus.GET_ORDER_BY_ID_FAILED)
                }
                is ResultHandler.Success -> {
                    MTLogger.c(TAG, "getOrderById - Success")
                    OrderRepoResult(OrderRepoStatus.GET_ORDER_BY_ID_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    suspend fun postReportIssue(orderId: Long, reports: Reports): OrderRepoResult<Order> {
        val result = withContext(Dispatchers.IO) {
            apiService.postReport(orderId, reports)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.c(TAG, "postReportIssue - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    MTLogger.c(TAG, "postReportIssue - GenericError")
                    OrderRepoResult(OrderRepoStatus.REPORT_ISSUE_FAILED)
                }
                is ResultHandler.Success -> {
                    MTLogger.c(TAG, "postReportIssue - Success")
                    OrderRepoResult(OrderRepoStatus.REPORT_ISSUE_SUCCESS)
                }
                is ResultHandler.WSCustomError -> {
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    suspend fun postReview(orderId: Long, reviewRequest: ReviewRequest): OrderRepoResult<Order> {
        val result = withContext(Dispatchers.IO) {
            apiService.postReview(orderId, reviewRequest)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.c(TAG, "postReview - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    MTLogger.c(TAG, "postReview - GenericError")
                    OrderRepoResult(OrderRepoStatus.POST_REVIEW_FAILED)
                }
                is ResultHandler.Success -> {
                    MTLogger.c(TAG, "postReview - Success")
                    OrderRepoResult(OrderRepoStatus.POST_REVIEW_SUCCESS)
                }
                is ResultHandler.WSCustomError -> {
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    suspend fun ignoreReview(orderId: Long): OrderRepoResult<Order> {
        val result = withContext(Dispatchers.IO) {
            apiService.ignoreReview(orderId)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    MTLogger.c(TAG, "postReview - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    MTLogger.c(TAG, "postReview - GenericError")
                    OrderRepoResult(OrderRepoStatus.POST_REVIEW_FAILED)
                }
                is ResultHandler.Success -> {
                    MTLogger.c(TAG, "postReview - Success")
                    OrderRepoResult(OrderRepoStatus.POST_REVIEW_SUCCESS)
                }
                is ResultHandler.WSCustomError -> {
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    companion object {
        const val TAG = "wowOrderRepo"
    }

}