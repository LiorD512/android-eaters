package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.Review
import com.bupp.wood_spoon_eaters.model.Restaurant
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.ResultManager

interface RestaurantRepositoryInterface{
    suspend fun getRestaurant(lat: Double?, lng: Double?, addressId: Long?, cookId: Long, query: String?): ResultHandler<ServerResponse<Restaurant>>
    suspend fun getCookReview(cookId: Long): ResultHandler<ServerResponse<Review>>
    suspend fun likeCook(cookId: Long): ResultHandler<ServerResponse<Any>>
    suspend fun unlikeCook(cookId: Long): ResultHandler<ServerResponse<Any>>
}

class RestaurantRepositoryImpl(private val service: ApiService, private val resultManager: ResultManager) : RestaurantRepositoryInterface {
    override suspend fun getRestaurant(lat: Double?, lng: Double?, addressId: Long?, cookId: Long, query: String?): ResultHandler<ServerResponse<Restaurant>> {
        return resultManager.safeApiCall { service.getRestaurant(cookId, lat, lng, addressId, query) }
    }

    override suspend fun getCookReview(cookId: Long): ResultHandler<ServerResponse<Review>> {
        return resultManager.safeApiCall { service.getCookReview(cookId = cookId) }
    }

    override suspend fun likeCook(cookId: Long): ResultHandler<ServerResponse<Any>> {
        return resultManager.safeApiCall { service.likeCook(cookId) }
    }

    override suspend fun unlikeCook(cookId: Long): ResultHandler<ServerResponse<Any>> {
        return resultManager.safeApiCall { service.unlikeCook(cookId) }
    }

}