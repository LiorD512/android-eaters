package com.bupp.wood_spoon_chef.utils.extensions

import android.view.View
import android.view.View.*

fun View.show(show: Boolean = true, useInvisible: Boolean = false) {
    if (visibility == VISIBLE && show) return
    if (visibility == GONE && !show && !useInvisible) return
    if (visibility == INVISIBLE && !show && useInvisible) return
    visibility = if (show) VISIBLE else { if (useInvisible) INVISIBLE else GONE }
}