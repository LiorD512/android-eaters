package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.ResultManager

interface BaseUpSaleRepository{

    suspend fun fetchUpSaleItems(orderId: Long): ResultHandler<ServerResponse<List<MenuItem>>>
}

class UpSaleRepositoryImpl(private val apiService: ApiService, private val resultManager: ResultManager): BaseUpSaleRepository {

    override suspend fun fetchUpSaleItems(orderId: Long): ResultHandler<ServerResponse<List<MenuItem>>> {
        return resultManager.safeApiCall { apiService.getUpsaleItemsByOrderId(orderId) }
    }

}