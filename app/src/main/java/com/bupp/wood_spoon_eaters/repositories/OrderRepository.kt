package com.bupp.wood_spoon_eaters.repositories

import android.util.Log
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.base_repos.OrderRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepository(val apiService: OrderRepositoryImpl, val eaterDataManager: EaterDataManager) {


    data class OrderRepoResult<T>(val type: OrderRepoStatus, val data: T? = null, val wsError: List<WSError>? = null)
    enum class OrderRepoStatus {
        FULL_DISH_SUCCESS,
        FULL_DISH_FAILED,
        POST_ORDER_SUCCESS,
        UPDATE_ORDER_FAILED,
        UPDATE_ORDER_SUCCESS,
        FINALIZE_ORDER_FAILED,
        FINALIZE_ORDER_SUCCESS,
        GET_SHIPPING_METHOD_FAILED,
        GET_SHIPPING_METHOD_SUCCESS,
        ACTIVE_ORDERS_FAILED,
        ACTIVE_ORDERS_SUCCESS,
        POST_ORDER_FAILED,
        SERVER_ERROR,
        SOMETHING_WENT_WRONG,
        WS_ERROR
    }

    suspend fun getFullDish(menuItemId: Long, feedRequest: FeedRequest): OrderRepoResult<FullDish> {
        val result = withContext(Dispatchers.IO) {
            apiService.getFullDish(menuItemId, feedRequest)
        }
        result.let{
            return  when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"getFullDish - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"getFullDish - GenericError")
                    OrderRepoResult(OrderRepoStatus.FULL_DISH_FAILED)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"getFullDish - Success")
                    OrderRepoResult(OrderRepoStatus.FULL_DISH_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
                    Log.d(TAG,"getFullDish - WSError")
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

//    suspend fun getDishReview(dishId: Long): Review? {
//        return withContext(Dispatchers.IO) {
//            apiService.getDishReview(dishId).data
//        }
//    }
    
    suspend fun postNewOrder(orderRequest: OrderRequest): OrderRepoResult<Order>{
        val result = withContext(Dispatchers.IO){
            apiService.postOrder(orderRequest)
        }
        result.let{
           return  when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"postNewOrder - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"postNewOrder - GenericError")
                    OrderRepoResult(OrderRepoStatus.POST_ORDER_FAILED)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"postNewOrder - Success")
                    OrderRepoResult(OrderRepoStatus.POST_ORDER_SUCCESS, result.value.data)
                }
               is ResultHandler.WSCustomError -> {
//                   Log.d(TAG,"postNewOrder - wsError ${result.errors?.get(0)?.msg}")
                   OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
               }
           }
        }
    }

    suspend fun updateOrder(orderId: Long, orderRequest: OrderRequest): OrderRepoResult<Order> {
        val result = withContext(Dispatchers.IO){
            apiService.updateOrder(orderId, orderRequest)
        }
        result.let{
            return  when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"updateOrder - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"updateOrder - GenericError")
                    OrderRepoResult(OrderRepoStatus.UPDATE_ORDER_FAILED)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"updateOrder - Success")
                    OrderRepoResult(OrderRepoStatus.UPDATE_ORDER_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
//                    Log.d(TAG,"updateOrder - wsError ${result.errors?.get(0)?.msg}")
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    suspend fun finalizeOrder(orderId: Long, paymentMethodId: String?): OrderRepoResult<Any> {
        val result = withContext(Dispatchers.IO){
            apiService.checkoutOrder(orderId, paymentMethodId)
        }
        result.let{
            return  when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"updateOrder - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"updateOrder - GenericError")
                    OrderRepoResult(OrderRepoStatus.FINALIZE_ORDER_FAILED)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"updateOrder - Success")
                    OrderRepoResult(OrderRepoStatus.FINALIZE_ORDER_SUCCESS)
                }
                is ResultHandler.WSCustomError -> {
//                    Log.d(TAG,"updateOrder - wsError ${result.errors?.get(0)?.msg}")
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    suspend fun getUpsShippingRates(orderId: Long): OrderRepoResult<List<ShippingMethod>> {
        val result = withContext(Dispatchers.IO){
            apiService.getUpsShippingRates(orderId)
        }
        result.let{
            return  when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"updateOrder - NetworkError")
                    OrderRepoResult(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"updateOrder - GenericError")
                    OrderRepoResult(OrderRepoStatus.GET_SHIPPING_METHOD_FAILED)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"updateOrder - Success")
                    OrderRepoResult(OrderRepoStatus.GET_SHIPPING_METHOD_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
//                    Log.d(TAG,"updateOrder - wsError ${result.errors?.get(0)?.msg}")
                    OrderRepoResult(OrderRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }


    companion object{
        const val TAG = "wowOrderRepo"
    }

}