package com.bupp.wood_spoon_chef.data.repositories.base_repos

import com.bupp.wood_spoon_chef.data.remote.model.*
import com.bupp.wood_spoon_chef.data.remote.network.ApiService
import com.bupp.wood_spoon_chef.data.remote.network.ResponseHandler
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult

interface BaseUserRepository {

    suspend fun getCode(phone: String): ResponseResult<Any>
    suspend fun validateCode(phone: String, code: String): ResponseResult<Cook>

    suspend fun getMe(): ResponseResult<Cook>
    suspend fun postMe(eater: CookRequest): ResponseResult<Cook>
    suspend fun deleteAccount(): ResponseResult<Any>
    suspend fun postTicket(fullName: String, email: String, message: String): ResponseResult<Any>

    suspend fun getStats(): ResponseResult<Earnings>
    suspend fun getCookReview(): ResponseResult<Review>
    suspend fun getOtl(): ResponseResult<Otl>
}

open class UserRepositoryImp(
    private val service: ApiService,
    private val responseHandler: ResponseHandler,
) : BaseUserRepository {

    override suspend fun getCode(phone: String): ResponseResult<Any> {
        return responseHandler.safeApiCall { service.getCode(phone) }
    }

    override suspend fun validateCode(phone: String, code: String): ResponseResult<Cook> {
        return responseHandler.safeApiCall { service.validateCode(phone, code) }
    }

    override suspend fun getMe(): ResponseResult<Cook> {
        return responseHandler.safeApiCall { service.getMe() }
    }

    override suspend fun postMe(eater: CookRequest): ResponseResult<Cook> {
        return responseHandler.safeApiCall { service.postMe(eater) }
    }

    override suspend fun deleteAccount(): ResponseResult<Any> {
        return responseHandler.safeApiCall { service.deleteAccount() }
    }

    override suspend fun postTicket(
        fullName: String,
        email: String,
        message: String
    ): ResponseResult<Any> {
        return responseHandler.safeApiCall { service.postTicket(fullName, email, message) }
    }

    override suspend fun getStats(): ResponseResult<Earnings> {
        return responseHandler.safeApiCall { service.getStats() }
    }

    override suspend fun getCookReview(): ResponseResult<Review> {
        return responseHandler.safeApiCall { service.getCookReview() }
    }

    override suspend fun getOtl(): ResponseResult<Otl> {
        return responseHandler.safeApiCall { service.getOtl() }
    }


}