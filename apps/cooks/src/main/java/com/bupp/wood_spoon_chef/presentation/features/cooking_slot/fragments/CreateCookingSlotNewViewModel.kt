package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper.CookingSlotStateMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTime
import java.lang.Exception

@Parcelize
data class OperatingHours(
    val startTime: Long?,
    val endTime: Long?
) : Parcelable

data class RecurringRule(
    val frequency: String?,
    val count: String?
)

data class CreateCookingSlotNewState(
    val selectedDate: Long? = null,
    val operatingHours: OperatingHours = OperatingHours(null, null),
    val lastCallForOrder: Long? = null,
    val recurringRule: RecurringRule? = null,
    val errors: List<Errors> = emptyList()
)

enum class Errors {
    OperatingHours
}

sealed class CreateCookingSlotEvents {
    data class Error(val message: String? = null) : CreateCookingSlotEvents()
    data class ShowOperatingHours(
        val operatingHours: OperatingHours? = null, val selectedDate: Long? = null) :
        CreateCookingSlotEvents()
    data class ShowLastCallForOrder(val lastCallForOrder: Long? = null) : CreateCookingSlotEvents()
    data class ShowRecurringRule(val recurringRule: RecurringRule? = null) :
        CreateCookingSlotEvents()
}

class CreateCookingSlotNewViewModel(
    private val cookingSlotFlowCoordinator: CookingSlotFlowCoordinator,
    private val stateMapper: CookingSlotStateMapper,
    private val cookingSlotRepository: CookingSlotRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(CreateCookingSlotNewState())
    val state: StateFlow<CreateCookingSlotNewState> = _state

    private val _events = MutableSharedFlow<CreateCookingSlotEvents>()
    val events: SharedFlow<CreateCookingSlotEvents> = _events

    fun setOperatingHours(operatingHours: OperatingHours) {
        _state.update {
            it.copy(operatingHours = operatingHours)
        }
    }

    fun setLastCallForOrders(lastCallForOrder: Long?) {
        _state.update {
            it.copy(lastCallForOrder = lastCallForOrder)
        }
    }

    fun setRecurringRule(recurringRule: RecurringRule?) {
        _state.update {
            it.copy(recurringRule = recurringRule)
        }
    }

    fun onNextClick() {
        if (validateInputs()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    when (val result = cookingSlotRepository.postCookingSlot(
                        stateMapper.mapStateToCreateCookingSlotRequest(_state.value)
                    )) {
                        is ResponseSuccess -> {
                            result.data?.let {
                                cookingSlotFlowCoordinator.next(CookingSlotFlowCoordinator.Step.OPEN_MENU_FRAGMENT, cookingSlot = it)
                            }
                        }
                        is ResponseError -> {
                            _events.emit(CreateCookingSlotEvents.Error(result.error.message))
                        }
                    }
                } catch (ex: Exception) {
                    _events.emit(CreateCookingSlotEvents.Error(ex.message))
                }
            }
        }
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


    fun onLastCallForOrderClick() {
        viewModelScope.launch {
            val lastCallForOrder = DateTime(_state.value.selectedDate).plusHours(9).millis
            _events.emit(CreateCookingSlotEvents.ShowLastCallForOrder(lastCallForOrder))
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private fun validateInputs(): Boolean {
        val errors = mutableListOf<Errors>()
        with(_state.value) {
            if (operatingHours.startTime == null) {
                errors.add(Errors.OperatingHours)
            }
        }
        _state.update { it.copy(errors = errors.toList()) }
        return errors.isEmpty()
    }

    fun setSelectedDate(selectedDate: Long?) {
        _state.update {
            it.copy(selectedDate = selectedDate)
        }
    }
}