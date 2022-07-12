package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.Dish
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CookingSlotMenuState(
    val cookingSlot: CookingSlot? = null,
    val dishList: MutableList<Dish>? = mutableListOf()
)

sealed class CookingSlotMenuEvents {
    data class ShowMyDishesBottomSheet(val dishList: List<Dish>? = null) : CookingSlotMenuEvents()
}

class CookingSlotMenuViewModel(private val cookingSlotFlowNavigator: CookingSlotFlowCoordinator) :
    BaseViewModel() {

    private val _state = MutableStateFlow(CookingSlotMenuState())
    val state: StateFlow<CookingSlotMenuState> = _state

    private val _events = MutableSharedFlow<CookingSlotMenuEvents>()
    val events: SharedFlow<CookingSlotMenuEvents> = _events

    fun openReviewFragment() {
        viewModelScope.launch {
            cookingSlotFlowNavigator.next(CookingSlotFlowCoordinator.Step.OPEN_REVIEW_FRAGMENT, _state.value.cookingSlot)
        }
    }

    fun onAddDishesClick(){
        viewModelScope.launch {
            _events.emit(CookingSlotMenuEvents.ShowMyDishesBottomSheet(_state.value.dishList))
        }
    }

    fun setDishList(dishList: List<Dish>?){
        _state.update {
            it.copy(dishList = dishList as MutableList<Dish>?)
        }
    }

    fun setCookingSlot(cookingSlot: CookingSlot?){
        _state.update {
            it.copy(cookingSlot = cookingSlot)
        }
    }

    fun removeDishFromList(dish: Dish){
        _state.value.dishList?.remove(dish)
    }
}