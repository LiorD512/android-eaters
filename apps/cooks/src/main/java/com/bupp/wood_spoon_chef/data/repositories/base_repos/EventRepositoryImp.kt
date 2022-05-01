package com.bupp.wood_spoon_chef.data.repositories.base_repos

import com.bupp.wood_spoon_chef.data.remote.model.Event
import com.bupp.wood_spoon_chef.data.remote.network.ApiService
import com.bupp.wood_spoon_chef.data.remote.network.ResponseHandler
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.remote.network.base.UnknownError

interface BaseEventRepository {
    suspend fun getEvents(): ResponseResult<List<Event>>
    suspend fun validateCode(eventId: Long, code: String): ResponseResult<Event>
    suspend fun requestJoinEvent(eventId: Long): Any
}

open class EventRepositoryImp(private val service: ApiService, private val responseHandler: ResponseHandler) :
    BaseEventRepository {
    override suspend fun getEvents(): ResponseResult<List<Event>> {
       return responseHandler.safeApiCall { service.getEvents() }
    }

    override suspend fun validateCode(eventId: Long, code: String): ResponseResult<Event> {
        return responseHandler.safeApiCall { service.validateCode(eventId, code)}
    }

    override suspend fun requestJoinEvent(eventId: Long): ResponseResult<Any> {
        //todo :check this shit
        return try {
            service.requestJoinEvent(eventId)
            ResponseSuccess(null)
        } catch (ex:Exception){
            ResponseError(UnknownError("requestJoinEventError"))
        }
    }

}