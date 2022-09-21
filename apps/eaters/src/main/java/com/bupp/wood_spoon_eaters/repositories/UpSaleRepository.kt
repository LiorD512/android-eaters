package com.bupp.wood_spoon_eaters.repositories

import com.bupp.wood_spoon_eaters.data.data_sorce.memory.MemoryUpSaleItemsDataSource
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.WSError
import com.bupp.wood_spoon_eaters.network.base_repos.UpSaleRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class UpSaleRepository(
    val apiService: UpSaleRepositoryImpl,
    private val memoryUpSaleItemsDataSource: MemoryUpSaleItemsDataSource
) {

    data class UpSaleRepoResult<T>(
        val type: UpSaleRepoStatus,
        val data: T? = null,
        val wsError: List<WSError>? = null
    )

    enum class UpSaleRepoStatus {
        GET_UPSALE_ITEMS_FAILED,
        GET_UPSALE_ITEMS_SUCCESS,
        UPSALE_EMPTY_LIST,
        SERVER_ERROR,
        WS_ERROR
    }

    fun getUpSaleItems(
        orderId: Long,
        forceFetch: Boolean = false
    ): Flow<UpSaleRepoResult<List<MenuItem>>> = flow {
        val localSource = fetchUpSaleItemsLocally().value

        if (forceFetch) {
            emit(fetchUpSaleItemsRemote(orderId))
        } else {
            if (localSource != null) {
                emit(UpSaleRepoResult(UpSaleRepoStatus.GET_UPSALE_ITEMS_SUCCESS, data = localSource))
            } else {
                emit(fetchUpSaleItemsRemote(orderId))
            }
        }
    }

    private suspend fun fetchUpSaleItemsRemote(orderId: Long): UpSaleRepoResult<List<MenuItem>> =
        when (val remoteSource = apiService.fetchUpSaleItems(orderId)) {
            is ResultHandler.GenericError -> {
                UpSaleRepoResult(UpSaleRepoStatus.GET_UPSALE_ITEMS_FAILED)
            }
            ResultHandler.NetworkError -> {
                UpSaleRepoResult(UpSaleRepoStatus.SERVER_ERROR)
            }
            is ResultHandler.Success -> {
                if (remoteSource.value.data == null) {
                    UpSaleRepoResult(UpSaleRepoStatus.UPSALE_EMPTY_LIST)
                } else {
                    memoryUpSaleItemsDataSource.upSaleItems.value = remoteSource.value.data
                    UpSaleRepoResult(UpSaleRepoStatus.GET_UPSALE_ITEMS_SUCCESS, remoteSource.value.data)
                }
            }
            is ResultHandler.WSCustomError -> {
                UpSaleRepoResult(UpSaleRepoStatus.WS_ERROR)
            }

        }

    private fun fetchUpSaleItemsLocally(): MutableStateFlow<List<MenuItem>?> =
        memoryUpSaleItemsDataSource.upSaleItems

}