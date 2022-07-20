package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.SectionWithDishes
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.DishesWithCategoryRepository
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MyDishesPickerAdapterDish
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MyDishesPickerAdapterModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CookingSlotMenuState(
    val cookingSlot: CookingSlot? = null,
    val selectedDishes: List<Long> = emptyList(),
    val myDishesPickerAdapterModelList: List<MyDishesPickerAdapterModel> = emptyList()
)

sealed class CookingSlotMenuEvents {
    object ShowMyDishesBottomSheet : CookingSlotMenuEvents()
    data class DeleteDish(
        val selectedDishes: List<Long> = emptyList(),
        val dishToRemoveId: Long? = null
    ) : CookingSlotMenuEvents()
}

class CookingSlotMenuViewModel(
    private val cookingSlotFlowNavigator: CookingSlotFlowCoordinator,
    private val dishesWithCategoryRepository: DishesWithCategoryRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(CookingSlotMenuState())
    val state: StateFlow<CookingSlotMenuState> = _state

    private val _events = MutableSharedFlow<CookingSlotMenuEvents>()
    val events: SharedFlow<CookingSlotMenuEvents> = _events

    fun openReviewFragment() {
        viewModelScope.launch {
            cookingSlotFlowNavigator.next(
                CookingSlotFlowCoordinator.Step.OPEN_REVIEW_FRAGMENT,
                _state.value.cookingSlot
            )
        }
    }

    fun onAddDishesClick() {
        viewModelScope.launch {
            _events.emit(CookingSlotMenuEvents.ShowMyDishesBottomSheet)
        }
    }

    fun setDishList(selectedDishes: List<Long>) {
        viewModelScope.launch {
            updateSelectedDishes(selectedDishes)
            val currentSectionWithDishes =
                parseDataForAdapter(dishesWithCategoryRepository.getSectionsAndDishes().getOrNull(), selectedDishes)
            currentSectionWithDishes.let { myDishList ->
                _state.update {
                    it.copy(myDishesPickerAdapterModelList = myDishList)
                }
            }
        }
    }

    private fun updateSelectedDishes(selectedDishes: List<Long>) {
        _state.update {
            it.copy(selectedDishes = selectedDishes)
        }
    }

    fun setCookingSlot(cookingSlot: CookingSlot?) {
        _state.update {
            it.copy(cookingSlot = cookingSlot)
        }
    }

    fun onDeleteDishClick(dishToRemoveId: Long?) {
        viewModelScope.launch {
            _events.emit(CookingSlotMenuEvents.DeleteDish(_state.value.selectedDishes, dishToRemoveId))
        }
    }

    private fun parseDataForAdapter(
        response: SectionWithDishes?,
        selectedDishes: List<Long>?
    ): List<MyDishesPickerAdapterModel> {
        return response?.sections?.map { section ->
            val myDishesPickerAdapterDishes = mutableListOf<MyDishesPickerAdapterDish>()
            val dishes =
                response.dishes?.filter { dish -> section.dishIds?.contains(dish.id) == true }
                    ?.filter { selectedDishes?.contains(it.id) == true }
            dishes?.forEach { dish ->
                myDishesPickerAdapterDishes.add(
                    MyDishesPickerAdapterDish(
                        dish
                    )
                )
            }
            MyDishesPickerAdapterModel(
                section.takeIf { section -> dishes?.isNotEmpty() == true },
                myDishesPickerAdapterDishes
            )
        }?.filter { it.dishes?.isNotEmpty() == true }?.toList() ?: emptyList()
    }

}