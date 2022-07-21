package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.SectionWithDishes
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.DishesMenuAdapterModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MenuDishItem
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.DishesWithCategoryRepository
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.updateItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CookingSlotMenuState(
    val cookingSlot: CookingSlot? = null,
    val selectedDishes: List<Long> = emptyList(),
    val dishesMenuAdapterModelList: List<DishesMenuAdapterModel> = emptyList()
)

sealed class CookingSlotMenuEvents {
    data class ShowMyDishesBottomSheet(val selectedDishes: List<Long> = emptyList()) :
        CookingSlotMenuEvents()
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
            _events.emit(CookingSlotMenuEvents.ShowMyDishesBottomSheet(_state.value.selectedDishes))
        }
    }

    fun setDishList(selectedDishes: List<Long>) {
        viewModelScope.launch {
            updateSelectedDishes(selectedDishes)
            val currentSectionWithDishes =
                parseDataForAdapter(
                    dishesWithCategoryRepository.getSectionsAndDishes().getOrNull(),
                    selectedDishes
                )
            currentSectionWithDishes.let { myDishList ->
                _state.update {
                    it.copy(dishesMenuAdapterModelList = myDishList)
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
        val dishes = mutableListOf<MenuDishItem>()
        dishes.addAll(_state.value.dishesMenuAdapterModelList.flatMap { it.dishes })
        dishes.removeIf { it.dish?.id?.equals(dishToRemoveId) == true }
       val dishList = _state.value.dishesMenuAdapterModelList.map {
            it.copy(dishes = dishes)
        }
        val selectedDishes = dishes.map { it.dish?.id }
        _state.update {
            it.copy(dishesMenuAdapterModelList = dishList, selectedDishes = selectedDishes as List<Long>)
        }
    }

    fun updateQuantity(dishId: Long, quantity: Int) {
        _state.update {
            it.copy(
                dishesMenuAdapterModelList = updateListWithQuantity
                    (_state.value.dishesMenuAdapterModelList, dishId, quantity) ?: emptyList()
            )
        }
    }

    private fun updateListWithQuantity(
        sectionedList: List<DishesMenuAdapterModel>?,
        dishId: Long,
        quantity: Int
    ): List<DishesMenuAdapterModel>? {
        return sectionedList?.map {
            it.copy(dishes = it.dishes.updateItem(where = { dish -> dish.dish?.id == dishId }) { dishes ->
                dishes.copy(quantity = quantity)
            })
        }
    }


    private fun parseDataForAdapter(
        response: SectionWithDishes?,
        selectedDishes: List<Long>?
    ): List<DishesMenuAdapterModel> {
        return response?.sections?.map { section ->
            val menuItemDishList = mutableListOf<MenuDishItem>()
            val dishes =
                response.dishes?.filter { dish -> section.dishIds?.contains(dish.id) == true }
                    ?.filter { selectedDishes?.contains(it.id) == true }
            dishes?.forEach { dish ->
                menuItemDishList.add(
                    MenuDishItem(
                        dish
                    )
                )
            }
            DishesMenuAdapterModel(
                section.takeIf { section -> dishes?.isNotEmpty() == true },
                menuItemDishList
            )
        }?.filter { it.dishes.isNotEmpty() }?.toList() ?: emptyList()
    }
}