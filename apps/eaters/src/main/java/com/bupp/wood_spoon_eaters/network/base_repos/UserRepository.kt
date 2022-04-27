package com.bupp.wood_spoon_eaters.network.base_repos

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.ResultManager
import retrofit2.Response


interface UserRepositoryInterface {

    suspend fun getCode(phone: String): ResultHandler<String>
    suspend fun validateCode(phone: String, code: String): ResultHandler<ServerResponse<Eater>>

    suspend fun getMe(): ResultHandler<ServerResponse<Eater>>
    suspend fun postMe(eater: EaterRequest): ResultHandler<ServerResponse<Eater>>
    suspend fun updateNotificationGroup(notifications: List<Long>?): ResultHandler<ServerResponse<Eater>>
    suspend fun deleteMe(): ResultHandler<Response<Unit>>

    suspend fun postNewAddress(addressRequest: AddressRequest): ResultHandler<ServerResponse<Address>>
    suspend fun deleteAddress(addressId: Long): ResultHandler<ServerResponse<Any>>

}

class UserRepositoryImpl(private val service: ApiService, private val resultManager: ResultManager) : UserRepositoryInterface {

    override suspend fun getCode(phone: String): ResultHandler<String> {
        return resultManager.safeApiCall { service.getCode(phone).toString() }
    }

    override suspend fun validateCode(phone: String, code: String): ResultHandler<ServerResponse<Eater>> {
        return resultManager.safeApiCall { service.validateCode(phone, code) }
    }

    override suspend fun getMe(): ResultHandler<ServerResponse<Eater>> {
        return resultManager.safeApiCall { service.getMe() }
    }

    override suspend fun postMe(eater: EaterRequest): ResultHandler<ServerResponse<Eater>> {
        return resultManager.safeApiCall { service.postMe(eater) }
    }

    override suspend fun updateNotificationGroup(notifications: List<Long>?): ResultHandler<ServerResponse<Eater>> {
        return resultManager.safeApiCall { service.postEaterNotificationGroup(SettingsRequest(notifications)) }
    }

    override suspend fun deleteMe(): ResultHandler<Response<Unit>> {
        return resultManager.safeApiCall { service.deleteMe() }
    }

    override suspend fun postNewAddress(addressRequest: AddressRequest): ResultHandler<ServerResponse<Address>> {
        return resultManager.safeApiCall { service.postNewAddress(addressRequest) }
    }

    override suspend fun deleteAddress(addressId: Long): ResultHandler<ServerResponse<Any>> {
        return resultManager.safeApiCall { service.deleteAddress(addressId) }
    }

}
