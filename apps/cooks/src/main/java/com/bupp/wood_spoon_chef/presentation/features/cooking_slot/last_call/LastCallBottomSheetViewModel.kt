package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LastCallState(
    val lastCall: LastCall
) {
    val timePickerVisible = lastCall.daysBefore != null
}

sealed class LastCallEvents {
    object ShowDayPicker : LastCallEvents()
    data class ShowTimePicker(val time: HoursAndMinutes?) : LastCallEvents()
    object ShowWarningDialog : LastCallEvents()
    data class SetResult(val result: LastCall) : LastCallEvents()
}

class LastCallBottomSheetViewModel : ViewModel() {

    private val _state = MutableStateFlow(LastCallState(LastCall(null, null)))
    val state: StateFlow<LastCallState> = _state

    private val _events = MutableSharedFlow<LastCallEvents>()
    val events: SharedFlow<LastCallEvents> = _events

    fun onDaysBeforeSelected(daysBefore: Int?) = viewModelScope.launch {
        _state.update {
            it.copy(lastCall = it.lastCall.copy(daysBefore = daysBefore))
        }
    }

    fun onWarningAccepted() = viewModelScope.launch {
        _events.emit(LastCallEvents.SetResult(state.value.lastCall))
    }

    fun onSaveClicked() = if (_state.value.lastCall.isNoLastCall()){
        onWarningAccepted()
    }else{
        viewModelScope.launch {
            _events.emit(LastCallEvents.ShowWarningDialog)
        }
    }

    fun onSelectDayClicked() = viewModelScope.launch {
        _events.emit(LastCallEvents.ShowDayPicker)
    }

    fun onSelectTimeClicked() = viewModelScope.launch {
        _events.emit(LastCallEvents.ShowTimePicker(state.value.lastCall.time))
    }

    fun setSelectedTime(hoursAndMinutes: HoursAndMinutes) = viewModelScope.launch {
        _state.update {
            it.copy(lastCall = it.lastCall.copy(time = hoursAndMinutes))
        }
    }

    fun setInitialValue(lastCall: LastCall) = viewModelScope.launch {
        _state.update {
            it.copy(lastCall = lastCall)
        }
    }
}
