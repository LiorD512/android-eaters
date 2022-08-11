package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.cooking_slot_menu

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.domain.GetSectionsWithDishesUseCase
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.CookingSlotReportEventUseCase
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowStep
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.create_cooking_slot.OperatingHours
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.DishesMenuAdapterModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MenuDishItem
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.CookingSlotsDraftRepository
import com.eatwoodspoon.analytics.events.ChefsCookingSlotsEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CookingSlotMenuState(
    val operatingHours: OperatingHours = OperatingHours(null, null),
    val menuItems: List<MenuDishItem> = emptyList(),
    val menuItemsByCategory: List<DishesMenuAdapterModel> = emptyList(),
    val isInEditMode: Boolean = false,
    val cookingSlotId: Long? = null
)

sealed class CookingSlotMenuEvents {
    data class Error(val message: String = "") : CookingSlotMenuEvents()
    data class ShowMyDishesBottomSheet(val selectedDishes: List<Long> = emptyList()) :
        CookingSlotMenuEvents()
}

class CookingSlotMenuViewModel(
    private val cookingSlotFlowNavigator: CookingSlotFlowCoordinator,
    private val getSectionsWithDishesUseCase: GetSectionsWithDishesUseCase,
    private val cookingSlotsDraftRepository: CookingSlotsDraftRepository,
    private val eventTracker: CookingSlotReportEventUseCase
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
                    setMode(draft.originalCookingSlot)
                }
            }
        }
        reportEvent { mode, slotId -> ChefsCookingSlotsEvent.MenuScreenOpenedEvent(mode, slotId) }
    }

    private fun setMode(originalCookingSlot: CookingSlot?) {
        _state.update {
            it.copy(
                isInEditMode = originalCookingSlot != null,
                cookingSlotId = originalCookingSlot?.id
            )
        }
    }

    fun onOpenReviewFragmentClicked(context: Context) {
        reportEvent { mode, slotId ->
            ChefsCookingSlotsEvent.MenuScreenNextClickedEvent(
                mode,
                slotId
            )
        }
        viewModelScope.launch {
            validateInputs(context)
        }
    }

    private suspend fun validateInputs(context: Context) {
        val currentDraft = cookingSlotsDraftRepository.getDraftValue() ?: return
        if (_state.value.menuItems.isEmpty()) {
            _events.emit(CookingSlotMenuEvents.Error(context.getString(R.string.menu_empty_error)))
            reportEvent { mode, slotId ->
                ChefsCookingSlotsEvent.MenuLocalValidationFailedEvent(
                    context.getString(R.string.menu_empty_error),
                    mode,
                    slotId
                )
            }
        } else if (_state.value.menuItems.any { it.quantity == 0 }) {
            _events.emit(CookingSlotMenuEvents.Error(context.getString(R.string.quantity_zero_error)))
            reportEvent { mode, slotId ->
                ChefsCookingSlotsEvent.MenuLocalValidationFailedEvent(
                    context.getString(R.string.quantity_zero_error),
                    mode,
                    slotId
                )
            }
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
        reportEvent { mode, slotId -> ChefsCookingSlotsEvent.AddDishesClickedEvent(mode, slotId) }
    }

    fun addDishesByIds(newIds: List<Long>) {
        viewModelScope.launch {
            val currentlyAddedIds = _state.value.menuItems.mapNotNull { it.dish?.id }
            val newIdsActually = newIds.toMutableList().apply {
                removeAll(currentlyAddedIds)
            }.toList()
            getSectionsWithDishesUseCase.execute(GetSectionsWithDishesUseCase.Params())
                .collect { result ->
                    when (result) {
                        is ResponseSuccess -> {
                            val allUserDishes = result.data?.dishes ?: emptyList()
                            val newMenuItems = newIdsActually.mapNotNull { dishId ->
                                val dish = allUserDishes.find { it.id == dishId }
                                dish?.let { dish ->
                                    MenuDishItem(dish = dish)
                                }
                            }

                            setMenuItems(_state.value.menuItems + newMenuItems)
                        }
                        is ResponseError -> {
                            setMenuItems(emptyList())
                        }
                    }
                }
        }
        reportEvent { mode, slotId ->
            ChefsCookingSlotsEvent.AddDishesAddedEvent(
                newIds.joinToString(),
                newIds.count(),
                mode,
                slotId
            )
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
        cookingSlotsDraftRepository.saveDraft(
            cookingSlotsDraftRepository.getDraftValue()?.copy(
                menuItems = menuItems
            )
        )
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
        reportEvent { mode, slotId ->
            ChefsCookingSlotsEvent.DeleteDishClickedEvent(
                dishToRemoveId?.toInt() ?: 0, mode, slotId
            )
        }
    }

    fun updateQuantity(dishId: Long, quantity: Int) {
        viewModelScope.launch {
            setMenuItems(_state.value.menuItems.updateItem(where = { it.dish?.id == dishId }) {
                it.copy(quantity = quantity)
            })
        }
        reportEvent { mode, slotId ->
            ChefsCookingSlotsEvent.DishQuantityChangedEvent(
                dishId.toInt(), quantity, mode, slotId
            )
        }
    }

    private suspend fun combineMenuItemsBySections(
        menuItems: List<MenuDishItem>
    ): List<DishesMenuAdapterModel> {
        return when (val response =
            getSectionsWithDishesUseCase.execute(GetSectionsWithDishesUseCase.Params()).first()) {
            is ResponseError -> emptyList()
            is ResponseSuccess -> {
                response.data?.sections?.map { section ->
                    DishesMenuAdapterModel(
                        section = section,
                        dishes = menuItems.filter { section.dishIds?.contains(it.dish?.id) == true }
                    )
                }?.filter { it.dishes.isNotEmpty() }?.toList() ?: emptyList()
            }
        }
    }

    private fun reportEvent(factory: (mode: ChefsCookingSlotsEvent.ModeValues, slotId: Int?) -> ChefsCookingSlotsEvent) {
        val mode = if (_state.value.isInEditMode) {
            ChefsCookingSlotsEvent.ModeValues.Edit
        } else {
            ChefsCookingSlotsEvent.ModeValues.New
        }
        eventTracker.reportEvent(factory.invoke(mode, _state.value.cookingSlotId?.toInt()))
    }
}