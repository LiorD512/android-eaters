package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTime
import org.joda.time.Days


@Parcelize
data class HoursAndMinutes(
    val hours: Int,
    val minutes: Int
) : Parcelable

@Parcelize
data class LastCall(
    val daysBefore: Int?,
    val time: HoursAndMinutes?
) : Parcelable {
    fun isNoLastCall() = daysBefore == null

    companion object
}

fun LastCall.toTimestampBasedOnEndDate(endDate: Long?): Long? {
    if (endDate == null || daysBefore == null || time == null) {
        return null
    }
    val endDateTime = DateTime(endDate)
    val lastCallDateTime =
        endDateTime.minusDays(daysBefore).withHourOfDay(time.hours).withMinuteOfHour(time.minutes)
    return lastCallDateTime.millis
}

fun LastCall.Companion.from(lastCallTimestamp: Long?, endsAtTimeStamp: Long?): LastCall? {
    if (lastCallTimestamp == null || endsAtTimeStamp == null) {
        return null
    }
    val lastCallDateTime = DateTime(lastCallTimestamp)
    val endsAtDateTime = DateTime(endsAtTimeStamp)
    val daysBefore = Days.daysBetween(lastCallDateTime, endsAtDateTime).days
    val hoursAndMinutes = HoursAndMinutes(
        hours = lastCallDateTime.hourOfDay,
        minutes = lastCallDateTime.minuteOfHour
    )
    return LastCall(daysBefore, hoursAndMinutes)
}

