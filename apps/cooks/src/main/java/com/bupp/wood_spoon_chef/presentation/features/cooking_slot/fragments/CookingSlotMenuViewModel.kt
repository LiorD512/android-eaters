package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowStep
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.DishesMenuAdapterModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MenuDishItem
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.DishesWithCategoryRepository
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.CookingSlotsDraftRepository
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.updateItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CookingSlotMenuState(
    val operatingHours: OperatingHours = OperatingHours(null, null),
    val menuItems: List<MenuDishItem> = emptyList(),
    val menuItemsByCategory: List<DishesMenuAdapterModel> = emptyList(),
    val isInEditMode: Boolean = false
)

sealed class CookingSlotMenuEvents {
    data class Error(val message: String = "") : CookingSlotMenuEvents()
    data class ShowMyDishesBottomSheet(val selectedDishes: List<Long> = emptyList()) :
        CookingSlotMenuEvents()
}

class CookingSlotMenuViewModel(
    private val cookingSlotFlowNavigator: CookingSlotFlowCoordinator,
    private val dishesWithCategoryRepository: DishesWithCategoryRepository,
    private val cookingSlotsDraftRepository: CookingSlotsDraftRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(CookingSlotMenuState())
    val state: StateFlow<CookingSlotMenuState> = _state

    private val _events = MutableSharedFlow<CookingSlotMenuEvents>()
    val events: SharedFlow<CookingSlotMenuEvents> = _events

    init {
        viewModelScope.launch {
            cookingSlotsDraftRepository.getDraft().collect { draft ->
                draft?.let {
                    setMenuItems(draft.menuItems)
                    setOperatingHours(draft.operatingHours)
                    setIsInEditMode(draft.originalCookingSlot != null)
                }
            }
        }
    }

    private fun setIsInEditMode(isInEditMode: Boolean) {
        _state.update {
            it.copy(isInEditMode = isInEditMode)
        }
    }

    fun onOpenReviewFragmentClicked(context: Context) {
        viewModelScope.launch {
            validateInputs(context)
        }
    }

    private suspend fun validateInputs(context: Context) {
        val currentDraft = cookingSlotsDraftRepository.getDraftValue() ?: return
        if (_state.value.menuItems.isEmpty()) {
            _events.emit(CookingSlotMenuEvents.Error(context.getString(R.string.menu_empty_error)))
        } else if (_state.value.menuItems.any { it.quantity == 0 }) {
            _events.emit(CookingSlotMenuEvents.Error(context.getString(R.string.quantity_zero_error)))
        } else {
            val updatedDraft = currentDraft.copy(
                menuItems = _state.value.menuItems
            )
            cookingSlotsDraftRepository.saveDraft(updatedDraft)
            cookingSlotFlowNavigator.navigateNext(fromStep = CookingSlotFlowStep.EDIT_MENU)
        }

    }

    fun onAddDishesClick() {
        viewModelScope.launch {
            _events.emit(CookingSlotMenuEvents.ShowMyDishesBottomSheet(_state.value.menuItems.mapNotNull { it.dish?.id }))
        }
    }

    fun addDishesByIds(newIds: List<Long>) {
        viewModelScope.launch {
            val currentlyAddedIds = _state.value.menuItems.mapNotNull { it.dish?.id }
            val newIdsActually = newIds.toMutableList().apply {
                removeAll(currentlyAddedIds)
            }.toList()
            val allUserDishes =
                dishesWithCategoryRepository.getSectionsAndDishes().getOrNull()?.dishes
                    ?: emptyList()
            val newMenuItems = newIdsActually.map { dishId ->
                val dish = allUserDishes.find { it.id == dishId }
                dish?.let { dish ->
                    MenuDishItem(dish = dish)
                }
            }.filterNotNull()

            setMenuItems(_state.value.menuItems + newMenuItems)
        }
    }

    private suspend fun setMenuItems(menuItems: List<MenuDishItem>) {
        val currentSectionWithDishes = combineMenuItemsBySections(
            menuItems
        )
        _state.update {
            it.copy(
                menuItems = menuItems,
                menuItemsByCategory = currentSectionWithDishes
            )
        }
    }

    private fun setOperatingHours(operatingHours: OperatingHours) {
        _state.update {
            it.copy(operatingHours = operatingHours)
        }
    }

    fun onDeleteDishClick(dishToRemoveId: Long?) {
        viewModelScope.launch {
            val updatedMenuItems = _state.value.menuItems.filter { it.dish?.id != dishToRemoveId }
            setMenuItems(updatedMenuItems)
        }
    }

    fun updateQuantity(dishId: Long, quantity: Int) {
        viewModelScope.launch {
            setMenuItems(_state.value.menuItems.updateItem(where = { it.dish?.id == dishId }) {
                it.copy(quantity = quantity)
            })
        }
    }

    private suspend fun combineMenuItemsBySections(
        menuItems: List<MenuDishItem>
    ): List<DishesMenuAdapterModel> {

        val dishesWithCategories =
            dishesWithCategoryRepository.getSectionsAndDishes().getOrNull() ?: return emptyList()

        return dishesWithCategories.sections?.map { section ->

            DishesMenuAdapterModel(
                section = section,
                dishes = menuItems.filter { section.dishIds?.contains(it.dish?.id) == true }
            )
        }?.filter { it.dishes.isNotEmpty() }?.toList() ?: emptyList()
    }
}