package com.bupp.wood_spoon_eaters.network.result_handler

import com.bupp.wood_spoon_eaters.managers.EventsManager

class ErrorManger(private val eventsManager : EventsManager) {

    fun onError(origin: String, errorMsg: String){
        eventsManager.logErrorToFirebase(origin, errorMsg)
    }
}