package com.bupp.wood_spoon_eaters.features.login.verification

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhoneVerificationViewModel(val api: ApiService) : ViewModel() {

    val navigationEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()

    data class NavigationEvent(val isSuccess: Boolean = false)

    fun sendPhoneNumber(phoneNumber: String) {
        api.getCode(phoneNumber).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("wowVerificationVM", "getCode success")
                    navigationEvent.postValue(NavigationEvent(true))
                } else {
                    Log.d("wowVerificationVM", "getCode fail")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("wowVerificationVM", "getCode big fail")
                navigationEvent.postValue(NavigationEvent(false))
            }
        })
    }
}