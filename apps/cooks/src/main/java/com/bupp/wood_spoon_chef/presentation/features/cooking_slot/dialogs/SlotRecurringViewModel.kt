package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs


import androidx.annotation.Keep
import androidx.lifecycle.viewModelScope
import biweekly.util.Frequency
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.rrules.RRuleTextFormatter
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.rrules.SimpleRRule
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

@Keep
enum class RecurringFrequencyName {
    ONE_TIME,
    EVERY_DAY,
    EVERY_WEEK,
    CUSTOM
}

sealed class RecurringFrequency(open val name: RecurringFrequencyName) {
    object OneTime : RecurringFrequency(RecurringFrequencyName.ONE_TIME)
    object EveryDay : RecurringFrequency(RecurringFrequencyName.EVERY_DAY)
    object EveryWeek : RecurringFrequency(RecurringFrequencyName.EVERY_WEEK)
    data class Custom(val recurringRule: SimpleRRule? = null) :
        RecurringFrequency(RecurringFrequencyName.CUSTOM)
}

data class SlotRecurringState(
    val selectedDate: Long? = null,
    val selectedFrequency: RecurringFrequency = RecurringFrequency.OneTime,
    val endsAt: Date? = null,
    val humanReadableText: String? = null
)

sealed class SlotRecurringEvent {
    data class Error(val message: String) : SlotRecurringEvent()
    data class ShowCustomPicker(val recurringRule: SimpleRRule? = null) : SlotRecurringEvent()
    data class OnSave(val recurringRule: String?) : SlotRecurringEvent()
    data class ShowEndAtDatePicker(val selectedDate: Long?) : SlotRecurringEvent()
}

class SlotRecurringViewModel : BaseViewModel() {

    private val rRuleTextFormatter = RRuleTextFormatter()

    private val _state = MutableStateFlow(SlotRecurringState())
    val state: StateFlow<SlotRecurringState> = _state

    private val _events = MutableSharedFlow<SlotRecurringEvent>()
    val events: SharedFlow<SlotRecurringEvent> = _events

    fun onSaveClick() {
        viewModelScope.launch {
            validateInputs()
        }
    }

    private suspend fun validateInputs() {
        when (_state.value.selectedFrequency) {
            RecurringFrequency.OneTime -> {
                _events.emit(SlotRecurringEvent.OnSave(mapStateToRule()))
            }
            else -> {
                with(_state.value) {
                    if (endsAt == null) {
                        _events.emit(SlotRecurringEvent.Error("Recurring slot must have Ends at date"))
                    } else {
                        _events.emit(SlotRecurringEvent.OnSave(mapStateToRule()))
                    }
                }
            }
        }
    }

    private fun mapStateToSimpleRule(): SimpleRRule? {
        val endsAt = _state.value.endsAt
        val simpleRule = when (_state.value.selectedFrequency) {
            is RecurringFrequency.OneTime -> null
            RecurringFrequency.EveryDay -> {
                SimpleRRule(Frequency.DAILY, 1, null, null)
            }
            RecurringFrequency.EveryWeek -> {
                SimpleRRule(Frequency.WEEKLY, 1, null, null)
            }
            is RecurringFrequency.Custom -> {
                (_state.value.selectedFrequency as? RecurringFrequency.Custom)?.recurringRule
            }
        }?.copy(until = endsAt)

        return simpleRule
    }

    private fun mapStateToRule() = mapStateToSimpleRule()?.let { rRuleTextFormatter.buildRRule(it) }

    private fun mapStateToHumanReadable() =
        mapStateToSimpleRule()?.let { rRuleTextFormatter.formatToHumanReadable(it) }
            ?: "Cooking slot will occur only once"

    fun init(rrule: String?, selectedDate: Long?) {
        setSelectedDate(selectedDate)
        viewModelScope.launch {
            mapRuleToState(rrule)
        }
        viewModelScope.launch {
            _state.collect {
                _state.update { it.copy(humanReadableText = mapStateToHumanReadable()) }
            }
        }
    }

    private suspend fun mapRuleToState(rrule: String?) {
        rrule?.let { recurringRule ->
            val simpleRule = rRuleTextFormatter.parseRRule(recurringRule)
            if (simpleRule == null) {
                _state.update {
                    it.copy(selectedFrequency = RecurringFrequency.OneTime)
                }
                _events.emit(
                    SlotRecurringEvent.Error(
                        "Invalid recurring setting, please select again"
                    )
                )
            } else {
                when {
                    !simpleRule.days.isNullOrEmpty() || simpleRule.interval > 1 -> {
                        _state.update {
                            it.copy(selectedFrequency = RecurringFrequency.Custom(simpleRule))
                        }
                    }
                    else -> {
                        when (simpleRule.frequency) {
                            Frequency.DAILY -> _state.update {
                                it.copy(selectedFrequency = RecurringFrequency.EveryDay)
                            }
                            Frequency.WEEKLY -> _state.update {
                                it.copy(selectedFrequency = RecurringFrequency.EveryWeek)
                            }
                            else -> {
                                _state.update {
                                    it.copy(selectedFrequency = RecurringFrequency.OneTime)
                                }
                            }
                        }
                    }
                }

                _state.update {
                    it.copy(endsAt = simpleRule.until)
                }
            }
        }
    }

    fun onItemClicked(recurringFrequencyName: RecurringFrequencyName) {
        when (recurringFrequencyName) {
            RecurringFrequencyName.ONE_TIME -> {
                _state.update {
                    it.copy(selectedFrequency = RecurringFrequency.OneTime)
                }
            }
            RecurringFrequencyName.EVERY_DAY -> {
                _state.update {
                    it.copy(selectedFrequency = RecurringFrequency.EveryDay)
                }
            }
            RecurringFrequencyName.EVERY_WEEK -> {
                _state.update {
                    it.copy(selectedFrequency = RecurringFrequency.EveryWeek)
                }
            }
            RecurringFrequencyName.CUSTOM -> {
                val recurringRule =
                    (_state.value.selectedFrequency as? RecurringFrequency.Custom)?.recurringRule
                showCustomPicker(recurringRule)
            }
        }
    }

    fun setRecurringFrequencyToCustom(recurringRule: SimpleRRule) {
        _state.update {
            it.copy(selectedFrequency = RecurringFrequency.Custom(recurringRule))
        }
    }

    fun onEndsAtClick() {
        viewModelScope.launch {
            _events.emit(SlotRecurringEvent.ShowEndAtDatePicker(_state.value.selectedDate))
        }
    }

    fun setEndDate(endsAt: Date) {
        _state.update {
            it.copy(endsAt = endsAt)
        }
    }

    private fun setSelectedDate(selectedDate: Long?) {
        _state.update {
            it.copy(selectedDate = selectedDate)
        }
    }

    private fun showCustomPicker(recurringRule: SimpleRRule?) {
        viewModelScope.launch {
            _events.emit(SlotRecurringEvent.ShowCustomPicker(recurringRule))
        }
    }
}