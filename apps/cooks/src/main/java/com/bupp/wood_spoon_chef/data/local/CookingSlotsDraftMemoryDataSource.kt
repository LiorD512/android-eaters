package com.bupp.wood_spoon_chef.data.local

import com.bupp.wood_spoon_chef.data.local.model.CookingSlotDraftData
import kotlinx.coroutines.flow.MutableStateFlow

class CookingSlotsDraftMemoryDataSource {

    val draftCookingSlot = MutableStateFlow<CookingSlotDraftData?>(null)
}