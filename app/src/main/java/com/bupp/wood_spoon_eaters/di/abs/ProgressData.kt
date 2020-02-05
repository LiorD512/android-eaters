package com.bupp.wood_spoon_eaters.di.abs

import androidx.lifecycle.LiveData
import java.util.concurrent.atomic.AtomicInteger

class ProgressData : LiveData<Boolean>() {

    private val progressCounter = AtomicInteger(0)

    init {
        value = progressCounter.get() > 0
    }

    val progressCount = {
        progressCounter.get()
    }

    fun startProgress() {
        postValue(progressCounter.incrementAndGet() > 0)
    }

    fun endProgress() {
        postValue(progressCounter.decrementAndGet() > 0)
    }
}
