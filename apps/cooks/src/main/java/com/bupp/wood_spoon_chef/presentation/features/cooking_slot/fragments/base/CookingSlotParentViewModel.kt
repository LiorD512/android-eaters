package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.MenuItem
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.domain.FetchCookingSlotByIdUseCase
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowStep
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.CookingSlotsDraftRepository
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.OperatingHours
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.RecurringRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


data class CookingSlotDraft(
    val selectedDate: Long? = null,
    val operatingHours: OperatingHours = OperatingHours(null, null),
    val lastCallForOrder: Long? = null,
    val recurringRule: RecurringRule? = null,
    val menuItems: List<MenuItem> = mutableListOf(),
    val selectedDishes: List<Long> = emptyList(),
    val originalCookingSlot: CookingSlot?
) {
    val id = originalCookingSlot?.id
}

class CookingSlotParentViewModel(
    private val cookingSlotFlowNavigator: CookingSlotFlowCoordinator,
    private val fetchCookingSlotByIdUseCase: FetchCookingSlotByIdUseCase,
    private val cookingSlotsDraftRepository: CookingSlotsDraftRepository
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
                    is ResponseError -> TODO()
                    is ResponseSuccess -> {
                        cookingSlotsDraftRepository.saveDraft(
                            // TODO MAP!
                            CookingSlotDraft(originalCookingSlot = cookingSlotResponse.data)
                        )
                        cookingSlotFlowNavigator.startFlow()
                    }
                }
            } else {
                cookingSlotsDraftRepository.saveDraft(
                    CookingSlotDraft(selectedDate = selectedDate, originalCookingSlot = null)
                )
                cookingSlotFlowNavigator.startFlow()
            }
        }
    }
}