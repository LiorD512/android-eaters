package com.bupp.wood_spoon_eaters.repositories

import android.util.Log
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.base_repos.RestaurantRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class RestaurantRepository(private val apiService: RestaurantRepositoryImpl) {

    data class RestaurantResult(val type: RestaurantRepoStatus, val restaurant: Restaurant? = null)
    enum class RestaurantRepoStatus {
        EMPTY,
        SUCCESS,
        SERVER_ERROR,
        SOMETHING_WENT_WRONG,
    }

    suspend fun getRestaurant(restaurantId: Long): RestaurantResult {
        val result = withContext(Dispatchers.IO) {//todo remove hard coded text !
            apiService.getRestaurant(null, null, 1996, restaurantId = restaurantId)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "getRestaurant - NetworkError")
                    RestaurantResult(RestaurantRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "getRestaurant - GenericError")
                    RestaurantResult(RestaurantRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "getRestaurant - Success")
                    RestaurantResult(RestaurantRepoStatus.SUCCESS, result.value.data)
                }
                else -> {
                    Log.d(TAG, "getRestaurant - wsError")
                    RestaurantResult(RestaurantRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }

    companion object {
        const val TAG = "wowFeedRepo"
    }

}