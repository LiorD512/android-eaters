package com.bupp.wood_spoon_chef.data.repositories

import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.managers.ChefAnalyticsTracker
import com.bupp.wood_spoon_chef.data.remote.model.Dish
import com.bupp.wood_spoon_chef.data.remote.model.DishRequest
import com.bupp.wood_spoon_chef.data.remote.network.ApiService
import com.bupp.wood_spoon_chef.data.remote.network.ResponseHandler
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.base_repos.DishRepositoryImp
import java.util.*

open class DishRepository(
    service: ApiService,
    responseHandler: ResponseHandler,
    private val chefAnalyticsTracker: ChefAnalyticsTracker,
    private val userRepository: UserRepository
) : DishRepositoryImp(service, responseHandler) {

    private var myCurrentDishes: List<Dish>? = listOf()

    override suspend fun getMyDishes(): ResponseResult<List<Dish>> {
        val result = super.getMyDishes()
        if (result is ResponseSuccess) {
            myCurrentDishes = result.data
        }
        return result
    }

    suspend fun postDishAndPublish(dishRequest: DishRequest): ResponseResult<Dish> {
        val dishResponse = super.postDish(dishRequest)
        if (dishResponse is ResponseSuccess) {
            dishResponse.data?.id?.let {
                return publishDish(it)
            }
        }
        return dishResponse
    }

    suspend fun updateDishAndPublish(dishRequest: DishRequest): ResponseResult<Dish> {
        val dishResponse = updateDish(dishRequest)
        if (dishResponse is ResponseSuccess) {
            dishResponse.data?.id?.let {
                return publishDish(it)
            }
        }
        return dishResponse
    }

    suspend fun postDishDraft(dishRequest: DishRequest): ResponseResult<Dish> {
        val result = super.postDish(dishRequest)
        if (result is ResponseSuccess) {
            chefAnalyticsTracker.trackEvent(
                Constants.EVENTS_NEW_DISH,
                getNewDishEventsParam(dishId = dishRequest.id, type = "draft")
            )
        }
        return result
    }

    override suspend fun updateDish(dishRequest: DishRequest): ResponseResult<Dish> {
        val result = super.updateDish(dishRequest)
        if (result is ResponseSuccess) {
            chefAnalyticsTracker.trackEvent(
                Constants.EVENTS_EDIT_DISH,
                getDishEventsParam(dishId = dishRequest.id)
            )
        }
        return result
    }

     override suspend fun publishDish(dishId: Long): ResponseResult<Dish> {
        val dishResponse = super.publishDish(dishId)
        if (dishResponse is ResponseSuccess) {
                chefAnalyticsTracker.trackEvent(
                    Constants.EVENTS_NEW_DISH,
                    getNewDishEventsParam(dishId = dishId, type = "published")
                )
            }
        return dishResponse
    }

    override suspend fun unPublishDish(dishId: Long): ResponseResult<Any> {
        val result = super.unPublishDish(dishId)
        if (result is ResponseSuccess) {
            chefAnalyticsTracker.trackEvent(
                Constants.EVENTS_NEW_DISH,
                getNewDishEventsParam(dishId = dishId, type = "unpublished")
            )
        }
        return result
    }

    fun filterDishes(input: String): List<Dish>? {
        return myCurrentDishes?.filter {
            it.name.toLowerCase(Locale.ROOT).contains(
                input.toLowerCase(
                    Locale.ROOT
                )
            )
        }
    }

    private fun getDishEventsParam(dishId: Long?): Map<String, Any?> {
        val data = mutableMapOf<String, Any?>("dish_id" to dishId)
        data["chef_id"] = userRepository.getCurrentChef()?.id ?: "N/A"
        return data
    }

    private fun getNewDishEventsParam(dishId: Long?, type: String): Map<String, Any?> {
        val data = mutableMapOf<String, Any?>("dish_id" to dishId)
        data["chef_id"] = userRepository.getCurrentChef()?.id ?: "N/A"
        data["type"] = type
        return data
    }

}