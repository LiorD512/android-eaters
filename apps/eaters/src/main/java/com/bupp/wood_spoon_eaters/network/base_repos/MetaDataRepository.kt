package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.ResultManager

interface MetaDataRepositoryInterface{
    suspend fun getMetaData(): ResultHandler<ServerResponse<MetaDataModel>>

}

class MetaDataRepositoryImpl(private val service: ApiService, private val resultManager: ResultManager) : MetaDataRepositoryInterface {

    override suspend fun getMetaData(): ResultHandler<ServerResponse<MetaDataModel>> {
        return resultManager.safeApiCall { service.getMetaData() }
    }


}