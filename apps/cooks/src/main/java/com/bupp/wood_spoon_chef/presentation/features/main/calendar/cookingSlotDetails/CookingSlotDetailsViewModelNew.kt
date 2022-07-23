package com.bupp.wood_spoon_chef.presentation.features.main.calendar.cookingSlotDetails

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.MenuItem
import com.bupp.wood_spoon_chef.data.remote.network.base.CustomError
import com.bupp.wood_spoon_chef.data.remote.network.base.MTError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.domain.CancelCookingSlotUseCase
import com.bupp.wood_spoon_chef.domain.CookingSlotWithCategoriesInteractor
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.CookingSlotMenuAdapterItem
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
        val adapterMenuItemList = mapMenuItemToAdapterModelList(slot.menuItems)
        val resultList = prepareGroupedByCategoriesList(adapterMenuItemList)

        return CookingSlotDetailsState.Success(
            categoriesWithMenu = resultList,
            cookingSlot = slot
        )
    }

    private fun prepareGroupedByCategoriesList(
        adapterMenuItemList: List<CookingSlotMenuAdapterItem.MenuAdapterItem>
    ): MutableList<CookingSlotMenuAdapterItem> =
        mutableListOf<CookingSlotMenuAdapterItem>().apply {
            (adapterMenuItemList.groupBy { group ->
                group.menuItem.dish.category?.name
            } as LinkedHashMap)
                .forEach { (groupName, v) ->
                    groupName?.let {
                        add(
                            CookingSlotMenuAdapterItem.SectionHeaderAdapterItem(
                                sectionTitle = groupName,
                                type = CookingSlotMenuAdapterItem.CookingSlotMenuAdapterType.TYPE_SECTION_HEADER
                            )
                        )
                    }
                    v.forEach { item ->
                        add(
                            CookingSlotMenuAdapterItem.MenuAdapterItem(
                                menuItem = item.menuItem,
                                type = CookingSlotMenuAdapterItem.CookingSlotMenuAdapterType.TYPE_MENU_ITEM
                            )
                        )
                    }
                }
        }

    private fun mapMenuItemToAdapterModelList(
        menuItemList: List<MenuItem>
    ): List<CookingSlotMenuAdapterItem.MenuAdapterItem> = menuItemList.map { item ->
        CookingSlotMenuAdapterItem.MenuAdapterItem(
            item,
            CookingSlotMenuAdapterItem.CookingSlotMenuAdapterType.TYPE_MENU_ITEM
        )
    }.toList()

    fun cancelCookingSlot(slotId: Long) = viewModelScope.launch {
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
