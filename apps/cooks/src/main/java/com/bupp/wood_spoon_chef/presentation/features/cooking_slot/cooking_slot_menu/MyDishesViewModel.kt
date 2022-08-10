package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.cooking_slot_menu

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.SectionWithDishes
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.CookingSlotReportEventUseCase
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.FilterAdapterSectionModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MyDishesPickerAdapterDish
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MyDishesPickerAdapterModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.CookingSlotsDraftRepository
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.DishesWithCategoryRepository
import com.eatwoodspoon.analytics.events.ChefsCookingSlotsEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

data class MyDishesState(
    val sectionedList: List<MyDishesPickerAdapterModel>? = null,
    val selectedDishesIds: List<Long> = emptyList(),
    val selectedSections: List<String>? = emptyList(),
    val isListFiltered: Boolean? = false,
    val currentSectionWithDishes: SectionWithDishes? = null,
    val showEmptyState: Boolean = false,
    val cookingSlotId: Long? = null
)

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
    }

    private suspend fun getSectionsWithDishes() {
        try {
            val result = dishesWithCategoryRepository.getSectionsAndDishes()
            val currentDishes = result.getOrThrow().dishes
            val sections = result.getOrThrow().sections
            val dishesWithoutSelected = currentDishes?.filter { dish ->
                !_state.value.selectedDishesIds.contains(dish.id)
            }
            val filteredSectionsAndDishes = SectionWithDishes(dishesWithoutSelected, sections)
            updateSectionedList(parseDataForAdapter(filteredSectionsAndDishes))
            setCurrentSectionsWithDishes(filteredSectionsAndDishes)
        } catch (e: Exception) {
            print(e.message)

        }
    }

    private fun setCurrentSectionsWithDishes(currentSectionWithDishes: SectionWithDishes?) {
        _state.update {
            it.copy(currentSectionWithDishes = currentSectionWithDishes)
        }
    }

    fun filterList(input: String) {
        val filteredList = filterByDishName(input)
        val parsedList = parseDataForAdapter(filteredList)
        updateSectionedList(parsedList)
    }

    private fun updateSectionedList(data: List<MyDishesPickerAdapterModel>?) {
        _state.update {
            it.copy(sectionedList = data)
        }
        updateSelectedDishes()
    }

    fun setIsListFiltered(isListFiltered: Boolean?) {
        _state.update {
            it.copy(isListFiltered = isListFiltered)
        }
    }

    fun setSelectedDishesIds(selectedDishesIds: List<Long>) {
        _state.update {
            it.copy(selectedDishesIds = selectedDishesIds)
        }
    }

    private fun updateSelectedDishes() {
        _state.update {
            it.copy(sectionedList = updateSelectedDishes(it.sectionedList, it.selectedDishesIds))
        }
    }

    fun filterBySectionName(sectionList: List<FilterAdapterSectionModel>) {
        val filteredList = filterBySections(sectionList)
        updateSectionedList(parseDataForAdapter(filteredList))
    }

    fun onFilterClick() {
        viewModelScope.launch {
            _events.emit(MyDishesEvent.ShowFilterMenu(_state.value.selectedSections))
        }
    }

    fun updateSelectedSections(selectedSections: List<String>?) {
        _state.update {
            it.copy(selectedSections = selectedSections)
        }
    }

    fun onDishSelected(isChecked: Boolean, dishId: Long) {
        setSelectedDishesIds(addOrRemoveSelectedDishId(isChecked, dishId))
        _state.update {
            it.copy(sectionedList = updateListWithSelected(it.sectionedList, dishId, isChecked))
        }
    }

    private fun addOrRemoveSelectedDishId(isChecked: Boolean, dishId: Long): List<Long> {
        val selectedDishesId = mutableListOf<Long>()
        selectedDishesId.addAll(_state.value.selectedDishesIds)
        if (isChecked) {
            selectedDishesId.add(dishId)
        } else {
            selectedDishesId.remove(dishId)
        }
        return selectedDishesId
    }


    fun onAddClick() {
        val selectedDishes = _state.value.sectionedList?.flatMap { it.dishes ?: emptyList() }
            ?.filter { selectedDish -> selectedDish.isSelected }?.map { dish -> dish.dish?.id }
            ?: emptyList()
        viewModelScope.launch {
            _events.emit(MyDishesEvent.AddDishes(selectedDishes = selectedDishes as List<Long>))
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

    private fun updateSelectedDishes(
        sectionedList: List<MyDishesPickerAdapterModel>?,
        selectedDishesIds: List<Long>
    ): List<MyDishesPickerAdapterModel>? {
        return sectionedList?.map {
            it.copy(dishes = it.dishes?.updateItem(
                where = { dish -> selectedDishesIds.contains(dish.dish?.id) }
            ) { myPickerDish ->
                myPickerDish.copy(isSelected = true)
            })
        }?.filter { !it.dishes.isNullOrEmpty() }
    }

    private fun parseDataForAdapter(
        response: SectionWithDishes?
    ): List<MyDishesPickerAdapterModel> {
        return response?.sections?.map { section ->
            val myDishesPickerAdapterDishes = mutableListOf<MyDishesPickerAdapterDish>()
            val dishes =
                response.dishes?.filter { dish -> section.dishIds?.contains(dish.id) == true }
            dishes?.forEach { dish ->
                myDishesPickerAdapterDishes.add(
                    MyDishesPickerAdapterDish(
                        dish
                    )
                )
            }
            MyDishesPickerAdapterModel(
                section,
                myDishesPickerAdapterDishes
            )
        }?.toList() ?: emptyList()
    }

    private fun filterBySections(sectionList: List<FilterAdapterSectionModel>): SectionWithDishes? {
        val currentSectionWithDishes = _state.value.currentSectionWithDishes
        val sectionName = sectionList.map { it.sectionName }
        return if (sectionName.isNotEmpty()) {
            val filteredSections = currentSectionWithDishes?.sections?.filter { section ->
                sectionName.any { it.equals(section.title, true) }
            }
            SectionWithDishes(currentSectionWithDishes?.dishes, filteredSections)
        } else {
            currentSectionWithDishes
        }
    }

    private fun filterByDishName(input: String): SectionWithDishes {
        val currentSectionWithDishes = _state.value.currentSectionWithDishes
        val filteredDishes =
            currentSectionWithDishes?.dishes?.filter { it.name.contains(input, true) }
        return SectionWithDishes(filteredDishes, currentSectionWithDishes?.sections)
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

fun <T> List<T>.updateItem(where: ((T) -> Boolean), transformation: ((T) -> T)): List<T> {
    return this.map {
        if (where(it)) {
            transformation.invoke(it)
        } else {
            it
        }
    }
}