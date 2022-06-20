package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base

import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.navigation.CookingSlotFlowNavigator

class CookingSlotParentViewModel(cookingSlotFlowNavigator: CookingSlotFlowNavigator): BaseViewModel() {

    val stepStateFlow = cookingSlotFlowNavigator.stepStateFlow
}