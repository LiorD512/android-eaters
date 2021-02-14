package com.bupp.wood_spoon_eaters.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


object DateUtils {

    fun parseDateToDayDateHour(date: Date): String {
        //mon, 24, 4pm
        val sdf = SimpleDateFormat("EE, dd, h:mma")
        return sdf.format(date.time)
    }

    fun parseDateToStartToEnd(startDate: Date, endDate: Date): String {
        //10:30 AM - 4:00 PM
        val sdf = SimpleDateFormat("h:mma")
        val start = sdf.format(startDate.time)
        val end = sdf.format(endDate.time)
        return "$start - $end"
    }

    fun parseDateToFromStartingDate(orderDate: Date?): String {
        //Aug 2, From 10:30 AM
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mma", Locale.getDefault())
        val date = dateFormat.format(orderDate)
        val time = timeFormat.format(orderDate)
        return "$date, From $time"
    }

    fun parseDateToFullDate(orderDate: Date?): String {
        //Aug 2, 10:30 AM
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mma", Locale.getDefault())
        val date = dateFormat.format(orderDate)
        val time = timeFormat.format(orderDate)
        return "$date, $time"
    }

    @SuppressLint("SimpleDateFormat")
    fun parseDateToTime(date: Date?): String {
        val sdf = SimpleDateFormat("h:mm a")
        if (date != null)
            return sdf.format(date.time)
        return ""
    }

    fun parseDateToUsDate(date: Date): String {
        //August 2, 2019
        val sdf = SimpleDateFormat("MMMM dd, yyyy")
        return sdf.format(date.time)
    }

    fun parseDateToDayDate(date: Date): String {
        //Fri, Feb 12
        val sdf = SimpleDateFormat("EE, MMM dd")
        return sdf.format(date.time)
    }

    fun parseDateOneHourInterval(date: Date): String {
        //4:30 PM - 5:30 PM
        val c = Calendar.getInstance()
        c.time = date
        c.add(Calendar.MINUTE, 30)

        val sdf = SimpleDateFormat("h:mma")
        val hour1 = sdf.format(date.time)
        val hour2 = sdf.format(c.time)
        return "$hour1 - $hour2"
    }

    fun parseDateToUsTime(date: Date): String {
        //4:30 PM
        val sdf = SimpleDateFormat("h:mma")
        return sdf.format(date.time)
    }

    fun parseDateToUsDayTime(date: Date?): String{
        date?.let{
            if(isToday(it))
                return parseDateToUsTime(it)
            else
                return parseDateToDayDateHour(it)
        }
        return ""
    }

    fun parseDateToDateAndTime(date: Date?): String {
        //05.04.19, 6:10PM
        val sdf = SimpleDateFormat("dd.MM.yy, h:mma")
        date?.let{
            return sdf.format(date.time)
        }
        return ""
    }

    fun parseTwoDates(startsAtDate: Date?, endsAtDate: Date?): String {
        val sdf = SimpleDateFormat("ha")
        if (startsAtDate != null && endsAtDate != null) {
            val end = sdf.format(endsAtDate.time)
            val dayDateHour = parseDateToDayDateHour(startsAtDate)
            return "$dayDateHour - $end"
        }
        return ""
    }

    fun parseTime(date: Date?): String {
        val sdf = SimpleDateFormat("H:mm")
        return sdf.format(date?.time)
    }

    fun parseCountDown(date: Date): CharSequence? {
        val sdf = SimpleDateFormat("mm:ss")
        return sdf.format(date?.time)
    }

    fun parseUnixTimestamp(date: Date?): String {
        var yourmilliseconds: Long = Date().time
        date?.let{
            yourmilliseconds = date.time
        }
        val droppedMillis = yourmilliseconds / 1000
        return droppedMillis.toString()
    }

    fun isNow(newChosenDate: Date?): Boolean {
        newChosenDate?.let{
            val now = Date().time
            return it.time-1000 < now
        }
        return true
    }

    fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        val sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        return sameDay
    }

    fun isIn30MinutesRangeFromNow(dateToCheck: Date): Boolean {
        val now = Calendar.getInstance()
        val nowPlusHalfHour = Calendar.getInstance()
        nowPlusHalfHour.add(Calendar.MINUTE, 30)

        val calendarToCheck = Calendar.getInstance()
        calendarToCheck.time = dateToCheck
        calendarToCheck.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR))
        return calendarToCheck.timeInMillis <= nowPlusHalfHour.timeInMillis && calendarToCheck.timeInMillis >= now.timeInMillis
    }

    fun isTodayOrTomorrow(orderTime: Date): Boolean {
        val orderDate = Calendar.getInstance()
        orderDate.time = orderTime
        val today = Calendar.getInstance()
        val tomorrow = GregorianCalendar()
        tomorrow.add(Calendar.DATE, 1)
        return isSameDay(orderDate, today) || isSameDay(orderDate, tomorrow)
    }

    fun isToday(orderTime: Date): Boolean {
        val orderDate = Calendar.getInstance()
        orderDate.time = orderTime
        val today = Calendar.getInstance()
        return isSameDay(orderDate, today)
    }
}