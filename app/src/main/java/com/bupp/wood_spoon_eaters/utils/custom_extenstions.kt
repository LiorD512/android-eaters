package com.bupp.wood_spoon_eaters.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.bupp.wood_spoon_eaters.R


fun Fragment.showKeyboard() {
    requireContext()?.let { activity?.showKeyboard(it) }
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

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
    toast.setGravity(Gravity.TOP, 0, 100)
    toast.view = customLayout
    toast.show()
}

fun Fragment.showErrorToast(title: String, anchorView: ViewGroup) {
    val customLayout = layoutInflater.inflate(R.layout.error_toast, anchorView, false)
    val titleView = customLayout.findViewById<TextView>(R.id.errorTitle)
    titleView.text = title
    val toast = Toast(requireContext())
    toast.duration = Toast.LENGTH_SHORT
    toast.setGravity(Gravity.TOP, 0, 100)
    toast.view = customLayout
    toast.show()
}


fun Context.showKeyboard(context: Context) {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
        InputMethodManager.SHOW_FORCED,
        InputMethodManager.HIDE_IMPLICIT_ONLY
    )
}

fun IntRange.convert(number: Int, target: IntRange): Int {
    val ratio = number.toFloat() / (endInclusive - start)
    return (ratio * (target.last - target.first)).toInt()
}

fun Activity.updateScreenUi() {
    val decorView = window.decorView
    decorView.setOnSystemUiVisibilityChangeListener { visibility ->
        if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }
}

fun DialogFragment.updateScreenUi() {
    val decorView = dialog?.window?.decorView
    decorView?.setOnSystemUiVisibilityChangeListener { visibility ->
        if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }
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
