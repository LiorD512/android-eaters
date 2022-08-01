package com.bupp.wood_spoon_chef.utils.extensions

import android.util.TypedValue
import android.view.View
import android.view.View.*
import androidx.core.content.ContextCompat

fun View.show(show: Boolean = true, useInvisible: Boolean = false) {
    if (visibility == VISIBLE && show) return
    if (visibility == GONE && !show && !useInvisible) return
    if (visibility == INVISIBLE && !show && useInvisible) return
    visibility = if (show) VISIBLE else { if (useInvisible) INVISIBLE else GONE }
}

fun View.addBackgroundRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}

fun View.addBackgroundCircleRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
    setBackgroundResource(resourceId)
}

fun View.addForegroundRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    foreground = ContextCompat.getDrawable(context, resourceId)
}

fun View.addForegroundCircleRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
    foreground = ContextCompat.getDrawable(context, resourceId)
}