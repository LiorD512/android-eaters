package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.data.local.model.CookingSlotDraftData
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotRequest
import com.bupp.wood_spoon_chef.data.remote.model.DishCategory
import com.bupp.wood_spoon_chef.data.remote.model.MenuItemRequest
import com.bupp.wood_spoon_chef.data.remote.model.SectionWithDishes
import com.bupp.wood_spoon_chef.data.remote.network.base.*
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.domain.GetSectionsWithDishesUseCase
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MenuDishItem
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.CookingSlotsDraftRepository
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper.AdapterModelCategoriesListMapper
import com.bupp.wood_spoon_chef.data.remote.model.request.CookingSlotStateToRequestMapper
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper.MenuDishItemToAdapterModelMapper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ReviewCookingSlotState {
    data class ScreenDataState(
        val selectedDate: Long? = null,
        val operatingHours: OperatingHours = OperatingHours(null, null),
        val lastCallForOrder: Long? = null,
        val recurringRule: String? = null,
        val menuItems: List<CookingSlotMenuAdapterItem> = emptyList(),
        @StringRes
        val btnActionStringRes: Int? = null
    ) : ReviewCookingSlotState()

    object Idle : ReviewCookingSlotState()
    object SlotCreatedSuccess : ReviewCookingSlotState()
    class Loading(val isLoading: Boolean = true) : ReviewCookingSlotState()
    class Error(val error: MTError) : ReviewCookingSlotState()
}

class CookingSlotReviewViewModel(
    private val getSectionsWithDishesUseCase: GetSectionsWithDishesUseCase,
    private val cookingSlotsDraftRepository: CookingSlotsDraftRepository,
    private val menuItemToAdapterModelMapper: MenuDishItemToAdapterModelMapper,
    private val adapterModelCategoriesListMapper: AdapterModelCategoriesListMapper,
    private val cookingSlotRepository: CookingSlotRepository,
    private val stateMapper: CookingSlotStateToRequestMapper
) : BaseViewModel() {

    private var isEditing: Boolean = false

    private val _state = MutableStateFlow<ReviewCookingSlotState>(ReviewCookingSlotState.Idle)
    val state: StateFlow<ReviewCookingSlotState> = _state

    init {
        getDraftMenuItems()
    }

    fun saveOrUpdateCookingSlot() = viewModelScope.launch {
        prepareAddSlotRequest().let { request ->
            if (isEditing) {
                updateCookingSlot(request)
            } else {
                saveCookingSlot(request)
            }
        }
    }

    private fun saveCookingSlot(request: CookingSlotRequest) = viewModelScope.launch {
        _state.emit(ReviewCookingSlotState.Loading())

        try {
            when (val result = cookingSlotRepository.postCookingSlot(request)) {
                is ResponseSuccess -> {
                    _state.emit(ReviewCookingSlotState.SlotCreatedSuccess)
                }
                is ResponseError -> {
                    _state.emit(
                        ReviewCookingSlotState.Error(
                            CustomError(
                                result.error.message ?: ""
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            _state.emit(ReviewCookingSlotState.Loading(false))
            e.message?.let {
                _state.emit(ReviewCookingSlotState.Error(CustomError(it)))
            }
        }
    }

    private fun updateCookingSlot(cookingSlotRequest: CookingSlotRequest) {
        viewModelScope.launch {
            _state.emit(ReviewCookingSlotState.Loading())

            try {
                cookingSlotsDraftRepository.getDraftValue()?.id?.let { draftSlotId ->
                    when (val result =
                        cookingSlotRepository.updateCookingSlot(draftSlotId, cookingSlotRequest)) {
                        is ResponseSuccess -> {
                            result.data?.let {
                                _state.emit(ReviewCookingSlotState.SlotCreatedSuccess)
                            }
                        }
                        is ResponseError -> {
                            _state.emit(ReviewCookingSlotState.Error(result.error))
                        }
                    }
                }

            } catch (e: java.lang.Exception) {
                e.message?.let { errorEvent.postRawValue(CustomError(it)) }
            }
            progressData.endProgress()
        }

    }

    private suspend fun prepareAddSlotRequest(): CookingSlotRequest =
        cookingSlotsDraftRepository.getDraft().map {
            val toList = it?.menuItems?.map { menuDishItem ->
                MenuItemRequest(
                    id = menuDishItem.menuItemId,
                    dishId = menuDishItem.dish?.id,
                    quantity = menuDishItem.quantity
                )
            }?.toList() ?: emptyList()

            stateMapper.mapStateToCreateCookingSlotRequest(
                startsTime = it?.operatingHours?.startTime,
                endTime = it?.operatingHours?.endTime,
                lastCallForOrder = it?.lastCallForOrder,
                recurringRule = it?.recurringRule,
            )
                .copy(submit = true)
                .copy(menuItems = toList)

        }.first()


    private fun getDraftMenuItems() {
        viewModelScope.launch {
            cookingSlotsDraftRepository.getDraftValue()?.let { draft ->
                isEditing = draft.isEditing

                _state.emit(
                    ReviewCookingSlotState.ScreenDataState(
                        selectedDate = draft.selectedDate,
                        operatingHours = draft.operatingHours,
                        lastCallForOrder = draft.lastCallForOrder,
                        recurringRule = draft.recurringRule,
                        btnActionStringRes = if (draft.isEditing) {
                            R.string.update_slot
                        } else {
                            R.string.save_cooking_slot
                        }

                    )
                )
            }

            try {
                val slotWithCategories: Flow<List<CookingSlotMenuAdapterItem>> =
                    getSectionsWithDishesUseCase.execute(GetSectionsWithDishesUseCase.Params())
                        .combineTransform(
                            flowOf(cookingSlotsDraftRepository.getDraftValue())
                        ) { responseSections, draftSlot ->
                            when (responseSections) {
                                is ResponseSuccess -> {
                                    responseSections.data?.let { sections ->
                                        val toList = inflateCategoriesToMenuDishItems(
                                            sections,
                                            draftSlot
                                        ).map {
                                            menuItemToAdapterModelMapper.map(it)
                                        }.toList()

                                        emit(adapterModelCategoriesListMapper.map(toList))
                                    }
                                }
                                is ResponseError -> {
                                    emit(emptyList<CookingSlotMenuAdapterItem>())
                                }
                            }
                        }

                setMenuItems(slotWithCategories.first())
            } catch (e: Exception) {
                e.message?.let {
                    _state.emit(ReviewCookingSlotState.Error(CustomError(it)))
                }
            }
        }
    }

    private fun inflateCategoriesToMenuDishItems(
        sections: SectionWithDishes,
        draftSlot: CookingSlotDraftData?
    ): List<MenuDishItem> = draftSlot?.menuItems?.map { menuDishItem ->
        sections.sections?.forEach { section ->
            section.dishIds?.contains(menuDishItem.dish?.id)?.let { isContains ->
                if (isContains) {
                    menuDishItem.dish?.category = DishCategory(
                        id = -1L,
                        name = section.title ?: ""
                    )
                }

            }
        }
        menuDishItem
    }?.toList() ?: emptyList()

    private fun setMenuItems(menuItems: List<CookingSlotMenuAdapterItem>) {
        _state.update {
            (_state.value as ReviewCookingSlotState.ScreenDataState)
                .copy(menuItems = menuItems)
        }
    }
}