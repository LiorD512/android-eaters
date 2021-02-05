package com.bupp.wood_spoon_eaters.network.test

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface Repository {

    suspend fun getCode(phone: String): ResultHandler<String>
    suspend fun validateCode(phone: String, code: String): ResultHandler<ServerResponse<Eater>>

    suspend fun getMe(): ResultHandler<ServerResponse<Eater>>
    suspend fun postMe(eater: EaterRequest): ResultHandler<ServerResponse<Eater>>

    suspend fun postNewAddress(addressRequest: AddressRequest): ResultHandler<ServerResponse<Address>>
    suspend fun deleteAddress(addressId: Long): ResultHandler<ServerResponse<Void>>
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

    override suspend fun postNewAddress(addressRequest: AddressRequest): ResultHandler<ServerResponse<Address>> {
        return safeApiCall { service.postNewAddress(addressRequest) }
    }
    override suspend fun deleteAddress(addressId: Long): ResultHandler<ServerResponse<Void>> {
        return safeApiCall { service.deleteAddress(addressId) }
    }
}