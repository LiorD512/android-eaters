package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.review_cooking_slot

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
import com.bupp.wood_spoon_chef.data.remote.model.request.CookingSlotRequestMapper
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.CookingSlotReportEventUseCase
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.cooking_slot_menu.CookingSlotMenuAdapterItem
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.create_cooking_slot.OperatingHours
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call.LastCall
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper.MenuDishItemToAdapterModelMapper
import com.bupp.wood_spoon_chef.utils.extensions.getErrorMsgByType
import com.eatwoodspoon.analytics.events.ChefsCookingSlotsEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ReviewCookingSlotState {
    data class ScreenDataState(
        val selectedDate: Long? = null,
        val operatingHours: OperatingHours = OperatingHours(null, null),
        val lastCallForOrder: LastCall? = null,
        val recurringRule: String? = null,
        val menuItems: List<CookingSlotMenuAdapterItem> = emptyList(),
        @StringRes
        val btnActionStringRes: Int? = null
    ) : ReviewCookingSlotState()

    object Idle : ReviewCookingSlotState()
    class Loading(val isLoading: Boolean = true) : ReviewCookingSlotState()
    class Error(val error: MTError) : ReviewCookingSlotState()
}

sealed class ReviewCookingSlotEvents {
    object ShowUpdateDetachDialog : ReviewCookingSlotEvents()
    object SlotCreatedSuccess : ReviewCookingSlotEvents()
}

class CookingSlotReviewViewModel(
    private val getSectionsWithDishesUseCase: GetSectionsWithDishesUseCase,
    private val cookingSlotsDraftRepository: CookingSlotsDraftRepository,
    private val menuItemToAdapterModelMapper: MenuDishItemToAdapterModelMapper,
    private val adapterModelCategoriesListMapper: AdapterModelCategoriesListMapper,
    private val cookingSlotRepository: CookingSlotRepository,
    private val stateMapper: CookingSlotRequestMapper,
    private val eventTracker: CookingSlotReportEventUseCase
) : BaseViewModel() {

    private var isEditing: Boolean = false
    private var cookingSlotId: Long? = null
    private var recurringRule: String? = null

    private val _state = MutableStateFlow<ReviewCookingSlotState>(ReviewCookingSlotState.Idle)
    val state: StateFlow<ReviewCookingSlotState> = _state

    private val _events = MutableSharedFlow<ReviewCookingSlotEvents>()
    val events: SharedFlow<ReviewCookingSlotEvents> = _events

    init {
        getDraftMenuItems()
        reportEvent { mode, slotId -> ChefsCookingSlotsEvent.ReviewScreenOpenedEvent(mode, slotId) }
    }

    fun saveOrUpdateCookingSlot() = viewModelScope.launch {
        reportEvent { mode, slotId ->
            ChefsCookingSlotsEvent.ReviewScreenNextClickedEvent(
                mode,
                slotId
            )
        }

        if (isEditing) {
            if (recurringRule.isNullOrEmpty()) {
                updateCookingSlot(
                    prepareAddSlotRequest().copy(
                        detach = true
                    )
                )
            } else {
                _events.emit(ReviewCookingSlotEvents.ShowUpdateDetachDialog)
                reportEvent { mode, slotId ->
                    ChefsCookingSlotsEvent.ReviewScreenUpdateDialogShownEvent(
                        mode,
                        slotId
                    )
                }
                return@launch
            }
        }else{
            saveCookingSlot(prepareAddSlotRequest())
        }
    }

    fun onDetachDialogResult(singleEvent: Boolean) = viewModelScope.launch {
        reportEvent { mode, slotId ->
            ChefsCookingSlotsEvent.ReviewScreenUpdateDialogResultEvent(
                singleEvent,
                mode,
                slotId
            )
        }
        updateCookingSlot(
            prepareAddSlotRequest().copy(
                detach = singleEvent
            )
        )
    }

    private fun saveCookingSlot(request: CookingSlotRequest) = viewModelScope.launch {
        _state.emit(ReviewCookingSlotState.Loading())

        try {
            when (val result = cookingSlotRepository.postCookingSlot(request)) {
                is ResponseSuccess -> {
                    _events.emit(ReviewCookingSlotEvents.SlotCreatedSuccess)
                }
                is ResponseError -> {
                    _state.emit(
                        ReviewCookingSlotState.Error(
                            CustomError(
                                result.error.getErrorMsgByType()
                            )
                        )
                    )
                    reportSaveResponseError(result)
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
                            _events.emit(ReviewCookingSlotEvents.SlotCreatedSuccess)
                        }
                        is ResponseError -> {
                            _state.emit(ReviewCookingSlotState.Error(result.error))
                            reportSaveResponseError(result)
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
        cookingSlotsDraftRepository.getDraft().map { cookingSlotDraft ->
            val existingMenuItems = cookingSlotDraft?.menuItems?.map { menuDishItem ->
                MenuItemRequest(
                    id = menuDishItem.menuItemId,
                    dishId = menuDishItem.dish?.id,
                    quantity = menuDishItem.quantity
                )
            }?.toList() ?: emptyList()

            val menuItemsToDelete =
                cookingSlotDraft?.originalCookingSlot?.menuItems?.filter { menuItem ->
                    !existingMenuItems.map { it.id }.contains(menuItem.id)
                }?.map { menuItemToDelete ->
                    MenuItemRequest(
                        id = menuItemToDelete.id,
                        _destroy = true
                    )
                }?.toList() ?: emptyList()

            stateMapper.mapCookingSlotToRequest(
                startsTime = cookingSlotDraft?.operatingHours?.startTime,
                endTime = cookingSlotDraft?.operatingHours?.endTime,
                lastCallForOrder = cookingSlotDraft?.lastCallForOrder,
                recurringRule = cookingSlotDraft?.recurringRule,
            )
                .copy(submit = true)
                .copy(menuItems = existingMenuItems + menuItemsToDelete)

        }.first()


    private fun getDraftMenuItems() {
        viewModelScope.launch {
            cookingSlotsDraftRepository.getDraftValue()?.let { draft ->
                isEditing = draft.isEditing
                cookingSlotId = draft.originalCookingSlot?.id
                recurringRule = draft.recurringRule

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

    private fun reportEvent(factory: (mode: ChefsCookingSlotsEvent.ModeValues, slotId: Int?) -> ChefsCookingSlotsEvent) {
        val mode = if (isEditing) {
            ChefsCookingSlotsEvent.ModeValues.Edit
        } else {
            ChefsCookingSlotsEvent.ModeValues.New
        }
        eventTracker.reportEvent(factory.invoke(mode, cookingSlotId?.toInt()))
    }

    private fun reportSaveResponseError(result: ResponseError<*>) {
        reportEvent { mode, slotId ->
            ChefsCookingSlotsEvent.ReviewScreenSaveErrorEvent(
                error_code = result.error.errorCode(),
                error_description = result.error.getErrorMsgByType(),
                mode = mode,
                slot_id = slotId
            )
        }
    }
}