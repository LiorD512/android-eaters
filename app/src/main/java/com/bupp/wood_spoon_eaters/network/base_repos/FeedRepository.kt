package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.safeApiCall

interface FeedRepository{
    suspend fun getFeed(lat: Double?, lng: Double?, addressId: Long?, timestamp: String? = null): ResultHandler<ServerResponse<List<Feed>>>
}

class FeedRepositoryImpl(private val service: ApiService) : FeedRepository {
    override suspend fun getFeed(lat: Double?, lng: Double?, addressId: Long?, timestamp: String?): ResultHandler<ServerResponse<List<Feed>>> {
        return safeApiCall { service.getFeed(lat, lng, addressId, timestamp) }
    }


}