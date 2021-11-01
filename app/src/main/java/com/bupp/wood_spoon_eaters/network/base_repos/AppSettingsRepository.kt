package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.ResultManager

interface AppSettingsRepositoryInterface{
    suspend fun getAppSetting(): ResultHandler<ServerResponse<List<AppSetting>>>

}

class AppSettingsRepositoryImpl(private val service: ApiService, private val resultManager: ResultManager) : AppSettingsRepositoryInterface {

    override suspend fun getAppSetting(): ResultHandler<ServerResponse<List<AppSetting>>> {
        return resultManager.safeApiCall { service.getAppSettings() }
    }


}