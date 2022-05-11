package com.bupp.wood_spoon_chef.data.repositories

import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.managers.ChefAnalyticsTracker
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotRequest
import com.bupp.wood_spoon_chef.data.remote.network.ApiService
import com.bupp.wood_spoon_chef.data.remote.network.ResponseHandler
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.base_repos.CookingSlotRepositoryImp

class CookingSlotRepository(
    private val chefAnalyticsTracker: ChefAnalyticsTracker,
    private val userRepository: UserRepository,
    service: ApiService,
    responseHandler: ResponseHandler
) : CookingSlotRepositoryImp(service, responseHandler) {

    override suspend fun postCookingSlot(cookingSlotRequest: CookingSlotRequest): ResponseResult<CookingSlot> {
        val result = super.postCookingSlot(cookingSlotRequest)
        if (result is ResponseSuccess) {
            chefAnalyticsTracker.trackEvent(
                Constants.EVENTS_CREATED_COOKING_SLOT,
                getCreateEventsParam(cookingSlotId = result.data?.id)
            )
        }
        return result
    }

    override suspend fun updateCookingSlot(
        id: Long,
        cookingSlotRequest: CookingSlotRequest
    ): ResponseResult<CookingSlot> {
        val result = super.updateCookingSlot(id, cookingSlotRequest)
        if (result is ResponseSuccess) {
            chefAnalyticsTracker.trackEvent(
                Constants.EVENTS_EDIT_COOKING_SLOT,
                getEditEventsParam(dishId = id)
            )
        }
        return result
    }

    private fun getEditEventsParam(dishId: Long): Map<String, Any> {
        val data = mutableMapOf<String, Any>("dish_id" to dishId)
        data["chef_id"] = userRepository.getCurrentChef()?.id ?: "N/A"
        return data
    }

    private fun getCreateEventsParam(cookingSlotId: Long?): Map<String, Any?> {
        val data = mutableMapOf<String, Any?>("cooking_slot_id" to cookingSlotId)
        data["chef_id"] = userRepository.getCurrentChef()?.id ?: "N/A"
        return data
    }


    companion object {
        const val TAG = "wowCookingSlotRepo"
    }


}