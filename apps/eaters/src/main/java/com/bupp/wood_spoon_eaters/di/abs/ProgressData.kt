package com.bupp.wood_spoon_eaters.di.abs

import androidx.lifecycle.LiveData
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implementation of Boolean LiveData class
 * that counts the number of startProgress and endProgress calls
 * and post value == true in case there is any unfinished progress.
 */
class ProgressData : LiveData<Boolean>() {

    private val progressCounter = AtomicInteger(0)

    init {
        value = progressCounter.get() > 0
    }

    fun startProgress() {
        postValue(progressCounter.incrementAndGet() > 0)
    }

    fun endProgress() {
        postValue(progressCounter.decrementAndGet() > 0)
    }
}
