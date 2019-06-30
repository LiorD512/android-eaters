package com.bupp.wood_spoon_eaters.features.sign_up.create_account

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Utils
import com.taliazhealth.predictix.network_google.models.google_api.AddressResponse


class CreateAccountViewModel(val appSettings: AppSettings, val api: ApiService) : ViewModel() {

    var client: Client? = appSettings.currentClient
    val navigationEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()

    data class NavigationEvent(val hasAccount: Boolean = false)

    fun updateClientAccount(
        fullName: String,
        email: String
    ) {

        var firstAndLast: Pair<String, String> = Utils.getFirstAndLastNames(fullName)
        var firstName = firstAndLast.first
        var lastName = firstAndLast.second

        if (client == null) {
            client = Client()
        }
        if (appSettings.currentClient != null) {
            client = appSettings.currentClient!!

            client = client!!.copy(firstName = firstName, lastName = lastName, email = email)
        } else {
            client = Client(firstName = firstName, lastName = lastName, email = email)
        }
        appSettings.currentClient = client

        navigationEvent.postValue(NavigationEvent(true))
    }
}


