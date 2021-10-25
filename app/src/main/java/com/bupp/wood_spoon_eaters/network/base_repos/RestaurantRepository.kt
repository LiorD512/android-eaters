package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.model.Restaurant
import com.bupp.wood_spoon_eaters.model.Review
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.ResultManager

interface RestaurantRepositoryInterface{
    suspend fun getRestaurant(lat: Double?, lng: Double?, addressId: Long?, cookId: Long): ResultHandler<ServerResponse<Restaurant>>
    suspend fun getCookReview(cookId: Long): ResultHandler<ServerResponse<Review>>
    suspend fun likeCook(cookId: Long): ResultHandler<ServerResponse<Any>>
    suspend fun unlikeCook(cookId: Long): ResultHandler<ServerResponse<Any>>
}

class RestaurantRepositoryImpl(private val service: ApiService, private val resultManager: ResultManager) : RestaurantRepositoryInterface {
    override suspend fun getRestaurant(lat: Double?, lng: Double?, addressId: Long?, restaurantId: Long): ResultHandler<ServerResponse<Restaurant>> {
//        val tempUrl = "https://woodspoon-server-pr-167.herokuapp.com/api/v2/cooks/1" // todo - remove this shit !
        return resultManager.safeApiCall { service.getRestaurant(restaurantId, lat, lng, addressId) }
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