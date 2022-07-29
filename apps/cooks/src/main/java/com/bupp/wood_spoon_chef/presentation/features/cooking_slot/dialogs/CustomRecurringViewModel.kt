package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import androidx.annotation.Keep
import androidx.lifecycle.viewModelScope
import biweekly.util.DayOfWeek
import biweekly.util.Frequency
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.rrules.RRuleTextFormatter
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.rrules.SimpleRRule
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Keep
enum class CustomFrequency {
    Days,
    Weeks,
    Day,
    Week
}


data class CustomRecurringState(
    val selectedDays: List<DayOfWeek> = emptyList(),
    val customFrequency: CustomFrequency = CustomFrequency.Day,
    val customInterval: Int = 1,
    val recurringRule: String? = null
)

sealed class CustomRecurringEvent {
    data class SaveRecurringRule(val recurringRule: String?) : CustomRecurringEvent()
    data class ShowCustomFrequencyPicker(val interval: Int) : CustomRecurringEvent()
}

class CustomRecurringViewModel : BaseViewModel() {

    private val _state = MutableStateFlow(CustomRecurringState())
    val state: StateFlow<CustomRecurringState> = _state

    private val _events = MutableSharedFlow<CustomRecurringEvent>()
    val events: SharedFlow<CustomRecurringEvent> = _events

    private val rRuleTextFormatter = RRuleTextFormatter()

    fun init(recurringRule: String?) {
        mapRuleToState(recurringRule)
    }

    private fun mapRuleToState(rrule: String?) {
        rrule?.let { rrule ->
            val simpleRule = rRuleTextFormatter.parseRRule(rrule)
            if (simpleRule == null) {
                //TODO Report invalid rule
                _state.update {
                    it.copy(selectedDays = emptyList())
                }
            } else {
                when (simpleRule.frequency) {
                    Frequency.DAILY -> {
                        if (simpleRule.interval == 1) {
                            _state.update {
                                it.copy(customFrequency = CustomFrequency.Day)
                            }
                        } else {
                            _state.update {
                                it.copy(customFrequency = CustomFrequency.Days)
                            }
                        }
                    }

                    Frequency.WEEKLY -> {
                        if (simpleRule.interval == 1) {
                            _state.update {
                                it.copy(customFrequency = CustomFrequency.Week)
                            }
                        } else {
                            _state.update {
                                it.copy(customFrequency = CustomFrequency.Weeks)
                            }
                        }
                    }
                    else -> {}
                }
            }
            _state.update {
                it.copy(customInterval = simpleRule?.interval ?: 1)
            }
            _state.update {
                it.copy(selectedDays = simpleRule?.days ?: emptyList())
            }
        }
    }

    fun mapStateToRule(): String? {
        val simpleRule = when (_state.value.customFrequency) {
            CustomFrequency.Days, CustomFrequency.Day -> {
                SimpleRRule(
                    Frequency.DAILY,
                    _state.value.customInterval,
                    null,
                    days = emptyList()
                )
            }
            CustomFrequency.Weeks, CustomFrequency.Week -> {
                SimpleRRule(
                    Frequency.WEEKLY,
                    _state.value.customInterval,
                    null,
                    days = _state.value.selectedDays
                )
            }
        }

        return simpleRule.let { rRuleTextFormatter.buildRRule(it) }
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
                    _state.value.customInterval
                )
            )
        }
    }

    private fun setSelectedDays(selectedDays: List<DayOfWeek>) {
        _state.update {
            it.copy(selectedDays = selectedDays)
        }
    }

    fun setFrequency(frequency: String) {
        _state.update {
            it.copy(customFrequency = CustomFrequency.valueOf(frequency))
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

}