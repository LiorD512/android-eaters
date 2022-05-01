package com.bupp.wood_spoon_chef.di.abs

import androidx.lifecycle.MutableLiveData

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class LiveEvent<out T>(private val content: T) {

    private var hasBeenHandled = false

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

}

open class LiveEventData<T> : MutableLiveData<LiveEvent<T>>() {

    fun postRawValue(value: T) = this.postValue(LiveEvent(value))
}