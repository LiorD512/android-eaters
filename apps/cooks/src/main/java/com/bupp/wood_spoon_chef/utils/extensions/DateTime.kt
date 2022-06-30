package com.bupp.wood_spoon_chef.utils.extensions

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

fun DateTime.monthOfYearAsShortText(): String =
    this.monthOfYear().asShortText

fun DateTime.prepareFormattedDate(): String =
    DateTimeFormat.forPattern("EEEE, MMM dd, yyyy").print(this)

fun DateTime.prepareRangeOneMonth(): Pair<Long, Long> {
    val startDate: DateTime = this.withDayOfMonth(1)
    val endDate: DateTime = startDate.plusMonths(1).minusDays(1)
    return Pair(startDate.millis / 1000, endDate.millis / 1000)
}

fun DateTime.prepareRangeOneDay(): Pair<Long, Long> {
    val startDate: DateTime = this.withTimeAtStartOfDay()
    val endDate: DateTime = startDate.plusDays(1).minusMinutes(1)
    return Pair(startDate.millis / 1000, endDate.millis / 1000)
}