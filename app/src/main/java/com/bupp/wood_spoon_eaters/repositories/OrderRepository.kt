package com.bupp.wood_spoon_eaters.repositories

import android.util.Log
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.base_repos.OrderRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepository(val apiService: OrderRepositoryImpl, val eaterDataManager: EaterDataManager) {


    data class OrderRepoResult<T>(val type: OrderRepoStatus, val data: T? = null)
    enum class OrderRepoStatus {
        FULL_DISH_SUCCESS,
        FULL_DISH_FAILED,
        POST_ORDER_SUCCESS,
        POST_ORDER_FAILED,
        SERVER_ERROR,
        SOMETHING_WENT_WRONG,
    }

    suspend fun getFullDish(menuItemId: Long, feedRequest: FeedRequest): OrderRepoResult<FullDish> {
        val result = withContext(Dispatchers.IO) {
            apiService.getFullDish(menuItemId, feedRequest)
        }
        result.let{
            return  when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"postNewOrder - NetworkError")
                    OrderRepoResult<FullDish>(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"postNewOrder - GenericError")
                    OrderRepoResult<FullDish>(OrderRepoStatus.FULL_DISH_FAILED)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"postNewOrder - Success")
                    OrderRepoResult<FullDish>(OrderRepoStatus.FULL_DISH_SUCCESS, result.value.data)
                }
                else -> {
                    Log.d(TAG,"postNewOrder - WSError")
                    OrderRepoResult<FullDish>(OrderRepoStatus.FULL_DISH_FAILED)
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
                    OrderRepoResult<Order>(OrderRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"postNewOrder - GenericError")
                    OrderRepoResult<Order>(OrderRepoStatus.POST_ORDER_FAILED)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"postNewOrder - Success")
                    OrderRepoResult<Order>(OrderRepoStatus.POST_ORDER_SUCCESS, result.value.data)
                }
               is ResultHandler.WSCustomError -> {
                   Log.d(TAG,"postNewOrder - wsError ${result.errors?.get(0)?.msg}")
                   OrderRepoResult<Order>(OrderRepoStatus.POST_ORDER_FAILED)
               }
           }
        }
    }
    
    companion object{
        const val TAG = "wowOrderRepo"
    }

}