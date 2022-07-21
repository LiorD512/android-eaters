package com.bupp.wood_spoon_chef.domain

import com.bupp.wood_spoon_chef.data.remote.model.SectionWithDishes
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult
import com.bupp.wood_spoon_chef.data.repositories.DishRepository
import com.bupp.wood_spoon_chef.domain.comon.UseCase
import kotlinx.coroutines.flow.Flow

class GetSectionsWithDishesUseCase(
    private val dishRepository: DishRepository
) : UseCase<Flow<ResponseResult<SectionWithDishes>>, GetSectionsWithDishesUseCase.Params> {

    data class Params(val isForceLoad: Boolean)

    override fun execute(params: Params): Flow<ResponseResult<SectionWithDishes>> =
        dishRepository.getSectionsWithDishes(params.isForceLoad)
}
