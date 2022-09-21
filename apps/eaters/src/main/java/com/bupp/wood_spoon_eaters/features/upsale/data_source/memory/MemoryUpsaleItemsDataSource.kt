package com.bupp.wood_spoon_eaters.features.upsale.data_source.memory

import com.bupp.wood_spoon_eaters.features.upsale.data_source.repository.UpSaleData
import kotlinx.coroutines.flow.MutableStateFlow

class MemoryUpSaleItemsDataSource {

    val upSaleItems = MutableStateFlow<UpSaleData?>(null)
}