package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.safeApiCall

interface EaterDataRepository{
    suspend fun getTraceableOrders(): ResultHandler<ServerResponse<List<Order>>>
}

class EaterDataRepositoryImpl(private val service: ApiService) : EaterDataRepository {
    override suspend fun getTraceableOrders(): ResultHandler<ServerResponse<List<Order>>> {
        return safeApiCall { service.getTraceableOrders() }
    }


}