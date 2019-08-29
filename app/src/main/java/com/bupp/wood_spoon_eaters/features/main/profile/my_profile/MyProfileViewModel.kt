package com.bupp.wood_spoon_eaters.features.main.profile.my_profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.dialogs.RateLastOrderViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.splash.SplashViewModel
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class MyProfileViewModel(val api: ApiService, val appSettings: AppSettings, val eaterDataManager: EaterDataManager) :
    ViewModel() {


    val TAG = "wowMyProfileVM"
    data class GetUserDetails(val isSuccess: Boolean, val eater: Eater? = null)
    val getUserDetails: SingleLiveEvent<GetUserDetails> = SingleLiveEvent()

    fun getUserDetails() {
        api.getMeCall().enqueue(object : Callback<ServerResponse<Eater>> {
            override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
                if (response.isSuccessful) {
                    val eater = response.body()?.data
                    if (eater != null) {
                        getUserDetails.postValue(GetUserDetails(true, eater))
                    } else {
                        getUserDetails.postValue(GetUserDetails(false))
                    }
                } else {
                    getUserDetails.postValue(GetUserDetails(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Eater>>, t: Throwable) {
                getUserDetails.postValue(GetUserDetails(false))
            }

        })
    }

    fun getDeliveryAddress(): String {
        val streetLine1 = eaterDataManager.getLastChosenAddress()?.streetLine1
        return if (streetLine1.isNullOrEmpty()) {
            ""
        } else {
            streetLine1
        }
    }

    fun logout(context: Context) {
        appSettings.logout(context)
    }



}
