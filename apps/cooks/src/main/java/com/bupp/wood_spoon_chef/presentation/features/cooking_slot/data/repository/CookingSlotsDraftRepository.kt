package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository

import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotDraft
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

class CookingSlotsDraftRepository(private val memoryDataSource: CookingSlotsDraftMemoryDataSource) {

    fun getDraft(): Flow<CookingSlotDraft?> = memoryDataSource.draftCookingSlot

    suspend fun saveDraft(cookingSlotDraft: CookingSlotDraft?) {
        memoryDataSource.draftCookingSlot.emit(cookingSlotDraft)
    }

}

class CookingSlotsDraftMemoryDataSource {

    val draftCookingSlot = MutableStateFlow<CookingSlotDraft?>(null)
}

suspend fun CookingSlotsDraftRepository.getDraftValue() = getDraft().first()