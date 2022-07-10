package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.DishCategory
import com.bupp.wood_spoon_chef.data.repositories.MetaDataRepository
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FilterMenuState(
    val dishCategories: List<DishCategory>? = null,
    val selectedCategory: DishCategory? = null
)

sealed class FilterMenuEvent {
    data class SelectedCategory(val dishCategory: DishCategory?) : FilterMenuEvent()
    data class PassSelectedCategory(val dishCategory: DishCategory?): FilterMenuEvent()
}

class FilterMenuViewModel(
    private val metaDataRepository: MetaDataRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(FilterMenuState())
    val state: StateFlow<FilterMenuState> = _state

    private val _events = MutableSharedFlow<FilterMenuEvent>()
    val events: SharedFlow<FilterMenuEvent> = _events


    init {
        getDishCategories()
    }

    private fun getDishCategories() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val result = metaDataRepository.getDishCategories()
            updateCategoryList(result)
        } catch (e: Exception) {
            e.message
        }
    }

    private fun updateCategoryList(data: List<DishCategory>) {
        _state.update {
            it.copy(dishCategories = data)
        }
    }

    private fun setSelectedCategory(selectedCategory: DishCategory?){
        _state.update {
            it.copy(selectedCategory = selectedCategory)
        }
    }

    fun onCategorySelectedClick(selectedCategory: DishCategory?){
        viewModelScope.launch {
            _events.emit(FilterMenuEvent.SelectedCategory(selectedCategory))
            setSelectedCategory(selectedCategory)
        }
    }

    fun onApplyClick(){
        viewModelScope.launch {
            _events.emit(FilterMenuEvent.PassSelectedCategory(_state.value.selectedCategory))
        }
    }
}