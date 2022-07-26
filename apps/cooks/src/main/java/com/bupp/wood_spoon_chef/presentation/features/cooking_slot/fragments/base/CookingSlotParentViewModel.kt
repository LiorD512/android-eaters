package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.local.model.CookingSlotDraftData
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.domain.FetchCookingSlotByIdUseCase
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.CookingSlotsDraftRepository
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper.OriginalCookingSlotToDraftCookingSlotMapper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CookingSlotParentViewModel(
    private val cookingSlotFlowNavigator: CookingSlotFlowCoordinator,
    private val fetchCookingSlotByIdUseCase: FetchCookingSlotByIdUseCase,
    private val cookingSlotsDraftRepository: CookingSlotsDraftRepository,
    private val originalCookingSlotToDraftCookingSlotMapper: OriginalCookingSlotToDraftCookingSlotMapper
) : BaseViewModel() {

    val navigationEvent = cookingSlotFlowNavigator.navigationEvent

    @Suppress("MoveVariableDeclarationIntoWhen")
    fun initWith(
        cookingSlotId: Long?,
        selectedDate: Long?
    ) {
        viewModelScope.launch {
            if (cookingSlotId != null) {
                val cookingSlotResponse = fetchCookingSlotByIdUseCase.execute(
                    FetchCookingSlotByIdUseCase.Params(cookingSlotId = cookingSlotId)
                ).first()
                when (cookingSlotResponse) {
                    is ResponseError -> {}
                    is ResponseSuccess -> {
                        cookingSlotResponse.data?.let { slot ->
                            val editedSlot = originalCookingSlotToDraftCookingSlotMapper
                                .mapOriginalCookingSlotToDraft(
                                    originalCookingSlot = slot
                                ).copy(isEditing = true)
                            cookingSlotsDraftRepository.saveDraft(editedSlot)
                        }
                        cookingSlotFlowNavigator.startFlow()
                    }
                }
            } else {
                cookingSlotsDraftRepository.saveDraft(
                    CookingSlotDraftData(
                        selectedDate = selectedDate,
                        originalCookingSlot = null
                    )
                )
                cookingSlotFlowNavigator.startFlow()
            }
        }
    }
}