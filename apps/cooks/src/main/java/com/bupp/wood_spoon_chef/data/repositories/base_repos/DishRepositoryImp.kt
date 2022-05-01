package com.bupp.wood_spoon_chef.data.repositories.base_repos

import com.bupp.wood_spoon_chef.data.remote.model.Dish
import com.bupp.wood_spoon_chef.data.remote.model.DishRequest
import com.bupp.wood_spoon_chef.data.remote.network.ApiService
import com.bupp.wood_spoon_chef.data.remote.network.ResponseHandler
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult

interface BaseDishRepository {

    // New Dish
    suspend fun postDish(dishRequest: DishRequest): ResponseResult<Dish>
    suspend fun updateDish(dishRequest: DishRequest): ResponseResult<Dish>
    suspend fun publishDish(dishId: Long): ResponseResult<Dish>
    suspend fun unPublishDish(dishId: Long): ResponseResult<Any>
    suspend fun duplicateDish(dishId: Long): ResponseResult<Any>
    suspend fun hideDish(dishId: Long): ResponseResult<Any>

    // My Dishes
    suspend fun getMyDishes(): ResponseResult<List<Dish>>
    suspend fun getDishById(editDishId: Long): ResponseResult<Dish>
}

open class DishRepositoryImp(private val service: ApiService, private val responseHandler: ResponseHandler) :
    BaseDishRepository {

    override suspend fun postDish(dishRequest: DishRequest): ResponseResult<Dish> {
        return responseHandler.safeApiCall { service.postDish(dishRequest) }
    }

    override suspend fun updateDish(dishRequest: DishRequest): ResponseResult<Dish> {
        return responseHandler.safeApiCall { service.updateDish(dishRequest.id!!, dishRequest) }
    }

    override suspend fun publishDish(dishId: Long): ResponseResult<Dish> {
        return responseHandler.safeApiCall { service.publishDish(dishId) }
    }

    override suspend fun unPublishDish(dishId: Long): ResponseResult<Any> {
        return responseHandler.safeApiCall { service.unPublishDish(dishId) }
    }

    override suspend fun duplicateDish(dishId: Long): ResponseResult<Any> {
        return responseHandler.safeApiCall { service.duplicateDish(dishId) }
    }

    override suspend fun hideDish(dishId: Long): ResponseResult<Any> {
        return responseHandler.safeApiCall { service.hideDish(dishId) }
    }

    override suspend fun getMyDishes(): ResponseResult<List<Dish>> {
        return responseHandler.safeApiCall { service.getMyDishes() }
    }

    override suspend fun getDishById(dishId: Long): ResponseResult<Dish> {
        return responseHandler.safeApiCall { service.getDishById(dishId) }
    }
}