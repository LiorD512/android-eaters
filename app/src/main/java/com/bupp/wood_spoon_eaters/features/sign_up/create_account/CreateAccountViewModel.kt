package com.bupp.wood_spoon_eaters.features.sign_up.create_account

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.EaterRequest
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class CreateAccountViewModel(val eaterDataManager: EaterDataManager, val api: ApiService) : ViewModel() {

    val navigationEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()

    data class NavigationEvent(val isSuccess: Boolean = false)

    fun updateClientAccount(fullName: String, email: String) {
        var firstAndLast: Pair<String, String> = Utils.getFirstAndLastNames(fullName)
        var firstName = firstAndLast.first
        var lastName = firstAndLast.second

        val eater = EaterRequest()
        eater.firstName = firstName
        eater.lastName = lastName
        eater.email = email
        postClient(eater)
    }

    private fun postClient(eater: EaterRequest) {
        api.postMe(eater!!).enqueue(object : Callback<ServerResponse<Eater>> {
            override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
                if (response.isSuccessful) {
                    Log.d("wowCreateAccountVM", "on success! ")
                    eaterDataManager.currentEater = response.body()?.data!!
                } else {
                    Log.d("wowCreateAccountVM", "on Failure! ")
                }
                navigationEvent.postValue(NavigationEvent(isSuccess = response.isSuccessful))

            }

            override fun onFailure(call: Call<ServerResponse<Eater>>, t: Throwable) {
                Log.d("wowCreateAccountVM", "on big Failure! " + t.message)
                navigationEvent.postValue(NavigationEvent(false))
            }
        })
    }
}


