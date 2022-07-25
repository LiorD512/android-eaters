package com.bupp.wood_spoon_chef.data.local

import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotDraft
import kotlinx.coroutines.flow.MutableStateFlow

class CookingSlotsDraftMemoryDataSource {

    val draftCookingSlot = MutableStateFlow<CookingSlotDraft?>(null)
}