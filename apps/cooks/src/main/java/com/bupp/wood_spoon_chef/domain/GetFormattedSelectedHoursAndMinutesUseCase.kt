package com.bupp.wood_spoon_chef.domain

import com.bupp.wood_spoon_chef.domain.comon.UseCase
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call.HoursAndMinutes

class GetFormattedSelectedHoursAndMinutesUseCase(
    private val formattedStringForZeroHours: String,
) : UseCase<String, GetFormattedSelectedHoursAndMinutesUseCase.Params> {

    data class Params(
        val selectedHoursAndMinutes: HoursAndMinutes?
    )

    override fun execute(params: Params): String = setSelectedTime(params.selectedHoursAndMinutes)

    private fun setSelectedTime(selectedHoursAndMinutes: HoursAndMinutes?): String {
        selectedHoursAndMinutes?.let {
            var formatSubtitle = String.format(
                formattedStringForZeroHours,
                "${selectedHoursAndMinutes.hours} hours"
            )

            if (selectedHoursAndMinutes.minutes != 0) {
                formatSubtitle = String.format(
                    formattedStringForZeroHours,
                    "${selectedHoursAndMinutes.hours} hr and ${selectedHoursAndMinutes.minutes} min"

                )
            }
            return formatSubtitle
        }
        return ""
    }
}