package com.bupp.wood_spoon_eaters.features.login.code

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.Client
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CodeViewModel(val api: ApiService, val eaterDataManager: EaterDataManager) : ViewModel() {

    val navigationEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()
    val resendCodeEvent: SingleLiveEvent<ResendCodeEvent> = SingleLiveEvent()

    data class NavigationEvent(val isCodeLegit: Boolean = false, val isAfterLogin: Boolean = false)
    data class ResendCodeEvent(val hasSent: Boolean = false)

    fun sendCode(phoneNumber: String, codeString: String) {
        api.validateCode(phoneNumber, codeString).enqueue(object : Callback<ServerResponse<Eater>> {
            override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
                if (response.isSuccessful) {
                    Log.d("wowCodeVM", "send code success: ");
                    val eater: Eater? = response.body()!!.data
                    eaterDataManager.currentEater = eater!!
                    navigationEvent.postValue(NavigationEvent(true, eaterDataManager.isAfterLogin()))
                } else {
                    Log.d("wowCodeVM", "send code fail");
                    navigationEvent.postValue(NavigationEvent(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Eater>>, t: Throwable) {
                Log.d("wowCodeVM", "send code big fail");
                navigationEvent.postValue(NavigationEvent(false))
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