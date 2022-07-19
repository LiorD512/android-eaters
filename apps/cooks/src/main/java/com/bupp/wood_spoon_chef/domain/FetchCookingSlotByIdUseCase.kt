package com.bupp.wood_spoon_chef.domain

import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.domain.comon.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchCookingSlotByIdUseCase(
    private val cookingSlotRepository: CookingSlotRepository
) : UseCase<Flow<ResponseResult<CookingSlot>>, FetchCookingSlotByIdUseCase.Params> {

    data class Params(val cookingSlotId: Long)

    override fun execute(params: Params): Flow<ResponseResult<CookingSlot>> = flow {
        val result = cookingSlotRepository.getCookingSlotById(
            cookingSlotId = params.cookingSlotId
        )

        emit(result)
    }
}