package com.bupp.wood_spoon_chef.data.remote.network

import com.bupp.wood_spoon_chef.data.remote.network.base.*
import com.bupp.wood_spoon_chef.managers.EventsManager

class ErrorManger(private val eventsManager: EventsManager) {

    fun onError(origin: String, error: MTError) {
        when(error){
            is FormattedError -> {
//                eventsManager.logErrorToFirebase(origin, error.message)
            }
            is HTTPError -> {
//                eventsManager.logErrorToFirebase(origin, error.message)
            }
            is JsonError -> {
                eventsManager.logErrorToFirebase(origin, error.message)
            }
            is NetworkError -> {
                eventsManager.logErrorToFirebase(origin, error.message)
            }
            is UnknownError -> {
                eventsManager.logErrorToFirebase(origin, error.message)
            }
            else -> {}
        }
    }
}