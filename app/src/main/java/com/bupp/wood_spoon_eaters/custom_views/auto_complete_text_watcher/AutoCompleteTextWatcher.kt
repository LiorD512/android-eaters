package com.bupp.wood_spoon_eaters.custom_views.auto_complete_text_watcher

abstract class AutoCompleteTextWatcher : InputTextWatcher {

    constructor() {
        setThrottlingTimeout(450)
    }

    override fun prepareString(input: String): String {
        return input
    }
}
