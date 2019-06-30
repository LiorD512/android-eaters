package com.bupp.wood_spoon_eaters.features.sign_up.create_account

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.model.Client
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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

        postClient(client)
    }

    private fun postClient(client: Client?) {
        api.postMe(client!!).enqueue(object : Callback<ServerResponse<Client>> {
            override fun onResponse(call: Call<ServerResponse<Client>>, response: Response<ServerResponse<Client>>) {
                if (response.isSuccessful) {
                    Log.d("wowCreateAccountVM", "on success! ")
                    appSettings.currentClient = response.body()?.data!!
                    appSettings.hasFinishedStory = true
                } else {
                    Log.d("wowCreateAccountVM", "on Failure! ")
                }
                navigationEvent.postValue(NavigationEvent(hasAccount = response.isSuccessful))

            }

            override fun onFailure(call: Call<ServerResponse<Client>>, t: Throwable) {
                Log.d("wowCreateAccountVM", "on big Failure! " + t.message)
                navigationEvent.postValue(NavigationEvent(hasAccount = false))
            }
        })
    }
}


