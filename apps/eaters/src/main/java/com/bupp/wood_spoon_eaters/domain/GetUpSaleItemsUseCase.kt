package com.bupp.wood_spoon_eaters.domain

import com.bupp.wood_spoon_eaters.domain.comon.UseCase
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.repositories.UpSaleRepository
import kotlinx.coroutines.flow.Flow

class GetUpSaleItemsUseCase(
    private val upSaleRepository: UpSaleRepository
): UseCase<Flow<UpSaleRepository.UpSaleRepoResult<List<MenuItem>>>, GetUpSaleItemsUseCase.Params> {

    data class Params(val orderId: Long, val isForceLoad: Boolean = false)

    override fun execute(params: Params): Flow<UpSaleRepository.UpSaleRepoResult<List<MenuItem>>> =
        upSaleRepository.getUpSaleItems(params.orderId, params.isForceLoad)
}