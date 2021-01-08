package com.bupp.wood_spoon_eaters.custom_views.auto_complete_text_watcher

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import java.util.*


abstract class InputTextWatcher : TextWatcher {

    private var myRunnable: Runnable? = null
    private val myHandler = Handler()
    private var throttlingTimeout: Long = 0
    private var timer = Timer()

    fun setThrottlingTimeout(throttlingTimeout: Long) {
        Log.d("wowInputTextWatcher", "setThrottlingTimeout: $throttlingTimeout")
        this.throttlingTimeout = throttlingTimeout
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val preparedString = prepareString(s.toString())
        handleInputStringThrottleIfNeeded(preparedString)
    }

    override fun afterTextChanged(s: Editable) {

    }

    protected open fun prepareString(input: String): String {
        return input
    }

    private fun handleInputStringThrottleIfNeeded(input: String) {
        if(myRunnable != null){
            myHandler.removeCallbacks(myRunnable)
            myRunnable = null
        }
        myRunnable = Runnable {
            Log.d("wowInputTextWatcher", "now: $input")
            handleInputString(input)
        }
        if (throttlingTimeout > 0) {
            myHandler.postDelayed(myRunnable, throttlingTimeout)
        } else {
            handleInputString(input)
        }
    }


    protected abstract fun handleInputString(string: String)
}
