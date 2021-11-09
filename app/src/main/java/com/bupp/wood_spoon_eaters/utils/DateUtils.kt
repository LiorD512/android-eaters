package com.bupp.wood_spoon_eaters.utils

import com.bupp.wood_spoon_eaters.utils.DateUtils.parseDateToDate
import com.google.android.gms.common.internal.Preconditions.checkArgument
import java.text.SimpleDateFormat
import java.util.*


object DateUtils {


    fun parseDateToStartToEnd(startDate: Date, endDate: Date): String {
        //10:30 AM - 4:00 PM
        val sdf = SimpleDateFormat("h:mma")
        val start = sdf.format(startDate.time)
        val end = sdf.format(endDate.time)
        return "$start - $end"
    }

    fun parseDateToFullDate(orderDate: Date?): String {
        //Aug 2, 10:30 AM
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mma", Locale.getDefault())
        val date = dateFormat.format(orderDate)
        val time = timeFormat.format(orderDate)
        return "$date, $time"
    }

    fun parseDateToOrderAtStr(orderDate: Date?): String {
        //Tue Jul 7th, 2020 at 9:54 PM
        val dateFormat = SimpleDateFormat("EEE MMM", Locale.getDefault())
        val dayFormat = SimpleDateFormat("d", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = dateFormat.format(orderDate)
        val day = dayFormat.format(orderDate)
        val daySuffix = getDayOfMonthSuffix(day.toInt())
        val time = timeFormat.format(orderDate)
        val year = yearFormat.format(orderDate)
        return "$date $day$daySuffix, $year at $time"
    }

    private fun getDayOfMonthSuffix(n: Int): String {
        checkArgument(n in 1..31, "illegal day of month: $n")
        return if (n in 11..13) {
            "th"
        } else when (n % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

    fun parseDateToStartAndEnd(startDate: Date, endDate: Date): String {
        //Mon, Jun 9 2pm - 5pm
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val date = dateFormat.format(startDate)
        val timeFormat1 = SimpleDateFormat("h:mm")
        val timeFormat2 = SimpleDateFormat("h:mm a")
        val startTime = timeFormat1.format(startDate).lowercase(Locale.getDefault())
        val endTime = timeFormat2.format(endDate).lowercase(Locale.getDefault())
        return "$date  $startTime - $endTime"
    }

    fun parseDateToUsDate(date: Date?): String {
        //August 2, 2019
        date?.let{
            val sdf = SimpleDateFormat("MMMM dd, yyyy")
            return sdf.format(date.time)
        }
        return ""
    }

    fun parseDateToMonthTime(date: Date?): String {
        //Aug 2, 10:40 pm
        date?.let{
            val sdf = SimpleDateFormat("MMM dd")
            return "${sdf.format(date.time)}, ${parseDateToUsTime(it)}"
        }
        return ""
    }

    fun parseDateToFullDayDate(date: Date): String {
        //Fri, Feb 12
        val sdf = SimpleDateFormat("EEEE, MMM dd")
        return sdf.format(date.time)
    }

    fun parseDateToDayName(date: Date?): String {
        //Friday
        date?.let{
            val sdf = SimpleDateFormat("EEEE")
            return sdf.format(date.time)
        }
        return ""
    }
    fun parseDateToDayDateNumber(date: Date): String {
        //Fri, Feb 12
        val sdf = SimpleDateFormat("EEE, MMM d")
        return sdf.format(date.time)
    }

    fun parseDateToDayDateNumberOrToday(date: Date): String {
        //Fri, Feb 12 / Today
        if(isToday(date)){
            return "Today"
        }else{
            return parseDateToDayDateNumber(date)
        }
    }

    fun parseDateToDayDateAndTime(date: Date): String {
        //Fri, Feb 12, 4:30 PM - 5:00 PM
        val sdf = SimpleDateFormat("EE, MMM dd")
        val dateVal = sdf.format(date.time)
        val hours = parseDateHalfHourInterval(date)
        return "$dateVal, $hours"
    }

    fun parseDateToFullTime(date: Date?): String {
        //16
        date?.let{
            val sdf = SimpleDateFormat("HH")
            return sdf.format(date.time)
        }
        return ""
    }

    fun parseDateHalfHourInterval(date: Date): String {
        //4:30 PM - 5:00 PM
        val c = Calendar.getInstance()
        c.time = date
        c.add(Calendar.MINUTE, 30)

        val sdf1 = SimpleDateFormat("h:mm")
        val sdf2 = SimpleDateFormat("h:mm a")
        val hour1 = sdf1.format(date.time)
        val hour2 = sdf2.format(c.time).lowercase(Locale.getDefault())

        return "$hour1 - $hour2"
    }

    fun parseDateToUsTime(date: Date): String {
        //04:30 pm
        val sdf = SimpleDateFormat("hh:mm a")
        return sdf.format(date.time).replace("AM", "am").replace("PM", "pm")
    }

    fun parseDateToDayAndUsTime(date: Date): String {
        //Fri, 04:30 pm
        val sdf = SimpleDateFormat("EE, hh:mm a")
        return sdf.format(date.time).replace("AM", "am").replace("PM", "pm")
    }

    fun parseDateToDate(date: Date?): String {
        //05.04.19
        val sdf = SimpleDateFormat("dd.MM.yy")
        date?.let {
            return sdf.format(date.time)
        }
        return ""
    }

    fun parseDateToDateAndTime(date: Date?): String {
        //05.04.19, 6:10PM
        val sdf = SimpleDateFormat("dd.MM.yy, h:mma")
        date?.let {
            return sdf.format(date.time)
        }
        return ""
    }

    fun parseUnixTimestamp(date: Date?): String {
        var yourmilliseconds: Long = Date().time
        date?.let {
            yourmilliseconds = date.time
        }
        val droppedMillis = yourmilliseconds / 1000
        return droppedMillis.toString()
    }

    fun isDateInRange(dateToCheck: Date, startDate: Date?, endDate: Date?): Boolean {
        startDate?.let{
            endDate?.let{
                return dateToCheck.time >= startDate.time && dateToCheck.time <= endDate.time
            }
        }
        return false
    }

    fun isNowInRange(startDate: Date, endDate: Date): Boolean {
        return isDateInRange(Date(), startDate, endDate)
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        val sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
        return sameDay
    }

    fun isSameDay(date1: Date, date2: Date): Boolean {
        val fmt = SimpleDateFormat("yyyyMMdd")
//        fmt.setTimeZone(...); // your time zone
        return fmt.format(date1).equals(fmt.format(date2))
    }

    fun isToday(orderTime: Date): Boolean {
        val orderDate = Calendar.getInstance()
        orderDate.time = orderTime
        val today = Calendar.getInstance()
        return isSameDay(orderDate, today)
    }

    fun parseDatesToNowOrDates(startsAt: Date, endsAt: Date): String {
        return if (isNowInRange(startsAt, endsAt)) {
            "Now"
        } else {
            "${parseDateToDayAndUsTime(startsAt)} - ${parseDateToUsTime(endsAt)}"
        }
    }

    fun isSameTime(from: Date?, from1: Date): Boolean {
        from?.let{
            return from.time == from1.time
        }
        return false
    }

    fun isTomorrow(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = calendar.time

        return isSameDay(date, tomorrow)
    }

    fun isAfterTomorrow(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 2)
        val tomorrow = calendar.time

        return isSameDay(date, tomorrow)
    }
}

/** Compering between 2 dates */
fun Date?.isSameDateAs(dateSecond: Date?): Boolean {
    return parseDateToDate(this) == parseDateToDate(dateSecond)
}
