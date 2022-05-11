package com.bupp.wood_spoon_eaters.network.result_handler

import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker

class ErrorManger(private val eatersAnalyticsTracker : EatersAnalyticsTracker) {

    fun onError(origin: String, errorMsg: String){
        eatersAnalyticsTracker.logErrorToFirebase(origin, errorMsg)
    }
}