package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call

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
            val hoursAndMinutes = listOfNotNull(hoursString, minutesString).joinToString ( " and " )
            return "$hoursAndMinutes before your cooking slot ends"
        }
        return null
    }
}