package com.bupp.wood_spoon_eaters.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.bupp.wood_spoon_eaters.R


fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.showErrorToast(title: String, anchorView: ViewGroup) {
    val customLayout = layoutInflater.inflate(R.layout.error_toast, anchorView, false)
    val titleView = customLayout.findViewById<TextView>(R.id.errorTitle)
    titleView.text = title
    val toast = Toast(this)
    toast.duration = Toast.LENGTH_SHORT
    toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 50)
    toast.view = customLayout
    toast.show()
}

fun Fragment.showErrorToast(title: String, anchorView: ViewGroup) {
    val customLayout = layoutInflater.inflate(R.layout.error_toast, anchorView, false)
    val titleView = customLayout.findViewById<TextView>(R.id.errorTitle)
    titleView.text = title.trim()
    val toast = Toast(requireContext())
    toast.duration = Toast.LENGTH_SHORT
    toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 50)
    toast.view = customLayout
    toast.show()
}


fun showKeyboard(context: Context) {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
        InputMethodManager.SHOW_FORCED,
        InputMethodManager.HIDE_IMPLICIT_ONLY
    )
}

fun IntRange.convert(number: Int, target: IntRange): Int {
    val ratio = number.toFloat() / (endInclusive - start)
    return (ratio * (target.last - target.first)).toInt()
}



fun NavController.navigateSafe(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navExtras: Navigator.Extras? = null
) {
    val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)
    if (action != null && currentDestination?.id != action.destinationId) {
        navigate(resId, args, navOptions, navExtras)
    }
}

inline fun View.waitForLayout(crossinline funAction: () -> Unit) {
    val vto = viewTreeObserver
    vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            when {
                vto.isAlive -> {
                    vto.removeOnGlobalLayoutListener(this)
                    funAction()
                }
                else -> {
                    funAction()
                }
            }
        }
    })
}
