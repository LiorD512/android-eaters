package com.bupp.wood_spoon_chef.data.repositories.base_repos

import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotRequest
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotSlim
import com.bupp.wood_spoon_chef.data.remote.model.Order
import com.bupp.wood_spoon_chef.data.remote.network.ResponseHandler
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.network.CookingSlotApiService

interface BaseCookingSlotRepository {

    suspend fun getCookingSlotByTimePeriod(
        startTimeSeconds: Long,
        endTimeSeconds: Long
    ): ResponseResult<List<CookingSlotSlim>>

    suspend fun postCookingSlot(cookingSlotRequest: CookingSlotRequest): ResponseResult<CookingSlot>
    suspend fun updateCookingSlot(
        id: Long,
        cookingSlotRequest: CookingSlotRequest
    ): ResponseResult<CookingSlot>

    suspend fun cancelCookingSlot(id: Long, detach: Boolean?): ResponseResult<Any>
    suspend fun getTraceableCookingSlot(): ResponseResult<List<CookingSlot>>
    suspend fun updateCookingSlotAvailability(
        cookingSlotId: Long,
        isActive: Boolean
    ): ResponseResult<CookingSlot>

    suspend fun getActiveCookingSlot(): ResponseResult<CookingSlot>
    suspend fun getCookingSlotOrders(cookingSlotId: Long): ResponseResult<List<Order>>
    suspend fun getCookingSlotById(cookingSlotId: Long): ResponseResult<CookingSlot>
}

open class CookingSlotRepositoryImp(
    private val service: CookingSlotApiService,
    private val responseHandler: ResponseHandler
) : BaseCookingSlotRepository {

    override suspend fun getCookingSlotByTimePeriod(
        startTimeSeconds: Long,
        endTimeSeconds: Long
    ): ResponseResult<List<CookingSlotSlim>> = responseHandler.safeApiCall {
        service.getCookingSlot(startTimeSeconds, endTimeSeconds)
    }

    override suspend fun postCookingSlot(
        cookingSlotRequest: CookingSlotRequest
    ): ResponseResult<CookingSlot> {
        return responseHandler.safeApiCall { service.postCookingSlot(cookingSlotRequest) }
    }

    override suspend fun updateCookingSlot(
        id: Long,
        cookingSlotRequest: CookingSlotRequest
    ): ResponseResult<CookingSlot> {
        return responseHandler.safeApiCall { service.updateCookingSlot(id, cookingSlotRequest) }
    }

    override suspend fun cancelCookingSlot(id: Long, detach: Boolean?): ResponseResult<Any> {
        return responseHandler.safeApiCall { service.cancelCookingSlot(id, detach) }
    }

    override suspend fun getTraceableCookingSlot(): ResponseResult<List<CookingSlot>> {
        return responseHandler.safeApiCall { service.getTrackableCookingSlot() }
    }

    override suspend fun updateCookingSlotAvailability(
        cookingSlotId: Long,
        isActive: Boolean
    ): ResponseResult<CookingSlot> {
        return responseHandler.safeApiCall {
            service.updateCookingSlotAvailability(
                cookingSlotId,
                isActive
            )
        }
    }

    override suspend fun getActiveCookingSlot(): ResponseResult<CookingSlot> {
        return responseHandler.safeApiCall { service.getActiveCookingSlot() }
    }

    override suspend fun getCookingSlotOrders(cookingSlotId: Long): ResponseResult<List<Order>> {
        return responseHandler.safeApiCall { service.getCookingSlotOrders(cookingSlotId) }
    }

    override suspend fun getCookingSlotById(cookingSlotId: Long): ResponseResult<CookingSlot> {
        return responseHandler.safeApiCall { service.getCookingSlotById(cookingSlotId) }
    }

}