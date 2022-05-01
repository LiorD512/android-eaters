package com.bupp.wood_spoon_chef.domain

import com.bupp.wood_spoon_chef.data.repositories.MetaDataRepository
import com.bupp.wood_spoon_chef.domain.comon.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class IsCallSupportByCancelingOrderUseCase(
    private val metaDataRepository: MetaDataRepository
) : UseCase<Flow<Boolean>, Nothing?> {

    override fun execute(params: Nothing?): Flow<Boolean> = flow {
        emit(metaDataRepository.isSupportCancellationEnabled())
    }
}