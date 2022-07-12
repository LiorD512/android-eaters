package com.bupp.wood_spoon_chef.utils

import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotSlim
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {


    fun parseDate(date: Date?): String {
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
        return sdf.format(date?.time)
    }

    fun DateTime.prepareFormattedDateForHours(): String =
        DateTimeFormat.forPattern("hh:mm aa").print(this)

    fun parseDateToMonthYear(date: Date): String {
        val sdf = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
        return sdf.format(date.time)
    }

    fun parseDateToDayDateHour(date: Date): String {
        //mon, 24, 4pm
        val sdf = SimpleDateFormat("EE, dd, h:mma", Locale.ENGLISH)
        return sdf.format(date.time)
    }

    fun parseDateToHour(date: Date): String {
        //4pm
        val sdf = SimpleDateFormat("h:mma", Locale.ENGLISH)
        return sdf.format(date.time)
    }

    fun parseDateToDay(date: Date): String {
        //Tuesday,08th
        val sdf = SimpleDateFormat("EEEE, dd", Locale.ENGLISH)
        val day = SimpleDateFormat("dd", Locale.ENGLISH)
        val day2 = day.format(date.time).toInt()
        val dayWithSuffix = getDayOfMonthSuffix(day2)
        return "${sdf.format(date.time)}$dayWithSuffix"
    }

    fun parseDateToDayMonthDay(date: Date): String {
        //Tuesday, Jan 08th
        val sdf = SimpleDateFormat("EEEE, MMMM dd", Locale.ENGLISH)
        val day = SimpleDateFormat("dd", Locale.ENGLISH)
        val day2 = day.format(date.time).toInt()
        val dayWithSuffix = getDayOfMonthSuffix(day2)
        return "${sdf.format(date.time)}$dayWithSuffix"
    }

    fun parseDateToMonthAndYear(date: Date): String {
        //Jan 2012
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
        return sdf.format(date.time)
    }

    private fun getDayOfMonthSuffix(n: Int): String {
        if (n in 11..13) {
            return "th"
        }
        return when (n % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

    fun parseDateIfThisWeek(date: Date): String {
        return if (isDateInCurrentWeek(date)) {
            //Monday, May 20, 2019
            val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.ENGLISH)
            sdf.format(date.time)
        } else {
            //mon, 24, 4pm
            val sdf = SimpleDateFormat("EE, dd, h:mma", Locale.ENGLISH)
            sdf.format(date.time)
        }
    }

    private fun isDateInCurrentWeek(date: Date): Boolean {
        val currentCalendar = Calendar.getInstance()
        val week = currentCalendar.get(Calendar.WEEK_OF_YEAR)
        val year = currentCalendar.get(Calendar.YEAR)
        val targetCalendar = Calendar.getInstance()
        targetCalendar.time = date
        val targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR)
        val targetYear = targetCalendar.get(Calendar.YEAR)
        return week == targetWeek && year == targetYear
    }


    fun parseDateToTime(date: Date?): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.ENGLISH)
        if (date != null)
            return sdf.format(date.time)
        return ""
    }

    fun parseTwoDates(startsAtDate: Date?, endsAtDate: Date?): String {
//        val sdf = SimpleDateFormat("HH:mm a")
        val sdf = SimpleDateFormat("h:mma", Locale.ENGLISH)
        if (startsAtDate != null && endsAtDate != null) {
//            val start = sdf.format(startsAtDate?.time)
            val end = sdf.format(endsAtDate.time)
            val dayDateHour = parseDateToDayDateHour(startsAtDate)
            return "$dayDateHour - $end"
        }
        return ""
    }

    fun parseTwoDatesToHours(startsAtDate: Date?, endsAtDate: Date?): String {
//       X:XX AM/PM - Y:YY AM/PM
        val sdf = SimpleDateFormat("h:mma", Locale.ENGLISH)
        if (startsAtDate != null && endsAtDate != null) {
            val start = sdf.format(startsAtDate.time)
            val end = sdf.format(endsAtDate.time)
            return "$start - $end"
        }
        return ""
    }

    fun parseTwoSimpleDates(startsAtDate: Date?, endsAtDate: Date?): String {
//        val sdf = SimpleDateFormat("HH:mm a")
        val sdf = SimpleDateFormat("h:mma", Locale.ENGLISH)
        if (startsAtDate != null && endsAtDate != null) {
            val start = sdf.format(startsAtDate.time)
            val end = sdf.format(endsAtDate.time)
            return "$start - $end"
        }
        return ""
    }

    fun parseMilliToLength(milli: Long): String {
        //01:22
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(milli),
            TimeUnit.MILLISECONDS.toSeconds(milli) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milli))
        )
    }


    fun parseDateToDate(date: Date?): String {
        //05.04.19
        val sdf = SimpleDateFormat("dd.MM.yy", Locale.ENGLISH)
        date?.let {
            return sdf.format(date.time)
        }
        return ""
    }

    fun getCookingSlotStartString(cookingSlot: CookingSlot): String {
        val date = cookingSlot.startsAt
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = date.time
        val now = Calendar.getInstance()
        return if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            "Today, ${parseDateToHour(date)}"
        } else {
            parseDateIfThisWeek(date)
        }
    }

    fun getCookingSlotStartString(cookingSlot: CookingSlotSlim): String {
        val date = cookingSlot.startsAt
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = date.time
        val now = Calendar.getInstance()
        return if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            "Today, ${parseDateToHour(date)}"
        } else {
            parseDateIfThisWeek(date)
        }
    }

    fun compareToDay(date1: Date?, date2: Date?): Int {
        if (date1 == null || date2 == null) {
            return 0
        }
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
        return sdf.format(date1).compareTo(sdf.format(date2))
    }

    fun isSameDay(date1: Date?, date2: Date?): Boolean {
        date1?.let {
            date2?.let {
                val fmt = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
                return fmt.format(date1) == fmt.format(date2)
            }
        }
        return false
    }
}


fun Date.hours(): Int {
    val sdf = SimpleDateFormat("H", Locale.ENGLISH)
    return sdf.format(this.time).toIntOrNull() ?: 0
}

fun Date.minutes(): Int {
    val sdf = SimpleDateFormat("m", Locale.ENGLISH)
    return sdf.format(this.time).toIntOrNull() ?: 0
}

fun String.parseStringToTime(): Date? {
    val formatter = SimpleDateFormat("h:mma", Locale.ENGLISH)
    return formatter.parse(this.replace(" ", ""))
}
