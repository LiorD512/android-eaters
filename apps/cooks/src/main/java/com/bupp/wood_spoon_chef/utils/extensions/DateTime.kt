package com.bupp.wood_spoon_chef.utils.extensions

import com.bupp.wood_spoon_chef.utils.getLocal
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

fun DateTime.monthOfYearAsShortText(): String =
    this.monthOfYear().asShortText

fun DateTime.prepareFormattedDate(): String =
    DateTimeFormat.forPattern("EEEE, MMM dd, yyyy").withLocale(getLocal()).print(this)

fun DateTime.prepareFormattedDateForDateAndHour(): String =
    DateTimeFormat.forPattern("EEEE, MMM dd, yyyy, hh:mm aa").withLocale(getLocal()).print(this)

fun DateTime.prepareRangeOneMonth(): Pair<Long, Long> {
    val startDate: DateTime = this.withDayOfMonth(1)
    val endDate: DateTime = startDate.plusMonths(1)
    return Pair(startDate.millis / 1000, endDate.millis / 1000)
}

fun DateTime.prepareRangeOneDay(): Pair<Long, Long> {
    val startDate: DateTime = this.withTimeAtStartOfDay()
    val endDate: DateTime = startDate.plusDays(1).minusMinutes(1)
    return Pair(startDate.millis / 1000, endDate.millis / 1000)
}