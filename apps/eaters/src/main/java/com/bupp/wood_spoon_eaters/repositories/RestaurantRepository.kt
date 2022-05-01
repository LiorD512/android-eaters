package com.bupp.wood_spoon_eaters.repositories

import android.util.Log
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.Review
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

    suspend fun getRestaurant(restaurantId: Long, lastFeedRequest: FeedRequest): RestaurantResult {
        val result = withContext(Dispatchers.IO) {
            apiService.getRestaurant(lastFeedRequest.lat, lastFeedRequest.lng, lastFeedRequest.addressId,
                cookId = restaurantId, lastFeedRequest.q)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    RestaurantResult(RestaurantRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    RestaurantResult(RestaurantRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    RestaurantResult(RestaurantRepoStatus.SUCCESS, result.value.data)
                }
                else -> {
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
                    ReviewResult(RestaurantRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    ReviewResult(RestaurantRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    ReviewResult(RestaurantRepoStatus.SUCCESS, result.value.data)
                }
                else -> {
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
                    RestaurantResult(RestaurantRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    RestaurantResult(RestaurantRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    RestaurantResult(RestaurantRepoStatus.SUCCESS)
                }
                else -> {
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
                    RestaurantResult(RestaurantRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    RestaurantResult(RestaurantRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    RestaurantResult(RestaurantRepoStatus.SUCCESS)
                }
                else -> {
                    RestaurantResult(RestaurantRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }
}