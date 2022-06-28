package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator

import kotlinx.coroutines.flow.MutableSharedFlow

class CookingSlotFlowCoordinator {

    enum class Step {
        DEFAULT,
        OPEN_MENU_FRAGMENT,
        OPEN_REVIEW_FRAGMENT
    }

    private var _stepStateFlow = MutableSharedFlow<Step>()
    val stepStateFlow = _stepStateFlow


    suspend fun next(step: Step){
        _stepStateFlow.emit(step)
    }
}