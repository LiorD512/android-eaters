package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository

import com.bupp.wood_spoon_chef.data.local.CookingSlotsDraftMemoryDataSource
import com.bupp.wood_spoon_chef.data.local.model.CookingSlotDraftData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CookingSlotsDraftRepository(
    private val memoryDataSource: CookingSlotsDraftMemoryDataSource
) {

    fun getDraft(): Flow<CookingSlotDraftData?> = memoryDataSource.draftCookingSlot

    suspend fun saveDraft(cookingSlotDraft: CookingSlotDraftData?) {
        memoryDataSource.draftCookingSlot.emit(cookingSlotDraft)
    }

    suspend fun getDraftValue() = getDraft().first()
}