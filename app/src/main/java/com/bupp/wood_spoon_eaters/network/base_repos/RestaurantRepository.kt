package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.safeApiCall
import com.bupp.wood_spoon_eaters.repositories.RestaurantRepository.RestaurantResult

interface RestaurantRepositoryInterface{
    suspend fun getRestaurant(lat: Double?, lng: Double?, addressId: Long?, cookId: Long): ResultHandler<ServerResponse<Restaurant>>

}

class RestaurantRepositoryImpl(private val service: ApiService) : RestaurantRepositoryInterface {
    override suspend fun getRestaurant(lat: Double?, lng: Double?, addressId: Long?, restaurantId: Long): ResultHandler<ServerResponse<Restaurant>> {
//        val tempUrl = "https://woodspoon-server-pr-167.herokuapp.com/api/v2/cooks/1" // todo - remove this shit !
        return safeApiCall { service.getRestaurant(restaurantId, lat, lng, addressId) }
    }

}