package com.bupp.wood_spoon_chef.data.remote.network

import com.bupp.wood_spoon_chef.data.remote.network.base.*
import com.bupp.wood_spoon_chef.analytics.ChefAnalyticsTracker

class ErrorManger(private val chefAnalyticsTracker: ChefAnalyticsTracker) {

    fun onError(origin: String, error: MTError) {
        when(error){
            is FormattedError -> {
//                eventsManager.logErrorToFirebase(origin, error.message)
            }
            is HTTPError -> {
//                eventsManager.logErrorToFirebase(origin, error.message)
            }
            is JsonError -> {
                chefAnalyticsTracker.trackErrorToFirebase(origin, error.message)
            }
            is NetworkError -> {
                chefAnalyticsTracker.trackErrorToFirebase(origin, error.message)
            }
            is UnknownError -> {
                chefAnalyticsTracker.trackErrorToFirebase(origin, error.message)
            }
            else -> {}
        }
    }
}