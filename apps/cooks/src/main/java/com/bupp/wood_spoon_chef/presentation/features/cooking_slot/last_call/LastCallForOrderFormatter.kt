package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call

import android.icu.number.NumberRangeFormatter
import com.bupp.wood_spoon_chef.utils.DateUtils.prepareFormattedDateForHours
import org.joda.time.DateTime
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

object LastCallForOrderFormatter {

    fun formatLastCallForOrder(lastCallForOrder: Long?, endTime: Long?): String? {
        if (lastCallForOrder != null && endTime != null && lastCallForOrder != endTime) {
            val dateDiff = endTime - lastCallForOrder
            return formatLastCallForOrder(dateDiff)
        }
        return null
    }

    fun formatLastCallForOrder(dateDiff: Long?): String? {
        if (dateDiff != null) {
            val hours = dateDiff / TimeUnit.HOURS.toMillis(1)
            val minutes = (dateDiff % TimeUnit.HOURS.toMillis(1)) / TimeUnit.MINUTES.toMillis(1)

            val hoursString = hours.takeIf { it > 0 }?.let { "$it hr" }
            val minutesString = minutes.takeIf { it > 0 }?.let { "$it min" }
            val hoursAndMinutes = listOfNotNull(hoursString, minutesString).joinToString(" and ")
            return "$hoursAndMinutes before your cooking slot ends"
        }
        return null
    }

    fun formatLastCallForOrder(lastCall: LastCall?, fallback: String? = "No last call"): String? {
        return listOfNotNull(
            formatTextForDaysBefore(lastCall?.daysBefore, fallback),
            formatTextForTimeBefore(lastCall?.time)
        ).joinToString(" ")
    }

    fun formatTextForDaysBefore(daysBefore: Int?, fallback: String? = "No last call"): String? {
        return when (daysBefore) {
            null -> fallback
            0 -> "Same day"
            1 -> "One day before"
            2 -> "Two days before"
            else -> "$daysBefore days before"
        }
    }

    fun formatTextForTimeBefore(hoursAndMinutes: HoursAndMinutes?): String? {
        return when (hoursAndMinutes) {
            null -> null
            else -> "at ${
                DateTime(0)
                    .withHourOfDay(hoursAndMinutes.hours)
                    .withMinuteOfHour(hoursAndMinutes.minutes)
                    .prepareFormattedDateForHours()
            }"
        }
    }
}