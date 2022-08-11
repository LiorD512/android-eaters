package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.rrules


import androidx.annotation.Keep
import androidx.lifecycle.viewModelScope
import biweekly.util.Frequency
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.CookingSlotReportEventUseCase
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.CookingSlotsDraftRepository
import com.bupp.wood_spoon_chef.utils.DateUtils
import com.eatwoodspoon.analytics.events.ChefsCookingSlotsEvent
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
    val humanReadableText: String? = null,
    val cookingSlotId: Long? = null
)

sealed class SlotRecurringEvent {
    data class Error(val message: String) : SlotRecurringEvent()
    data class ShowCustomPicker(val recurringRule: SimpleRRule? = null) : SlotRecurringEvent()
    data class OnSave(val recurringRule: String?) : SlotRecurringEvent()
    data class ShowEndAtDatePicker(
        val selectedDate: Long?, val endAtSelectedDate: Long?
    ) : SlotRecurringEvent()
}

class SlotRecurringViewModel(
    private val cookingSlotsDraftRepository: CookingSlotsDraftRepository,
    private val eventTracker: CookingSlotReportEventUseCase
) : BaseViewModel() {

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
                reportEvent { recurrence, _, rrule, mode, slotId ->
                    ChefsCookingSlotsEvent.RruleValueSelectedEvent(
                        recurrence,
                        rrule,
                        mode,
                        slotId
                    )
                }
            }
            else -> {
                with(_state.value) {
                    if (endsAt == null) {
                        _events.emit(SlotRecurringEvent.Error("Recurring slot must have Ends at date"))
                    } else {
                        _events.emit(SlotRecurringEvent.OnSave(mapStateToRule()))
                        reportEvent { recurrence, _, rrule, mode, slotId ->
                            ChefsCookingSlotsEvent.RruleValueSelectedEvent(
                                recurrence,
                                rrule,
                                mode,
                                slotId
                            )
                        }
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
        viewModelScope.launch {
            val originalCookingSlot =
                cookingSlotsDraftRepository.getDraft().first()?.originalCookingSlot
            setMode(rrule, originalCookingSlot)
        }
        reportEvent { _, _, rrule, mode, slotId ->
            ChefsCookingSlotsEvent.RruleScreenShownEvent(
                rrule,
                mode,
                slotId
            )
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
            _events.emit(
                SlotRecurringEvent.ShowEndAtDatePicker(
                    _state.value.selectedDate, _state.value.endsAt?.time
                )
            )
        }
    }

    fun setEndDate(endsAt: Date) {
        _state.update {
            it.copy(endsAt = endsAt)
        }
        reportEvent { _, until, rrule, mode, slotId ->
            ChefsCookingSlotsEvent.RruleUntilSelectedEvent(
                until,
                rrule,
                mode,
                slotId
            )
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

    private fun setMode(recurringRule: String?, originalCookingSlot: CookingSlot?) {
        _state.update {
            it.copy(
                cookingSlotId = originalCookingSlot?.id
            )
        }
    }

    private fun reportEvent(
        factory: (
            recurrence: ChefsCookingSlotsEvent.RruleValueSelectedEvent.RecurrencyValues,
            until: String,
            rrule: String?,
            mode: ChefsCookingSlotsEvent.ModeValues,
            slotId: Int?
        ) -> ChefsCookingSlotsEvent
    ) {
        val mode = if (_state.value.cookingSlotId != null) {
            ChefsCookingSlotsEvent.ModeValues.Edit
        } else {
            ChefsCookingSlotsEvent.ModeValues.New
        }
        val recurrence = when (_state.value.selectedFrequency) {
            is RecurringFrequency.Custom -> ChefsCookingSlotsEvent.RruleValueSelectedEvent.RecurrencyValues.custom
            RecurringFrequency.EveryDay -> ChefsCookingSlotsEvent.RruleValueSelectedEvent.RecurrencyValues.dayly
            RecurringFrequency.EveryWeek -> ChefsCookingSlotsEvent.RruleValueSelectedEvent.RecurrencyValues.weekly
            RecurringFrequency.OneTime -> ChefsCookingSlotsEvent.RruleValueSelectedEvent.RecurrencyValues.onetime
        }
        val until = _state.value.endsAt?.let { DateUtils.parseDateToDayMonthDay(it) } ?: ""
        val recurringRule = mapStateToRule()

        eventTracker.reportEvent(
            factory.invoke(
                recurrence,
                until,
                recurringRule,
                mode,
                _state.value.cookingSlotId?.toInt()
            )
        )
    }
}