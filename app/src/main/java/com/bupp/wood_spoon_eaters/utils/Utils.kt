package com.bupp.wood_spoon_eaters.utils

import android.content.Context
import android.content.res.Resources
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.core.content.res.ResourcesCompat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Calendar.*

object Utils {

    fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    fun String.splitAtIndex(index: Int) = require(index in 0..length).let {
        take(index) to substring(index)
    }

    fun parseDate(date: Date?): String {
        val sdf = SimpleDateFormat("MMMM dd, yyyy")
        return sdf.format(date?.time)
    }

    fun parseDateToMonthYear(date: Date): String {
        val sdf = SimpleDateFormat("MMMM yyyy")
        return sdf.format(date.time)
    }

    fun parseDateToDayDateHour(date: Date): String {
        //mon, 24, 4pm
        val sdf = SimpleDateFormat("EE, dd, H:mma")
        return sdf.format(date.time)
    }

    fun parseDateToDayMonthYear(date: Date?): String {
        val sdf = SimpleDateFormat("EEEE, MMMM yyyy")
        return sdf.format(date?.time)
    }

    fun parseDateToTime(date: Date?): String {
        val sdf = SimpleDateFormat("HH:mm a")
        if (date != null)
            return sdf.format(date?.time)
        return ""
    }

    fun parseDateToDateAndTime(date: Date?): String {
        //05.04.19, 6:10PM
        val sdf = SimpleDateFormat("ww.dd.yy, HH:mma")
        if (date != null)
            return sdf.format(date?.time)
        return ""
    }

    fun parseTwoDates(startsAtDate: Date?, endsAtDate: Date?): String {
//        val sdf = SimpleDateFormat("HH:mm a")
        val sdf = SimpleDateFormat("Ha")
        if (startsAtDate != null && endsAtDate != null) {
//            val start = sdf.format(startsAtDate?.time)
            val end = sdf.format(endsAtDate?.time)
            val dayDateHour = parseDateToDayDateHour(startsAtDate)
            return "$dayDateHour - $end"
        }
        return ""
    }

    fun parseTime(date: Date?): String {
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(date?.time)
    }

    fun parseCountDown(date: Date): CharSequence? {
        val sdf = SimpleDateFormat("mm:ss")
        return sdf.format(date?.time)
    }

    fun parseUnixTimestamp(date: Date?): String {
        var yourmilliseconds: Long = Date().time
        if(date != null){
            yourmilliseconds = date.time
        }
        val droppedMillis = yourmilliseconds/ 1000
        return droppedMillis.toString()
    }




    fun setCustomFontTypeSpan(
        context: Context,
        source: String,
        startIndex: Int,
        endIndex: Int,
        font: Int
    ): SpannableString {

        val spannableString = SpannableString(source)

        val typeface = ResourcesCompat.getFont(context, font)

        spannableString.setSpan(
            StyleSpan(typeface!!.style),
            startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

    fun isValidEmailAddress(email: String): Boolean {
        val ePattern =
            "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
        val p = java.util.regex.Pattern.compile(ePattern)
        val m = p.matcher(email)
        return m.matches()
    }

    fun parseDateForServer(date: Date): String {
        val sdf = SimpleDateFormat("dd/MM/YYYY")
        return sdf.format(date?.time)
    }

    fun getFirstAndLastNames(fullName: String): Pair<String, String> {
        var firstNameEndIndex = fullName.indexOf(" ")

        return if (firstNameEndIndex != -1) {
            var str = fullName.splitAtIndex(firstNameEndIndex)

            if (!str.first.isNullOrBlank() && !str.second.isNullOrBlank()) {
                var first : String = str.first
                var last: String = str.second

                if(first.endsWith(" ")){
                    first = str.first.replace(" ","")
                }

                if(last.startsWith(" ")){
                    last = str.second.replaceFirst(" ", "")
                }

                Pair(first, last)
            } else {
                Pair(fullName, fullName)
            }
        } else {
            Pair(fullName, fullName)
        }
    }

    fun getDiffYears(date: Date): Int {
        val a = getCalendar(Date())
        val b = getCalendar(date)
        var diff = b.get(YEAR) - a.get(YEAR)
        if (a.get(MONTH) > b.get(MONTH) || a.get(MONTH) === b.get(MONTH) && a.get(DATE) > b.get(DATE)) {
            diff--
        }
        return diff
    }

    fun getCalendar(date: Date): Calendar {
        val cal = Calendar.getInstance(Locale.US)
        cal.time = date
        return cal
    }
}