package com.bupp.wood_spoon_chef.domain

import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.domain.comon.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CancelCookingSlotUseCase(
    private val cookingSlotRepository: CookingSlotRepository
) : UseCase<Flow<ResponseResult<Any>>, CancelCookingSlotUseCase.Params> {

    data class Params(val cookingSlotId: Long, val detach: Boolean?)

    override fun execute(params: Params): Flow<ResponseResult<Any>> = flow {
        val result = cookingSlotRepository.cancelCookingSlot(params.cookingSlotId, params.detach)
        emit(result)
    }
}