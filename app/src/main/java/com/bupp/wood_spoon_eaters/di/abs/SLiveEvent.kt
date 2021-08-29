//package com.bupp.wood_spoon_eaters.di.abs
//
//import androidx.lifecycle.MutableLiveData
//import com.bupp.wood_spoon_eaters.model.Address
//
///**
// * Used as a wrapper for data that is exposed via a LiveData that represents an event.
// */
//open class SLiveEvent<out T>(private val content: T) {
//
//    var hasBeenHandled = false
//        private set // Allow external read but not write
//
//    /**
//     * Returns the content and prevents its use again.
//     */
//    fun getContentIfNotHandled(): T? {
//        return if (hasBeenHandled) {
//            null
//        } else {
//            hasBeenHandled = true
//            content
//        }
//    }
//
//    /**
//     * Returns the content, even if it's already been handled.
//     */
//    fun peekContent(): T = content
//}
