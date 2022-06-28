package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import kotlinx.coroutines.launch

class CookingSlotMenuViewModel(private val cookingSlotFlowNavigator: CookingSlotFlowCoordinator): BaseViewModel() {

    fun openReviewFragment(){
        viewModelScope.launch {
            cookingSlotFlowNavigator.next(CookingSlotFlowCoordinator.Step.OPEN_REVIEW_FRAGMENT)
        }
    }
}