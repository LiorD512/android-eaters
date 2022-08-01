package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_chef.domain.GetFormattedSelectedHoursAndMinutesUseCase
import kotlinx.coroutines.flow.*

class LastCallBottomSheetViewModel(
    private val getFormattedSelectedHoursAndMinutesUseCase: GetFormattedSelectedHoursAndMinutesUseCase,
) : ViewModel() {

    private val _selectedHoursAndMinutes = MutableStateFlow(
        SelectedHoursAndMinutes(0, 0)
    )

    val selectedHoursAndMinutes: StateFlow<SelectedHoursAndMinutes> =
        _selectedHoursAndMinutes

    val formattedSelectedHoursAndMinutesFlow: Flow<String> =
        _selectedHoursAndMinutes.map {
            getFormattedSelectedHoursAndMinutesUseCase.execute(
                GetFormattedSelectedHoursAndMinutesUseCase.Params(selectedHoursAndMinutes = it)
            )
        }

    val shouldShowWarningBottomSheetFlow: Flow<Boolean> =
        _selectedHoursAndMinutes.map { !(it.hours == 0 && it.minutes == 0) }

    fun setSelectedHoursAndMinutes(selectedHoursAndMinutes: SelectedHoursAndMinutes) {
        _selectedHoursAndMinutes.value = selectedHoursAndMinutes
    }

}