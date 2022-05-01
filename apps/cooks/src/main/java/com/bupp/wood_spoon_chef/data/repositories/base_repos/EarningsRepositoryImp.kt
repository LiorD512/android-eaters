package com.bupp.wood_spoon_chef.data.repositories.base_repos

import com.bupp.wood_spoon_chef.data.remote.model.Earnings
import com.bupp.wood_spoon_chef.data.remote.network.ApiService
import com.bupp.wood_spoon_chef.data.remote.network.ResponseHandler
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult

interface BaseEarningsRepository {
    suspend fun getStats(): ResponseResult<Earnings>
    suspend fun getBalance(): ResponseResult<Any>
    suspend fun getLifetime(): ResponseResult<Any>
}

open class EarningsRepositoryImp(private val service: ApiService, private val responseHandler: ResponseHandler) :
    BaseEarningsRepository {

    override suspend fun getStats(): ResponseResult<Earnings> {
        return responseHandler.safeApiCall { service.getStats() }
    }

    override suspend fun getBalance(): ResponseResult<Any> {
        return responseHandler.safeApiCall { service.getBalance() }
    }

    override suspend fun getLifetime(): ResponseResult<Any> {
        return responseHandler.safeApiCall { service.getLifetime() }
    }

}