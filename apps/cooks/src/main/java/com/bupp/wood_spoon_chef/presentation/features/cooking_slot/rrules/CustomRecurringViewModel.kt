package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.common

import android.content.Context
import androidx.annotation.Keep
import androidx.lifecycle.viewModelScope
import biweekly.util.DayOfWeek
import biweekly.util.Frequency
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.rrules.RRuleTextFormatter
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.rrules.SimpleRRule
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Keep
enum class CustomFrequency {
    DAY,
    WEEK
}

data class CustomRecurringState(
    val selectedDays: List<DayOfWeek> = emptyList(),
    val customFrequency: CustomFrequency = CustomFrequency.DAY,
    val customInterval: Int = 1,
    val recurringRule: String? = null
)

sealed class CustomRecurringEvent {
    data class Error(val message: String) : CustomRecurringEvent()
    data class SaveRecurringRule(val recurringRule: SimpleRRule) : CustomRecurringEvent()
    data class ShowCustomFrequencyPicker(
        val interval: Int, val frequency: String
    ) : CustomRecurringEvent()
}

class CustomRecurringViewModel : BaseViewModel() {

    private val _state = MutableStateFlow(CustomRecurringState())
    val state: StateFlow<CustomRecurringState> = _state

    private val _events = MutableSharedFlow<CustomRecurringEvent>()
    val events: SharedFlow<CustomRecurringEvent> = _events

    private val rRuleTextFormatter = RRuleTextFormatter()

    fun init(recurringRule: SimpleRRule?) {
        viewModelScope.launch {
            mapRuleToState(recurringRule)
        }
    }

    private suspend fun mapRuleToState(simpleRule: SimpleRRule?) {
        if (simpleRule == null) {
            _state.update {
                it.copy(selectedDays = emptyList())
            }
            _events.emit(
                CustomRecurringEvent.Error(
                    "Invalid recurring setting, please select again"
                )
            )
        } else {
            when (simpleRule.frequency) {
                Frequency.DAILY -> {
                    _state.update {
                        it.copy(customFrequency = CustomFrequency.DAY)
                    }
                }

                Frequency.WEEKLY -> {
                    _state.update {
                        it.copy(customFrequency = CustomFrequency.WEEK)
                    }
                }
                else -> {
                }
            }
        }
        _state.update {
            it.copy(customInterval = simpleRule?.interval ?: 1)
        }
        _state.update {
            it.copy(selectedDays = simpleRule?.days ?: emptyList())
        }
    }

    private fun mapStateToRule(): SimpleRRule {
        val simpleRule = when (_state.value.customFrequency) {
            CustomFrequency.DAY -> {
                SimpleRRule(
                    Frequency.DAILY,
                    _state.value.customInterval,
                    null,
                    days = emptyList()
                )
            }
            CustomFrequency.WEEK -> SimpleRRule(
                Frequency.WEEKLY,
                _state.value.customInterval,
                null,
                days = _state.value.selectedDays
            )
        }

        return simpleRule
    }

    fun onSaveClick() {
        viewModelScope.launch {
            _events.emit(CustomRecurringEvent.SaveRecurringRule(mapStateToRule()))
        }
    }

    fun onCustomClick() {
        viewModelScope.launch {
            _events.emit(
                CustomRecurringEvent.ShowCustomFrequencyPicker(
                    _state.value.customInterval,
                    _state.value.customFrequency.name
                )
            )
        }
    }

    private fun setSelectedDays(selectedDays: List<DayOfWeek>) {
        _state.update {
            it.copy(selectedDays = selectedDays)
        }
    }

    fun setFrequency(context: Context, frequency: String) {
        _state.update {
            it.copy(
                customFrequency = frequencyStringToCustomFrequency(context, frequency)
            )
        }
    }

    fun setInterval(interval: Int) {
        _state.update {
            it.copy(customInterval = interval)
        }
    }


    fun onDayClick(tag: String) {
        viewModelScope.launch {
            val dayOfWeek = DayOfWeek.valueOf(tag.uppercase())
            val newDaysList = mutableListOf<DayOfWeek>()
            newDaysList.addAll(_state.value.selectedDays)
            if (newDaysList.contains(dayOfWeek)) {
                newDaysList.remove(dayOfWeek)
            } else {
                newDaysList.add(dayOfWeek)
            }
            setSelectedDays(newDaysList)
            mapStateToRule()
        }
    }

    private fun frequencyStringToCustomFrequency(
        context: Context, frequency: String
    ): CustomFrequency {
        return when (frequency) {
            context.getString(R.string.day), context.getString(R.string.days) -> {
                CustomFrequency.DAY
            }
            else -> {
                CustomFrequency.WEEK
            }
        }
    }

}