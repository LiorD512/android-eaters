package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.Dish
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.data.repositories.DishRepository
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

data class MyDishesState(
    val dishes: List<Dish>? = null,
    val isListFiltered: Boolean? = false
)

sealed class MyDishesEvent {
    data class Error(val message: String? = null):MyDishesEvent()
    object ShowFilterMenu : MyDishesEvent()
    data class ShowEmptyState(val show: Boolean = false) : MyDishesEvent()
}

class MyDishesViewModel(
    private val cookingSlotRepository: CookingSlotRepository,
    private val dishRepository: DishRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(MyDishesState())
    val state: StateFlow<MyDishesState> = _state

    private val _events = MutableSharedFlow<MyDishesEvent>()
    val events: SharedFlow<MyDishesEvent> = _events

    init {
        getMyDishes()
    }

    private fun getMyDishes() = viewModelScope.launch(Dispatchers.IO) {
        try {
            when (val result = dishRepository.getMyDishes()) {
                is ResponseSuccess -> {
                    val dishes = result.data?.filter { it.isActive() }
                    updateDishList(dishes)
                }
                is ResponseError -> {
                    _events.emit(MyDishesEvent.Error(result.error.message))
                }
            }
        } catch (e: Exception) {
            _events.emit(MyDishesEvent.Error(e.message))
        }
    }

    fun filterList(input: String) {
        viewModelScope.launch {
            val filteredList = dishRepository.filterDishes(input)
            if (filteredList.isNullOrEmpty()) {
                _events.emit(MyDishesEvent.ShowEmptyState(true))
            } else {
                _events.emit(MyDishesEvent.ShowEmptyState(false))
            }
            updateDishList(filteredList)
        }
    }

    private fun updateDishList(data: List<Dish>?) {
        _state.update {
            it.copy(dishes = data)
        }
    }

    fun setIsListFiltered(isListFiltered: Boolean?) {
        _state.update {
            it.copy(isListFiltered = isListFiltered)
        }
    }

    fun onFilterClick() {
        viewModelScope.launch {
            _events.emit(MyDishesEvent.ShowFilterMenu)
        }
    }
}