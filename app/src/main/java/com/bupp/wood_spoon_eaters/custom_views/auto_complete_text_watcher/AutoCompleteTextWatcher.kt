package com.bupp.wood_spoon_eaters.custom_views.auto_complete_text_watcher

abstract class AutoCompleteTextWatcher(throttlingTimeout: Long) : InputTextWatcher() {

    init {
        setThrottlingTimeout(throttlingTimeout)
    }

    override fun prepareString(input: String): String {
        return input
    }
}
