package com.bupp.wood_spoon_eaters.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment


/** Implementation of KeyboardUtils class with extension for Activity/Fragment/DialogFragment
 *  CloseKeyboard() and OpenKeyboard will be available in every class mentioned above  */

private fun openKeyboard(context: Context, focusView: View) {
    val inputManager: InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.showSoftInput(focusView, InputMethodManager.SHOW_FORCED)
}

private fun closeKeyboard(context: Context, focusView: View) {
    val inputManager: InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(
        focusView.windowToken,
        InputMethodManager.RESULT_UNCHANGED_SHOWN
    )
}

fun Activity.closeKeyboard() {
    closeKeyboard(this, currentFocus ?: View(this))
}

fun Fragment.closeKeyboard() {
    closeKeyboard(requireContext(), activity?.currentFocus ?: View(requireContext()))
}

fun DialogFragment.closeKeyboard() {
    closeKeyboard(requireContext(), dialog?.currentFocus ?: View(requireContext()))
}

fun Activity.openKeyboard() {
    openKeyboard(this, currentFocus ?: View(this))
}

fun Fragment.openKeyboard() {
    openKeyboard(requireContext(), activity?.currentFocus ?: View(requireContext()))
}

fun DialogFragment.openKeyboard() {
    openKeyboard(requireContext(), dialog?.currentFocus ?: View(requireContext()))
}
