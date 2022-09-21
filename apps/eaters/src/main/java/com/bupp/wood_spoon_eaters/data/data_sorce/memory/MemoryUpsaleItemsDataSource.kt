package com.bupp.wood_spoon_eaters.data.data_sorce.memory

import com.bupp.wood_spoon_eaters.model.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow

class MemoryUpSaleItemsDataSource {

    val upSaleItems = MutableStateFlow<List<MenuItem>?>(null)
}