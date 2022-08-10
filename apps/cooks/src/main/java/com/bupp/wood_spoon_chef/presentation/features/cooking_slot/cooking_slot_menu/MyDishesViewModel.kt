package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.cooking_slot_menu

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.Dish
import com.bupp.wood_spoon_chef.data.remote.model.SectionWithDishes
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.CookingSlotReportEventUseCase
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.FilterAdapterSectionModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MyDishesPickerAdapterDish
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MyDishesPickerAdapterModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.CookingSlotsDraftRepository
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.DishesWithCategoryRepository
import com.eatwoodspoon.analytics.events.ChefsCookingSlotsEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

data class MyDishesState(
    val initialList: List<MyDishesPickerAdapterModel> = emptyList(),
    val visibleItems: List<MyDishesPickerAdapterModel> = emptyList(),
    val excludedDishesIds: List<Long> = emptyList(),
    val searchCriteria: String? = null,
    val filteredSections: List<String>? = null,
    val showEmptyState: Boolean = false,
    val cookingSlotId: Long? = null
) {
    val isListFiltered = !searchCriteria.isNullOrEmpty()
}

sealed class MyDishesEvent {
    data class Error(val message: String? = null) : MyDishesEvent()
    data class ShowFilterMenu(
        val selectedSections:
        List<String>? = emptyList()
    ) : MyDishesEvent()

    data class AddDishes(val selectedDishes: List<Long> = emptyList()) : MyDishesEvent()
}

class MyDishesViewModel(
    private val dishesWithCategoryRepository: DishesWithCategoryRepository,
    private val cookingSlotsDraftRepository: CookingSlotsDraftRepository,
    private val eventTracker: CookingSlotReportEventUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(MyDishesState())
    val state: StateFlow<MyDishesState> = _state

    private val _events = MutableSharedFlow<MyDishesEvent>()
    val events: SharedFlow<MyDishesEvent> = _events

    init {
        viewModelScope.launch {
            getSectionsWithDishes()
            val originalCookingSlot =
                cookingSlotsDraftRepository.getDraft().first()?.originalCookingSlot
            setMode(originalCookingSlot)
        }
        reportEvent { mode, slotId -> ChefsCookingSlotsEvent.DishPickerShownEvent(mode, slotId) }
        viewModelScope.launch {
            _state.collect {
                _state.update { it.copy(
                    visibleItems = it.initialList
                        .filterExcluding(it.excludedDishesIds)
                        .filterByDishName(it.searchCriteria)
                        .filterBySections(it.filteredSections)
                ) }
            }
        }
    }


    private suspend fun getSectionsWithDishes() {
        try {

            val result = dishesWithCategoryRepository.getSectionsAndDishes().getOrThrow()
            _state.update { it.copy(initialList = result.mapToAdapterModels()) }
        } catch (e: Exception) {
            print(e.message)

        }
    }

    fun filterList(input: String) {
        viewModelScope.launch {
            _state.update { it.copy(searchCriteria = input) }
        }
    }

    fun setExcludedDishesIds(excludedDishesIds: List<Long>) {
        _state.update {
            it.copy(excludedDishesIds = excludedDishesIds)
        }
    }

    fun filterBySectionName(sectionList: List<FilterAdapterSectionModel>) {
        _state.update {
            it.copy(filteredSections = sectionList.map { it.sectionName })
        }
    }

    fun onFilterClick() {
        viewModelScope.launch {
            _events.emit(MyDishesEvent.ShowFilterMenu(_state.value.filteredSections))
        }
    }

    fun onDishSelected(isChecked: Boolean, dishId: Long) {
        _state.update {
            it.copy(
                initialList = updateListWithSelected(it.initialList, dishId, isChecked)
                    ?: emptyList()
            )
        }
    }

    fun onAddClick() {
        val selectedDishes = _state.value.initialList.flatMap { it.dishes ?: emptyList() }
            .filter { selectedDish -> selectedDish.isSelected }.mapNotNull { dish -> dish.dish?.id }

        viewModelScope.launch {
            _events.emit(MyDishesEvent.AddDishes(selectedDishes = selectedDishes))
        }
    }

    private fun updateListWithSelected(
        sectionedList: List<MyDishesPickerAdapterModel>?,
        dishId: Long,
        isChecked: Boolean
    ): List<MyDishesPickerAdapterModel>? {
        return sectionedList?.map {
            it.copy(dishes = it.dishes?.updateItem(
                where = { dish -> dish.dish?.id == dishId }
            ) { myPickerDish ->
                myPickerDish.copy(isSelected = isChecked)
            })
        }
    }

    private fun setMode(originalCookingSlot: CookingSlot?) {
        _state.update {
            it.copy(
                cookingSlotId = originalCookingSlot?.id
            )
        }
    }

    private fun reportEvent(factory: (mode: ChefsCookingSlotsEvent.ModeValues, slotId: Int?) -> ChefsCookingSlotsEvent) {
        val mode = if (_state.value.cookingSlotId != null) {
            ChefsCookingSlotsEvent.ModeValues.Edit
        } else {
            ChefsCookingSlotsEvent.ModeValues.New
        }
        eventTracker.reportEvent(factory.invoke(mode, _state.value.cookingSlotId?.toInt()))
    }
}

//TODO Move to shared extension
fun <T> List<T>.updateItem(where: ((T) -> Boolean), transformation: ((T) -> T)): List<T> {
    return this.map {
        if (where(it)) {
            transformation.invoke(it)
        } else {
            it
        }
    }
}

private fun List<MyDishesPickerAdapterModel>.filterExcluding(dishIds: List<Long>?): List<MyDishesPickerAdapterModel> {
    if(dishIds.isNullOrEmpty()) {
        return this
    }
    return this.filterByDish { dish ->
        dish.id !in dishIds
    }
}

private fun List<MyDishesPickerAdapterModel>.filterByDishName(input: String?): List<MyDishesPickerAdapterModel> {
    if(input.isNullOrEmpty()) {
        return this
    }
    return this.filterByDish { dish ->
        dish.name.contains(input, ignoreCase = true)
    }
}

private fun List<MyDishesPickerAdapterModel>.filterByDish(where: (dish: Dish) -> Boolean): List<MyDishesPickerAdapterModel> {
    return this.map { section ->
        section.copy(dishes = section.dishes?.filter { dish ->
            dish.dish?.let { where(it) } ?: false
        })
    }.filter { section -> !section.dishes.isNullOrEmpty() }
}

private fun List<MyDishesPickerAdapterModel>.filterBySections(sectionList: List<String>?): List<MyDishesPickerAdapterModel> {
    if (sectionList == null) {
        return this
    }
    return this.filter { section ->
        section.section?.title in sectionList
    }
}

private fun SectionWithDishes.mapToAdapterModels(): List<MyDishesPickerAdapterModel> {
    return this.sections?.map { section ->

        val dishModels = this.dishes?.filter { dish -> section.dishIds?.contains(dish.id) == true }
            ?.map { dish ->
                MyDishesPickerAdapterDish(dish)
            }

        dishModels.takeIf { !it.isNullOrEmpty() }?.let {
            MyDishesPickerAdapterModel(section, it)
        }


    }?.toList()?.filterNotNull() ?: emptyList()
}
