package com.bupp.wood_spoon_eaters.features.upsale.data_source.repository

import com.bupp.wood_spoon_eaters.features.upsale.data_source.memory.MemoryUpSaleItemsDataSource
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow

data class UpSaleData(
    val orderId: Long,
    val items: List<MenuItem>?
)

class UpSaleRepository(
    val apiService: ApiService,
    private val memoryUpSaleItemsDataSource: MemoryUpSaleItemsDataSource
) {

    private val orderIdOfAlreadyShowedUpSaleScreen = mutableListOf<Long>()

    suspend fun fetchUpSaleItemsRemote(orderId: Long): UpSaleData? {
        val remoteSource = apiService.getUpsaleItemsByOrderId(orderId)
        if (remoteSource.data != null) {
            memoryUpSaleItemsDataSource.upSaleItems.value = UpSaleData(orderId, remoteSource.data)
            return UpSaleData(orderId, remoteSource.data)
        }
        return null
    }

    fun getUpSaleItemsLocally(orderId: Long): UpSaleData? =
        memoryUpSaleItemsDataSource.upSaleItems.value.takeIf { it?.orderId == orderId }

    fun getOrderIdsOfShownUpSaleScreen():List<Long>{
        return orderIdOfAlreadyShowedUpSaleScreen
    }

    fun setUpSaleItemShowedForThisOrder(orderId: Long){
        orderIdOfAlreadyShowedUpSaleScreen.add(orderId)
    }
}