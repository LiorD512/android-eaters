package com.bupp.wood_spoon_chef.presentation.views

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.CharacterStyle
import android.text.style.StyleSpan

fun findTextAndApplySpan(result: SpannableString, part: String, fullString: String,
                                 styleSpan: CharacterStyle = StyleSpan(Typeface.BOLD)
) {
    val startIdx = part.let { fullString.indexOf(part, ignoreCase = true) }
    val length = part.length
    if (startIdx >= 0) {
        result.setSpan(styleSpan, startIdx, startIdx + length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
    }
}