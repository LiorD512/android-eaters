package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.navigation

import kotlinx.coroutines.flow.MutableStateFlow

class CookingSlotFlowNavigator {

    enum class Step {
        DEFAULT,
        OPEN_MENU_FRAGMENT,
        OPEN_REVIEW_FRAGMENT
    }

    private var _stepStateFlow = MutableStateFlow(Step.DEFAULT)
    val stepStateFlow = _stepStateFlow


    suspend fun next(step: Step){
        _stepStateFlow.emit(step)
    }
}