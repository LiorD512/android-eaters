package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base

import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator

class CookingSlotParentViewModel(cookingSlotFlowNavigator: CookingSlotFlowCoordinator): BaseViewModel() {

    val stepStateFlow = cookingSlotFlowNavigator.stepStateFlow
}