package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs


import androidx.annotation.Keep
import androidx.lifecycle.viewModelScope
import biweekly.util.Frequency
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.rrules.RRuleTextFormatter
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.rrules.SimpleRRule
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.joda.time.DateTime
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
    data class Custom(val recurringRule: String? = null) :
        RecurringFrequency(RecurringFrequencyName.CUSTOM)
}

data class SlotRecurringState(
    val selectedFrequency: RecurringFrequency = RecurringFrequency.OneTime,
    val endsAt: Date? = DateTime(Date()).plusMonths(3).toDate()
)

sealed class SlotRecurringEvent {
    data class ShowCustomPicker(val recurringRule: String? = null) : SlotRecurringEvent()
    data class OnSave(val recurringRule: String?) : SlotRecurringEvent()
}

class SlotRecurringViewModel : BaseViewModel() {

    private val rRuleTextFormatter = RRuleTextFormatter()

    private val _state = MutableStateFlow(SlotRecurringState())
    val state: StateFlow<SlotRecurringState> = _state

    private val _events = MutableSharedFlow<SlotRecurringEvent>()
    val events: SharedFlow<SlotRecurringEvent> = _events

    fun onSaveClick() {
        viewModelScope.launch {
            _events.emit(SlotRecurringEvent.OnSave(mapStateToRule()))
        }
    }

    private fun mapStateToRule(): String? {
        val endsAt = _state.value.endsAt ?: return null // todo - validate end date
        val simpleRule = when (_state.value.selectedFrequency) {
            is RecurringFrequency.OneTime -> null
            RecurringFrequency.EveryDay -> {
                SimpleRRule(Frequency.DAILY, 1, endsAt, null)
            }
            RecurringFrequency.EveryWeek -> {
                SimpleRRule(Frequency.WEEKLY, 1, endsAt, null)
            }
            is RecurringFrequency.Custom -> {
                val recurringRule =
                    (_state.value.selectedFrequency as? RecurringFrequency.Custom)?.recurringRule
                recurringRule?.let {
                    rRuleTextFormatter.parseRRule(recurringRule)
                }
            }
        }

        return simpleRule?.let { rRuleTextFormatter.buildRRule(it) }
    }

    fun init(rrule: String?) {
        mapRuleToState(rrule)
    }

    private fun mapRuleToState(rrule: String?) {
        rrule?.let { recurringRule ->
            val simpleRule = rRuleTextFormatter.parseRRule(recurringRule)
            if (simpleRule == null) {
                //TODO Report invalid rule
                _state.update {
                    it.copy(selectedFrequency = RecurringFrequency.OneTime)
                }
            } else {
                when {
                    !simpleRule.days.isNullOrEmpty() || simpleRule.interval > 1 -> {
                        _state.update {
                            it.copy(selectedFrequency = RecurringFrequency.Custom(recurringRule))
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
                            else -> _state.update {
                                //TODO Report unsupported freq
                                it.copy(selectedFrequency = RecurringFrequency.OneTime)
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

    fun setRecurringFrequencyToCustom(recurringRule: String?){
        _state.update {
            it.copy(selectedFrequency = RecurringFrequency.Custom(recurringRule))
        }
    }

    private fun showCustomPicker(recurringRule: String?) {
        viewModelScope.launch {
            _events.emit(SlotRecurringEvent.ShowCustomPicker(recurringRule))
        }
    }
}