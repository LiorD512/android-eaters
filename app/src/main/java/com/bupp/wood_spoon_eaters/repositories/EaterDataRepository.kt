package com.bupp.wood_spoon_eaters.repositories

import android.util.Log
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
        GET_TRACEABLE_SUCCESS,
        GET_TRACEABLE_FAILED,

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

    companion object{
        const val TAG = "wowEaterDataRepo"
    }


}