package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.cooking_slot_menu

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.Section
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.FilterAdapterSectionModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.DishesWithCategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FilterMenuState(
    val sectionList: List<FilterAdapterSectionModel> = emptyList(),
    val selectedSections: List<String>? = emptyList(),
    val showClearAll: Boolean = false
)

sealed class FilterMenuEvent {
    data class ApplyClicked(val selectedSections: List<FilterAdapterSectionModel>?) :
        FilterMenuEvent()
}

class FilterMenuViewModel(
    private val dishesWithCategoryRepository: DishesWithCategoryRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(FilterMenuState())
    val state: StateFlow<FilterMenuState> = _state

    private val _events = MutableSharedFlow<FilterMenuEvent>()
    val events: SharedFlow<FilterMenuEvent> = _events

    init {
        getSectionsList()
    }

    private fun getSectionsList() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val result = dishesWithCategoryRepository.getSectionsAndDishes()
            val sections = result.getOrThrow().sections
            sections?.let {
                updateSectionList(convertSectionsToFilterAdapterModelList(it))
            }
        } catch (e: Exception) {
            e.message
        }
    }

    private fun updateSectionList(data: List<FilterAdapterSectionModel>) {
        _state.update {
            it.copy(sectionList = updateSectionItem(data, it.selectedSections, true))
        }
        showClearAll()
    }

    fun onSectionSelected(sectionName: String, isSelected: Boolean) {
        val sectionNameList = mutableListOf<String>()
        sectionNameList.add(sectionName)
        _state.update {
            it.copy(sectionList = updateSectionItem(it.sectionList, sectionNameList, isSelected))
        }
        showClearAll()
    }

    private fun showClearAll() {
        _state.update {
            it.copy(showClearAll = it.sectionList.any { section -> section.isSelected })
        }
    }

    fun setSelectedSections(selectedSections: List<String>?) {
        _state.update {
            it.copy(selectedSections = selectedSections)
        }
    }

    private fun updateSectionItem(
        sectionList: List<FilterAdapterSectionModel>,
        sectionNameList: List<String>?,
        isSelected: Boolean
    ): List<FilterAdapterSectionModel> {
        return sectionList.updateItem(
            where = { section -> sectionNameList?.contains(section.sectionName) == true }
        ) {
            it.copy(isSelected = isSelected)
        }
    }

    fun onApplyClick() {
        viewModelScope.launch {
            val selectedSections = _state.value.sectionList.filter { it.isSelected }
            _events.emit(FilterMenuEvent.ApplyClicked(selectedSections))
        }
    }

    fun onClearAllClick() {
        setSelectedSections(null)
        getSectionsList()
    }

    private fun convertSectionsToFilterAdapterModelList(
        sectionList: List<Section>
    ): List<FilterAdapterSectionModel> {
        return sectionList.map { section ->
            FilterAdapterSectionModel(
                section.title ?: ""
            )
        }.toList()
    }
}