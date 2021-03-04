package com.bupp.wood_spoon_eaters.repositories

import android.util.Log
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.base_repos.EaterDataRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EaterDataRepository(
    private val apiService: EaterDataRepositoryImpl,
) {

    data class EaterDataRepoResult<T>(val type: EaterDataRepoStatus, val data: T? = null, val wsError: List<WSError>? = null)
    enum class EaterDataRepoStatus {
        GET_FAVORITES_SUCCESS,
        GET_FAVORITES_FAILED,

        GET_TRACEABLE_SUCCESS,
        GET_TRACEABLE_FAILED,

        GET_TRIGGERS_SUCCESS,
        GET_TRIGGERS_FAILED,

        CANCEL_ORDER_SUCCESS,
        CANCEL_ORDER_FAILED,

        SERVER_ERROR,
        SOMETHING_WENT_WRONG,
        WS_ERROR
    }

    suspend fun getTraceableOrders(): EaterDataRepoResult<List<Order>> {
        val result = withContext(Dispatchers.IO){
            apiService.getTraceableOrders()
        }
        result.let{
            return  when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"getActiveOrders - NetworkError")
                    EaterDataRepoResult(EaterDataRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"getActiveOrders - GenericError")
                    EaterDataRepoResult(EaterDataRepoStatus.GET_TRACEABLE_FAILED)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"getActiveOrders - Success")
                    EaterDataRepoResult(EaterDataRepoStatus.GET_TRACEABLE_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
                    EaterDataRepoResult(EaterDataRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    suspend fun getFavorites(feedRequest: FeedRequest): EaterDataRepoResult<List<Dish>> {
        val result = withContext(Dispatchers.IO){
            val feedRequest = feedRequest
            apiService.getFavorites(feedRequest)
        }
        result.let{
            return  when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"getFavorites - NetworkError")
                    EaterDataRepoResult(EaterDataRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"getFavorites - GenericError")
                    EaterDataRepoResult(EaterDataRepoStatus.GET_FAVORITES_FAILED)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"getFavorites - Success")
                    val searchObj = result.value.data
                    val favOrders = searchObj?.results
                    EaterDataRepoResult(EaterDataRepoStatus.GET_FAVORITES_SUCCESS, favOrders as List<Dish>)
                }
                is ResultHandler.WSCustomError -> {
                    EaterDataRepoResult(EaterDataRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }


    suspend fun getTriggers(): EaterDataRepoResult<Trigger> {
        val result = withContext(Dispatchers.IO){
            apiService.getTrigger()
        }
        result.let{
            return  when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"getFavorites - NetworkError")
                    EaterDataRepoResult(EaterDataRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"getFavorites - GenericError")
                    EaterDataRepoResult(EaterDataRepoStatus.GET_TRIGGERS_FAILED)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"getFavorites - Success")
                    EaterDataRepoResult(EaterDataRepoStatus.GET_TRIGGERS_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
                    EaterDataRepoResult(EaterDataRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    suspend fun cancelOrder(orderId: Long, note: String?): EaterDataRepoResult<Void> {
        val result = withContext(Dispatchers.IO){
            apiService.cancelOrder(orderId, note)
        }
        result.let{
            return  when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"cancelOrder - NetworkError")
                    EaterDataRepoResult(EaterDataRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"cancelOrder - GenericError")
                    EaterDataRepoResult(EaterDataRepoStatus.CANCEL_ORDER_FAILED)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"cancelOrder - Success")
                    EaterDataRepoResult(EaterDataRepoStatus.CANCEL_ORDER_SUCCESS, result.value.data)
                }
                is ResultHandler.WSCustomError -> {
                    EaterDataRepoResult(EaterDataRepoStatus.WS_ERROR, wsError = result.errors)
                }
            }
        }
    }

    companion object{
        const val TAG = "wowEaterDataRepo"
    }


}