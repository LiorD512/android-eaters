package com.bupp.wood_spoon_eaters.utils

import android.content.Context
import android.content.res.Resources
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.core.content.res.ResourcesCompat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

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
        val sdf = SimpleDateFormat("EE, W, Ha")
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

    fun parseTwoDates(startsAtDate: Date?, endsAtDate: Date?): String {
        val sdf = SimpleDateFormat("HH:mm a")
        if (startsAtDate != null && endsAtDate != null) {
            val start = sdf.format(startsAtDate?.time)
            val end = sdf.format(endsAtDate?.time)
            val monthYear = parseDateToMonthYear(startsAtDate)
            return "$monthYear $start - $end"
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

    fun parseDateFromServer(dateStr: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY", Locale.ENGLISH)
        val date = LocalDate.parse(dateStr, formatter)
        return date.toString()
    }

    fun getFirstAndLastNames(fullName: String): Pair<String, String> {
        var firstNameEndIndex = fullName.indexOf(" ")

        return if (firstNameEndIndex != -1) {
            var str = fullName.splitAtIndex(firstNameEndIndex)

            if (!str.first.isNullOrBlank() && !str.second.isNullOrBlank()) {
                Pair(str.first, str.second)
            } else {
                Pair(fullName, fullName)
            }
        } else {
            Pair(fullName, fullName)
        }
    }
}