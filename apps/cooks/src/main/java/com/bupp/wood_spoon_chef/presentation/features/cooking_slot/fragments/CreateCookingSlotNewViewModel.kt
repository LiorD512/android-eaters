package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.os.Parcelable
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
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call.SelectedHoursAndMinutes
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call.isZero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTime
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
    val selectedHoursAndMinutes: SelectedHoursAndMinutes? = null,
    val recurringRule: String? = null,
    val cookingSlotId: Long? = null,
    val errors: List<Errors> = emptyList(),
    val inProgress: Boolean = false,
    val recurringViewVisible: Boolean = true,
) {
    val isInEditMode = cookingSlotId != null
}

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
    private val getFormattedSelectedHoursAndMinutesUseCase: GetFormattedSelectedHoursAndMinutesUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(CreateCookingSlotNewState())
    val state: StateFlow<CreateCookingSlotNewState> = _state

    private val _events = MutableSharedFlow<CreateCookingSlotEvents>()
    val events: SharedFlow<CreateCookingSlotEvents> = _events

    init {
        viewModelScope.launch {
            cookingSlotsDraftRepository.getDraft().filterNotNull().collect { draft ->
                setSelectedDate(draft.selectedDate)
                setOperatingHours(draft.operatingHours)
                setRecurringRule(draft.recurringRule)
                setRecurringViewVisible(draft)
                setCookingSlotId(draft.originalCookingSlot?.id)
            }
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
        _state.update {
            it.copy(operatingHours = operatingHours)
        }
    }

    fun setRecurringRule(recurringRule: String?) {
        _state.update {
            it.copy(recurringRule = recurringRule)
        }
    }

    fun setSelectedHoursAndMinutes(selectedHoursAndMinutes: SelectedHoursAndMinutes) {
        _state.update {
            it.copy(
                selectedHoursAndMinutes = if (selectedHoursAndMinutes.isZero()) {
                    null
                } else {
                    selectedHoursAndMinutes
                }
            )
        }
    }

    fun formatSelectedLastCall(selectedHoursAndMinutes: SelectedHoursAndMinutes) =
        getFormattedSelectedHoursAndMinutesUseCase.execute(
            GetFormattedSelectedHoursAndMinutesUseCase.Params(selectedHoursAndMinutes)
        )

    fun onNextClick() {
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
                    lastCallForOrder = getSelectedLastCallDate().millis,
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
                    }
                }
            } catch (ex: Exception) {
                setInProgress(false)
                _events.emit(CreateCookingSlotEvents.Error(ex.message))
            }
        }
    }

    private fun getSelectedLastCallDate(): DateTime {
        var lastCallForOrderDate = DateTime(_state.value.operatingHours.startTime)
        _state.value.selectedHoursAndMinutes?.hours?.let {
            lastCallForOrderDate = lastCallForOrderDate.minusHours(it % 12)
        }
        _state.value.selectedHoursAndMinutes?.minutes?.let {
            lastCallForOrderDate = lastCallForOrderDate.minusMinutes(it % 60)
        }
        return lastCallForOrderDate
    }

    private suspend fun onValidationSuccess() {
        val currentDraft = cookingSlotsDraftRepository.getDraftValue() ?: return
        val updatedDraft = currentDraft.copy(
            selectedDate = state.value.selectedDate,
            operatingHours = state.value.operatingHours,
            lastCallForOrder = getSelectedLastCallDate().millis,
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
        return errors.isEmpty()
    }

    private fun setSelectedDate(selectedDate: Long?) {
        _state.update {
            it.copy(selectedDate = selectedDate)
        }
    }
}