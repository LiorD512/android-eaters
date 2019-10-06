package com.bupp.wood_spoon_eaters.features.login.code

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.FcmManager
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CodeViewModel(val api: ApiService, val eaterDataManager: EaterDataManager, val metaDataManager: MetaDataManager
                    , val deviceDetailsManager: FcmManager) : ViewModel() {

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

                    deviceDetailsManager.refreshPushNotificationToken()

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

    fun updateMetaData() {
        api.getMetaDataCall().enqueue(object : Callback<ServerResponse<MetaDataModel>> {
            override fun onResponse(
                call: Call<ServerResponse<MetaDataModel>>,
                response: Response<ServerResponse<MetaDataModel>>
            ) {
                if (response.isSuccessful) {
                    Log.d("wowSplashVM", "getMetaData success")
                    val metaDataResponse = response.body() as ServerResponse<MetaDataModel>
                    metaDataManager.setMetaDataObject(metaDataResponse.data!!)
//                    updateAddressEvent.postValue(SplashViewModel.NavigationEvent(true, isRegistered))
                } else {
                    Log.d("wowSplashVM", "getMetaData fail")
//                    updateAddressEvent.postValue(SplashViewModel.NavigationEvent(false, isRegistered))
                }
            }

            override fun onFailure(call: Call<ServerResponse<MetaDataModel>>, t: Throwable) {
                Log.d("wowVerificationVM", "getMetaData big fail")
//                updateAddressEvent.postValue(SplashViewModel.NavigationEvent(false, isRegistered))
            }
        })
    }
}