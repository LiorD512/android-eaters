package com.bupp.wood_spoon_eaters.network.test

import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.EaterRequest
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface Repository {

    suspend fun getCode(phone: String): ResultHandler<String>
    suspend fun validateCode(phone: String, code: String): ResultHandler<ServerResponse<Eater>>

    suspend fun getMe(): ResultHandler<ServerResponse<Eater>>
    suspend fun postMe(eater: EaterRequest): ResultHandler<ServerResponse<Eater>>
}

class RepositoryImpl(private val service: ApiService) : Repository {

    override suspend fun getCode(phone: String): ResultHandler<String> {
        return safeApiCall { service.getCode(phone).toString() }
    }

    override suspend fun validateCode(phone: String, code: String): ResultHandler<ServerResponse<Eater>> {
        return safeApiCall { service.validateCode(phone, code) }
    }

    override suspend fun getMe(): ResultHandler<ServerResponse<Eater>> {
        return safeApiCall { service.getMe() }
    }

    override suspend fun postMe(eater: EaterRequest): ResultHandler<ServerResponse<Eater>> {
        return safeApiCall { service.postMe(eater) }
    }
}