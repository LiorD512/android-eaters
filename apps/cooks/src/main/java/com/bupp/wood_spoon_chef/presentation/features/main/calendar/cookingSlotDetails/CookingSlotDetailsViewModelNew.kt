package com.bupp.wood_spoon_chef.presentation.features.main.calendar.cookingSlotDetails

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.network.base.CustomError
import com.bupp.wood_spoon_chef.data.remote.network.base.MTError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.domain.CancelCookingSlotUseCase
import com.bupp.wood_spoon_chef.domain.CookingSlotWithCategoriesInteractor
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.CookingSlotMenuAdapterItem
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper.AdapterModelCategoriesListMapper
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper.MenuDishItemToAdapterModelMapper
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper.MenuItemToMenuDishItemMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.Exception

sealed class CookingSlotDetailsState {
    object Idle : CookingSlotDetailsState()
    object Loading : CookingSlotDetailsState()
    object SlotCanceled : CookingSlotDetailsState()
    class Error(val error: MTError) : CookingSlotDetailsState()
    class Success(
        val cookingSlot: CookingSlot,
        val categoriesWithMenu: List<CookingSlotMenuAdapterItem>
    ) : CookingSlotDetailsState()
}

class CookingSlotDetailsViewModelNew(
    private val cancelCookingSlotUseCase: CancelCookingSlotUseCase,
    private val cookingSlotWithCategoriesInteractor: CookingSlotWithCategoriesInteractor,
    private val menuItemToMenuDishItemMapper: MenuItemToMenuDishItemMapper,
    private val menuItemToAdapterModelMapper: MenuDishItemToAdapterModelMapper,
    private val adapterModelCategoriesListMapper: AdapterModelCategoriesListMapper,
    ) : BaseViewModel() {

    var selectedCookingSlot: CookingSlot? = null

    private val _state = MutableStateFlow<CookingSlotDetailsState>(CookingSlotDetailsState.Idle)
    val state: StateFlow<CookingSlotDetailsState> = _state.asStateFlow()

    fun fetchSlotById(slotId: Long) = viewModelScope.launch {
        _state.emit(CookingSlotDetailsState.Loading)

        try {
            cookingSlotWithCategoriesInteractor.getCookingSlotWithCategories(slotId)
                .collect { slotWithCategories ->
                    when (slotWithCategories) {
                        is ResponseSuccess -> {
                            slotWithCategories.data?.let { slot ->
                                _state.emit(prepareOnSuccessListState(slot))
                            }
                        }
                        is ResponseError -> {
                            slotWithCategories.error.let {
                                _state.emit(CookingSlotDetailsState.Error(it))
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            e.message?.let {
                _state.emit(CookingSlotDetailsState.Error(error = CustomError(it)))
            }
        }
    }

    private fun prepareOnSuccessListState(slot: CookingSlot): CookingSlotDetailsState.Success {
        val adapterMenuItemList = slot.menuItems
            .map {
                menuItemToMenuDishItemMapper.map(it)
            }.map {
                menuItemToAdapterModelMapper.map(it)
            }.toList()

        val resultList = adapterModelCategoriesListMapper.map(adapterMenuItemList)

        return CookingSlotDetailsState.Success(
            categoriesWithMenu = resultList,
            cookingSlot = slot
        )
    }

    fun cancelCookingSlot(slotId: Long, singleSlot: Boolean?) = viewModelScope.launch {
        _state.emit(CookingSlotDetailsState.Loading)

        try {
            val params = CancelCookingSlotUseCase.Params(cookingSlotId = slotId)
            cancelCookingSlotUseCase.execute(params)
                .collect { result ->
                    when (result) {
                        is ResponseSuccess -> {
                            _state.emit(CookingSlotDetailsState.SlotCanceled)
                        }
                        is ResponseError -> {
                            result.error.let {
                                _state.emit(CookingSlotDetailsState.Error(it))
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            e.message?.let {
                _state.emit(CookingSlotDetailsState.Error(error = CustomError(it)))
            }
        }
    }

}
