package com.eatwoodspoon.android_utils.views

import android.os.SystemClock
import android.view.View

class SafeClickListener(
    private var defaultInterval: Int = 500,
    private val onSafeClick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeClick(v)
    }
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

fun View.setLongerSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener(defaultInterval = 2000) {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}