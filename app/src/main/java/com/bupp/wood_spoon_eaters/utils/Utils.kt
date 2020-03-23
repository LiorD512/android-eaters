package com.bupp.wood_spoon_eaters.utils

import android.Manifest
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
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentActivity
import com.bupp.wood_spoon_eaters.R


object Utils {

    fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    fun Int.DptoPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    fun String.splitAtIndex(index: Int) = require(index in 0..length).let {
        take(index) to substring(index)
    }

    fun onContactUsClick(context: Context) {

    }


    fun callPhone(activity: FragmentActivity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), Constants.PHONE_CALL_PERMISSION_REQUEST_CODE)
            }
        } else {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + activity.getString(R.string.default_bupp_phone_number)))
            activity.startActivity(intent)
        }
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

    fun parseDateToTime(date: Date?): String {
        val sdf = SimpleDateFormat("h:mm a")
        if (date != null)
            return sdf.format(date?.time)
        return ""
    }

    fun parseDDateToUsDate(date: Date): String {
        //August 2, 2019
        val sdf = SimpleDateFormat("MMMM dd, yyyy")
        return sdf.format(date.time)
    }

    fun parseDDateToUsTime(date: Date): String {
        //4:30 PM
        val sdf = SimpleDateFormat("h:mma")
        return sdf.format(date.time)
    }


    fun parseDateToDateAndTime(date: Date?): String {
        //05.04.19, 6:10PM
        val sdf = SimpleDateFormat("dd.MM.yy, h:mma")
        if (date != null)
            return sdf.format(date?.time)
        return ""
    }

    fun parseTwoDates(startsAtDate: Date?, endsAtDate: Date?): String {
//        val sdf = SimpleDateFormat("HH:mm a")
        val sdf = SimpleDateFormat("ha")
        if (startsAtDate != null && endsAtDate != null) {
//            val start = sdf.format(startsAtDate?.time)
            val end = sdf.format(endsAtDate?.time)
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

    fun isTodayOrTomorrow(orderTime: Date): Boolean {
        val orderDate = Calendar.getInstance()
        orderDate.time = orderTime
        val today = Calendar.getInstance()
        val tomorrow = GregorianCalendar()
        tomorrow.add(Calendar.DATE, 1)
        return isSameDay(orderDate, today) || isSameDay(orderDate, tomorrow)
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
                var first: String = str.first
                var last: String = str.second

                if (first.endsWith(" ")) {
                    first = str.first.replace(" ", "")
                }

                if (last.startsWith(" ")) {
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
        val a = getCalendar(date)
        val b = getCalendar(Date())
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

    fun hideKeyBoard(activity: FragmentActivity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.getCurrentFocus()
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }

    fun shareText(activity: FragmentActivity, text: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        activity.startActivity(Intent.createChooser(shareIntent, "Share"))
    }




}