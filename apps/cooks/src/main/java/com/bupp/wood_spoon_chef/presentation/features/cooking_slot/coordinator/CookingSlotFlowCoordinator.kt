package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator

import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class CookingSlotFlowCoordinatorState{
    data class NextStep(val step: CookingSlotFlowCoordinator.Step? = null, val cookingSlot: CookingSlot? = null)
}

class CookingSlotFlowCoordinator {

    enum class Step {
        OPEN_MENU_FRAGMENT,
        OPEN_REVIEW_FRAGMENT
    }

    private var _stepStateFlow = MutableSharedFlow<CookingSlotFlowCoordinatorState.NextStep>()
    val stepStateFlow = _stepStateFlow

    suspend fun next(step: Step, cookingSlot: CookingSlot?){
        _stepStateFlow.emit(CookingSlotFlowCoordinatorState.NextStep(step, cookingSlot))
    }
}