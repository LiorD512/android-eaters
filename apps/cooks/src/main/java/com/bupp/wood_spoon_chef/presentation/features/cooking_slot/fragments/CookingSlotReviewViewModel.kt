package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.DishCategory
import com.bupp.wood_spoon_chef.data.remote.model.SectionWithDishes
import com.bupp.wood_spoon_chef.data.remote.network.base.*
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.domain.GetSectionsWithDishesUseCase
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MenuDishItem
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.CookingSlotsDraftRepository
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotDraft
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper.AdapterModelCategoriesListMapper
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper.CookingSlotStateMapper
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper.MenuDishItemToAdapterModelMapper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ReviewCookingSlotState {
    data class ScreenDataState(
        val selectedDate: Long? = null,
        val operatingHours: OperatingHours = OperatingHours(null, null),
        val lastCallForOrder: Long? = null,
        val recurringRule: RecurringRule? = null,
        val menuItems: List<CookingSlotMenuAdapterItem> = emptyList(),
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
    private val stateMapper: CookingSlotStateMapper
) : BaseViewModel() {

    private val _state = MutableStateFlow<ReviewCookingSlotState>(ReviewCookingSlotState.Idle)
    val state: StateFlow<ReviewCookingSlotState> = _state

    init {
        getDraft()
    }

    fun saveCookingSlot() = viewModelScope.launch {
        _state.emit(ReviewCookingSlotState.Loading())

        try {
            val request = cookingSlotsDraftRepository.getDraft().map {
                stateMapper.mapStateToCreateCookingSlotRequest(
                    startsTime = it?.operatingHours?.startTime,
                    endTime = it?.operatingHours?.endTime,
                    lastCallForOrder = it?.lastCallForOrder,
                    recurringRule = it?.recurringRule
                )
            }.first()

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

    private fun getDraft() {
        viewModelScope.launch {
            cookingSlotsDraftRepository.getDraftValue()?.let { draft ->
                _state.emit(
                    ReviewCookingSlotState.ScreenDataState(
                        selectedDate = draft.selectedDate,
                        operatingHours = draft.operatingHours,
                        lastCallForOrder = draft.lastCallForOrder,
                        recurringRule = draft.recurringRule
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
        draftSlot: CookingSlotDraft?
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