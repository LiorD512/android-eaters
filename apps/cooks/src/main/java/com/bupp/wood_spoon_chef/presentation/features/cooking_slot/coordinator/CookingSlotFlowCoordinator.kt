package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

enum class CookingSlotFlowStep {
    EDIT_DETAILS,
    EDIT_MENU,
    PREVIEW_DETAILS
}

sealed class CookingSlotFlowNavigationEvent() {
    data class NavigateToStep(/* val mode: CookingSlotFlowMode, */ val step: CookingSlotFlowStep,
                                                                   val fromStep: CookingSlotFlowStep?
    ) : CookingSlotFlowNavigationEvent()

    object NavigateDone : CookingSlotFlowNavigationEvent()
}

class CookingSlotFlowCoordinator {

    private var _navigationEvent = MutableSharedFlow<CookingSlotFlowNavigationEvent>()
    val navigationEvent: SharedFlow<CookingSlotFlowNavigationEvent> = _navigationEvent

    suspend fun startFlow() {
//        _navigationEvent.emit(
//            CookingSlotFlowNavigationEvent.NavigateToStep(
//                CookingSlotFlowStep.EDIT_DETAILS,
//                null
//            )
//        )
    }

    suspend fun navigateNext(fromStep: CookingSlotFlowStep) {
        _navigationEvent.emit(
            when (fromStep) {
                CookingSlotFlowStep.EDIT_DETAILS -> CookingSlotFlowNavigationEvent.NavigateToStep(
                    CookingSlotFlowStep.EDIT_MENU,
                    fromStep
                )
                CookingSlotFlowStep.EDIT_MENU -> CookingSlotFlowNavigationEvent.NavigateToStep(
                    CookingSlotFlowStep.PREVIEW_DETAILS,
                    fromStep
                )
                CookingSlotFlowStep.PREVIEW_DETAILS -> CookingSlotFlowNavigationEvent.NavigateDone
            }
        )
    }
}