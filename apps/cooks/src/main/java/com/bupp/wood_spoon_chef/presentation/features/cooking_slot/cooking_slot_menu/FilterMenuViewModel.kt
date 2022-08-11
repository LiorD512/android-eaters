package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.cooking_slot_menu

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.Section
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.domain.GetSectionsWithDishesUseCase
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.CookingSlotReportEventUseCase
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.FilterAdapterSectionModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.CookingSlotsDraftRepository
import com.eatwoodspoon.analytics.events.ChefsCookingSlotsEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FilterMenuState(
    val sectionList: List<FilterAdapterSectionModel> = emptyList(),
    val selectedSections: List<String>? = emptyList(),
    val showClearAll: Boolean = false,
    val cookingSlotId: Long? = null
)

sealed class FilterMenuEvent {
    data class ApplyClicked(val selectedSections: List<FilterAdapterSectionModel>?) :
        FilterMenuEvent()
}

class FilterMenuViewModel(
    private val getSectionsWithDishesUseCase: GetSectionsWithDishesUseCase,
    private val cookingSlotsDraftRepository: CookingSlotsDraftRepository,
    private val eventTracker: CookingSlotReportEventUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(FilterMenuState())
    val state: StateFlow<FilterMenuState> = _state

    private val _events = MutableSharedFlow<FilterMenuEvent>()
    val events: SharedFlow<FilterMenuEvent> = _events

    fun init(selectedSections: List<String>?) {
        setSelectedSections(selectedSections)
        viewModelScope.launch {
            getSectionsList()
            val originalCookingSlot =
                cookingSlotsDraftRepository.getDraft().first()?.originalCookingSlot
            setMode(originalCookingSlot)
        }
        reportEvent { mode, slotId ->
            ChefsCookingSlotsEvent.CategoriesFilterShownEvent(
                mode,
                slotId
            )
        }
    }

    private suspend fun getSectionsList() {
        try {
            when (val sectionWithDishesResult =
                getSectionsWithDishesUseCase.execute
                    (GetSectionsWithDishesUseCase.Params()).first()) {
                is ResponseError -> {
                    updateSectionList(emptyList())
                }
                is ResponseSuccess -> {
                    sectionWithDishesResult.data?.sections?.let {
                        updateSectionList(convertSectionsToFilterAdapterModelList(it))
                    }
                }
            }

        } catch (e: Exception) {
            e.message
        }
    }

    private fun setMode(originalCookingSlot: CookingSlot?) {
        _state.update {
            it.copy(
                cookingSlotId = originalCookingSlot?.id
            )
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

    private fun setSelectedSections(selectedSections: List<String>?) {
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
        viewModelScope.launch {
            getSectionsList()
        }
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

    private fun reportEvent(factory: (mode: ChefsCookingSlotsEvent.ModeValues, slotId: Int?) -> ChefsCookingSlotsEvent) {
        val mode = if (_state.value.cookingSlotId != null) {
            ChefsCookingSlotsEvent.ModeValues.Edit
        } else {
            ChefsCookingSlotsEvent.ModeValues.New
        }
        eventTracker.reportEvent(factory.invoke(mode, _state.value.cookingSlotId?.toInt()))
    }
}