package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.create_cooking_slot

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.local.model.CookingSlotDraftData
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowStep
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.CookingSlotsDraftRepository
import com.bupp.wood_spoon_chef.utils.extensions.getErrorMsgByType
import com.bupp.wood_spoon_chef.data.remote.model.request.CookingSlotRequestMapper
import com.bupp.wood_spoon_chef.domain.GetFormattedSelectedHoursAndMinutesUseCase
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.CookingSlotReportEventUseCase
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call.SelectedHoursAndMinutes
import com.eatwoodspoon.analytics.events.ChefsCookingSlotsEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.lang.Exception
import java.lang.IllegalArgumentException

@Parcelize
data class OperatingHours(
    val startTime: Long?,
    val endTime: Long?
) : Parcelable


data class CreateCookingSlotNewState(
    val selectedDate: Long? = null,
    val operatingHours: OperatingHours = OperatingHours(null, null),
    val recurringRule: String? = null,
    val cookingSlotId: Long? = null,
    val errors: List<Errors> = emptyList(),
    val inProgress: Boolean = false,
    val recurringViewVisible: Boolean = true,
    val lastCallForOrderShift: Long? = null
) {
    val isInEditMode = cookingSlotId != null
}

@Keep
enum class Errors {
    OperatingHours
}

sealed class CreateCookingSlotEvents {
    data class Error(val message: String? = null) : CreateCookingSlotEvents()
    data class ShowOperatingHours(
        val operatingHours: OperatingHours? = null, val selectedDate: Long? = null
    ) :
        CreateCookingSlotEvents()

    data class ShowLastCallForOrder(val lastCallForOrder: Long? = null) : CreateCookingSlotEvents()
    data class ShowRecurringRule(val recurringRule: String? = null, val selectedDate: Long) :
        CreateCookingSlotEvents()
}

class CreateCookingSlotNewViewModel(
    private val cookingSlotFlowCoordinator: CookingSlotFlowCoordinator,
    private val cookingSlotRequestMapper: CookingSlotRequestMapper,
    private val cookingSlotRepository: CookingSlotRepository,
    private val cookingSlotsDraftRepository: CookingSlotsDraftRepository,
    private val getFormattedSelectedHoursAndMinutesUseCase: GetFormattedSelectedHoursAndMinutesUseCase,
    private val eventTracker: CookingSlotReportEventUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(CreateCookingSlotNewState())
    val state: StateFlow<CreateCookingSlotNewState> = _state

    private val _events = MutableSharedFlow<CreateCookingSlotEvents>()
    val events: SharedFlow<CreateCookingSlotEvents> = _events

    init {
        viewModelScope.launch {
            cookingSlotsDraftRepository.getDraft().filterNotNull().collect { draft ->
                setSelectedDate(draft.selectedDate)
                setOperatingHoursInternal(draft.operatingHours)
                setRecurringRuleInternal(draft.recurringRule)
                setRecurringViewVisible(draft)
                setCookingSlotId(draft.originalCookingSlot?.id)
                setLastCallForOrderShift(draft.lastCallForOrderShift)
            }
        }
        reportEvent { mode, slotId ->
            ChefsCookingSlotsEvent.DetailsScreenOpenedEvent(mode, slotId)
        }
    }

    private fun setRecurringViewVisible(draft: CookingSlotDraftData) {
        _state.update {
            it.copy(recurringViewVisible = !draft.isEditing || draft.recurringRule != null)
        }
    }

    private fun setInProgress(inProgress: Boolean) {
        _state.update {
            it.copy(inProgress = inProgress)
        }
    }

    private fun setCookingSlotId(cookingSlotId: Long?) {
        _state.update {
            it.copy(cookingSlotId = cookingSlotId)
        }
    }

    fun setOperatingHours(operatingHours: OperatingHours) {
        setOperatingHoursInternal(operatingHours)
        reportEvent { mode, slotId ->
            ChefsCookingSlotsEvent.SetOperatingHoursSelectedEvent(
                operatingHours.startTime?.toString(),
                operatingHours.endTime?.toString(),
                mode,
                slotId
            )
        }
    }

    private fun setOperatingHoursInternal(operatingHours: OperatingHours) {
        _state.update {
            it.copy(operatingHours = operatingHours)
        }
    }

    fun setRecurringRule(recurringRule: String?) {
        setRecurringRuleInternal(recurringRule)
        reportEvent { mode, slotId ->
            ChefsCookingSlotsEvent.MakeSlotReccuringSelectedEvent(recurringRule, mode, slotId)
        }
    }

    private fun setRecurringRuleInternal(recurringRule: String?) {
        _state.update {
            it.copy(recurringRule = recurringRule)
        }
    }

    private fun setLastCallForOrderShift(lastCallForOrderShift: Long?) {
        _state.update {
            it.copy(lastCallForOrderShift = lastCallForOrderShift)
        }
    }


    fun setSelectedHoursAndMinutes(selectedHoursAndMinutes: SelectedHoursAndMinutes) {
        _state.update {
            it.copy(lastCallForOrderShift = selectedHoursAndMinutes.toTimeSpan())
        }
    }

    fun formatSelectedLastCall(selectedHoursAndMinutes: SelectedHoursAndMinutes?) =
        getFormattedSelectedHoursAndMinutesUseCase.execute(
            GetFormattedSelectedHoursAndMinutesUseCase.Params(selectedHoursAndMinutes)
        )

    fun onNextClick() {
        reportEvent { mode, slotId ->
            ChefsCookingSlotsEvent.DetailsScreenNextClickedEvent(mode, slotId)
        }
        setInProgress(true)
        if (validateInputs()) {
            validateIfPossibleCreateThisSlot()
        }
    }

    private fun validateIfPossibleCreateThisSlot() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = cookingSlotRequestMapper.mapCookingSlotToRequest(
                    startsTime = _state.value.operatingHours.startTime,
                    endTime = _state.value.operatingHours.endTime,
                    lastCallForOrderShift = _state.value.lastCallForOrderShift,
                    recurringRule = _state.value.recurringRule
                )

                val result = if(_state.value.isInEditMode) {
                    val slotId = _state.value.cookingSlotId ?: throw IllegalArgumentException("No slot id in eduit mode")
                    cookingSlotRepository.updateCookingSlot(slotId, request)
                } else {
                    cookingSlotRepository.postCookingSlot(request)
                }

                when (result) {
                    is ResponseSuccess -> {
                        setInProgress(false)
                        onValidationSuccess()
                    }
                    is ResponseError -> {
                        setInProgress(false)
                        _events.emit(CreateCookingSlotEvents.Error(result.error.getErrorMsgByType()))
                        reportEvent { mode, slotId ->
                            ChefsCookingSlotsEvent.DetailsServerValidationFailedEvent(
                                result.error.getErrorMsgByType(),
                                mode,
                                slotId
                            )
                        }
                    }
                }
            } catch (ex: Exception) {
                setInProgress(false)
                _events.emit(CreateCookingSlotEvents.Error(ex.message))
            }
        }
    }

    private suspend fun onValidationSuccess() {
        val currentDraft = cookingSlotsDraftRepository.getDraftValue() ?: return
        val updatedDraft = currentDraft.copy(
            selectedDate = state.value.selectedDate,
            operatingHours = state.value.operatingHours,
            lastCallForOrderShift = _state.value.lastCallForOrderShift,
            recurringRule = state.value.recurringRule
        )
        cookingSlotsDraftRepository.saveDraft(updatedDraft)
        cookingSlotFlowCoordinator.navigateNext(CookingSlotFlowStep.EDIT_DETAILS)
    }

    fun onOperatingHoursClick() {
        viewModelScope.launch {
            _events.emit(
                CreateCookingSlotEvents.ShowOperatingHours(
                    OperatingHours(
                        _state.value.operatingHours.startTime,
                        _state.value.operatingHours.endTime
                    ),
                    _state.value.selectedDate
                )
            )
        }
        reportEvent { mode, slotId ->
            ChefsCookingSlotsEvent.SetOperatingHoursClickedEvent(mode, slotId)
        }
    }

    fun onMakeSlotRecurringClick() {
        _state.value.selectedDate?.let {
            viewModelScope.launch {
                _events.emit(
                    CreateCookingSlotEvents.ShowRecurringRule(
                        _state.value.recurringRule,
                        it
                    )
                )
            }
        }
        reportEvent { mode, slotId ->
            ChefsCookingSlotsEvent.MakeSlotRecurringClickedEvent(mode, slotId)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private fun validateInputs(): Boolean {
        val errors = mutableListOf<Errors>()
        with(_state.value) {
            if (operatingHours.startTime == null) {
                errors.add(Errors.OperatingHours)
                setInProgress(false)
            }
        }
        _state.update { it.copy(errors = errors.toList()) }
        return errors.isEmpty().also { valid ->
            if (!valid) {
                reportEvent { mode, slotId ->
                    ChefsCookingSlotsEvent.DetailsLocalValidationFailedEvent(
                        errors.joinToString(",") { it.name },
                        mode,
                        slotId
                    )
                }
            }
        }
    }

    private fun setSelectedDate(selectedDate: Long?) {
        _state.update {
            it.copy(selectedDate = selectedDate)
        }
    }

    private fun reportEvent(factory: (mode: ChefsCookingSlotsEvent.ModeValues, slotId: Int?) -> ChefsCookingSlotsEvent) {
        val mode = if (_state.value.isInEditMode) {
            ChefsCookingSlotsEvent.ModeValues.Edit
        } else {
            ChefsCookingSlotsEvent.ModeValues.New
        }
        eventTracker.reportEvent(factory.invoke(mode, _state.value.cookingSlotId?.toInt()))
    }
}