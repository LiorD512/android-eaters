package com.bupp.wood_spoon_chef.utils.extensions

import android.widget.EditText

fun EditText.setTextIfNeeded(text: CharSequence) {
    if (!this.text.contentEquals(text)) {
        setText(text)
        setSelection(text.length)
    }
}
