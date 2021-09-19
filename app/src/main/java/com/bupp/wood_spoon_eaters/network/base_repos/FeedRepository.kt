package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.safeApiCall

interface FeedRepositoryInterface{
    suspend fun getFeed(lat: Double?, lng: Double?, addressId: Long?, timestamp: String? = null): ResultHandler<ServerResponse<FeedResult>>
    suspend fun getHrefCollection(href: String): ResultHandler<ServerResponse<List<FeedSectionCollectionItem>>>
    suspend fun getCookById(cookId: Long, addressId: Long?, lat: Double?, lng: Double?): ResultHandler<ServerResponse<Cook>>
    suspend fun getCookReview(cookId: Long): ResultHandler<ServerResponse<Review>>
    suspend fun search(searchRequest: SearchRequest): ResultHandler<ServerResponse<List<Search>>>

}

class FeedRepositoryImpl(private val service: ApiService) : FeedRepositoryInterface {
    override suspend fun getFeed(lat: Double?, lng: Double?, addressId: Long?, timestamp: String?): ResultHandler<ServerResponse<FeedResult>> {
        return safeApiCall { service.getFeed(lat, lng, addressId, timestamp) }
    }

    override suspend fun getHrefCollection(href: String): ResultHandler<ServerResponse<List<FeedSectionCollectionItem>>> {
        return safeApiCall { service.getHrefCollection(href) }
    }

    override suspend fun getCookById(cookId: Long, addressId: Long?, lat: Double?, lng: Double?): ResultHandler<ServerResponse<Cook>> {
        return safeApiCall { service.getCook(cookId = cookId, addressId = addressId, lat = lat, lng = lng) }
    }

    override suspend fun getCookReview(cookId: Long): ResultHandler<ServerResponse<Review>> {
        return safeApiCall { service.getCookReview(cookId = cookId) }
    }

    override suspend fun search(searchRequest: SearchRequest): ResultHandler<ServerResponse<List<Search>>> {
        return safeApiCall { service.search(searchRequest) }
    }

}