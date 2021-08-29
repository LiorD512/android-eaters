package com.bupp.wood_spoon_eaters.repositories

import android.util.Log
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.base_repos.RestaurantRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class RestaurantRepository(private val apiService: RestaurantRepositoryImpl) {

    data class RestaurantResult(val type: RestaurantRepoStatus, val restaurant: Restaurant? = null)
    data class ReviewResult(val type: RestaurantRepoStatus, val review: Review? = null)
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

    suspend fun getCookReview(cookId: Long): ReviewResult {
        val result = withContext(Dispatchers.IO) {
            apiService.getCookReview(cookId)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(FeedRepository.TAG, "getCookReview - NetworkError")
                    ReviewResult(RestaurantRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(FeedRepository.TAG, "getCookReview - GenericError")
                    ReviewResult(RestaurantRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(FeedRepository.TAG, "getCookReview - Success")
                    ReviewResult(RestaurantRepoStatus.SUCCESS, result.value.data)
                }
                else -> {
                    Log.d(FeedRepository.TAG, "getCookReview - wsError")
                    ReviewResult(RestaurantRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }

    suspend fun likeCook(cookId: Long): RestaurantResult {
        val result = withContext(Dispatchers.IO) {
            apiService.likeCook(cookId)
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
                    RestaurantResult(RestaurantRepoStatus.SUCCESS)
                }
                else -> {
                    Log.d(TAG, "getRestaurant - wsError")
                    RestaurantResult(RestaurantRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }

    suspend fun unlikeCook(cookId: Long): RestaurantResult {
        val result = withContext(Dispatchers.IO) {
            apiService.unlikeCook(cookId)
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
                    RestaurantResult(RestaurantRepoStatus.SUCCESS)
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