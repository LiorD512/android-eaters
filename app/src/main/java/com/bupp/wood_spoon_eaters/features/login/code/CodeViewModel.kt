package com.bupp.wood_spoon_eaters.features.login.code

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.model.Client
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CodeViewModel(val api: ApiService, val appSettings: AppSettings) : ViewModel() {

    val navigationEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()
    val resendCodeEvent: SingleLiveEvent<ResendCodeEvent> = SingleLiveEvent()

    data class NavigationEvent(val isCodeLegit: Boolean = false)
    data class ResendCodeEvent(val hasSent: Boolean = false)

    private fun onApiDone(isSuccess: Boolean) {
        navigationEvent.postValue(NavigationEvent(isSuccess))
    }

    fun sendCode(phoneNumber: String, codeString: String) {
        api.validateCode(phoneNumber, codeString).enqueue(object : Callback<ServerResponse<Client>> {
            override fun onResponse(call: Call<ServerResponse<Client>>, response: Response<ServerResponse<Client>>) {
                if (response.isSuccessful) {
                    Log.d("wowCodeVM", "send code success: ");
                    val client: Client? = response.body()!!.data
                    appSettings.currentClient = client!!
                    onApiDone(true)
                } else {
                    Log.d("wowCodeVM", "send code fail");
                    onApiDone(false)
                }
            }

            override fun onFailure(call: Call<ServerResponse<Client>>, t: Throwable) {
                Log.d("wowCodeVM", "send code big fail");
                onApiDone(false)
            }

        })
        //appSettings.setToken("123")
    }

    fun sendPhoneNumber(phoneNumber: String) {
        api.getCode(phoneNumber).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
//                    Log.d("wow", "phoneNumber sent: $phoneNumber")
                    Log.d("wowCodeVM", "getCode success")
                    resendCodeEvent.postValue(ResendCodeEvent(true))
                } else {
                    Log.d("wowCodeVM", "getCode fail")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("wowCodeVM", "getCode big fail")
            }
        })
    }
}