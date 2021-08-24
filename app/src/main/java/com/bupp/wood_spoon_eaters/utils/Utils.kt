package com.bupp.wood_spoon_eaters.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.decorators.FeedAdapterDishItemDecorator
import com.bupp.wood_spoon_eaters.model.WSError
import java.util.*


object Utils {

    fun toDp(int: Int): Int = (int / Resources.getSystem().displayMetrics.density).toInt()
    fun toPx(int: Int): Int = (int * Resources.getSystem().displayMetrics.density).toInt()
    fun toSp(int: Int): Int = (int / Resources.getSystem().displayMetrics.scaledDensity).toInt()

    fun String.splitAtIndex(index: Int) = require(index in 0..length).let {
        take(index) to substring(index)
    }

    fun lerp(value: Float, min: Float, max: Float, min2: Float, max2: Float): Float {
        val percentage = (value - min) / (max - min)
        val result = (percentage * (max2 - min2)) + min2
        return result
    }


    fun callPhone(activity: FragmentActivity, phone: String){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), Constants.PHONE_CALL_PERMISSION_REQUEST_CODE)
            }
        } else {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone"))
            activity.startActivity(intent)
        }
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

    fun vibrate(context: Context, milliseconds: Long = 150){
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // Vibrate for 500 milliseconds
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(
                VibrationEffect.createOneShot(milliseconds,
                    VibrationEffect.DEFAULT_AMPLITUDE))
        }
        else {
            v.vibrate(milliseconds)
        }
    }

    fun List<WSError>.getErrorsMsg(): String {
        var errorList = ""
        this?.forEach {
            errorList += "${it.msg} \n"
        }
        return errorList
    }


}