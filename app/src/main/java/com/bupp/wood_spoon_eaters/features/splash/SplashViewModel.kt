package com.bupp.wood_spoon_eaters.features.splash

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.new_order.service.EphemeralKeyProvider
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.Client
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.MetaDataModel
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.ApiSettings
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.stripe.android.CustomerSession
import com.stripe.android.PaymentConfiguration
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class SplashViewModel(val apiSettings: ApiSettings, val eaterDataManager: EaterDataManager, val appSettings: AppSettings, val api: ApiService, val metaDataManager: MetaDataManager,
val paymentManager: PaymentManager, val eventsManager: EventsManager) : ViewModel(), EphemeralKeyProvider.EphemeralKeyProviderListener {

    private var loginTries: Int = 0
    var serverCallMap = mutableMapOf<Int, Observable<*>>()
    val navigationEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()

    data class NavigationEvent(
        val isSuccess: Boolean = false,
        val isRegistered: Boolean = false,
        val shouldUpdateVersion: Boolean = false
    )

    val errorEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()

    //todo - add getFeed call. save feed object in FeedHelper (singleton)- update feed saved object on new getFeed Call.



    fun initServerCall() {
        val isRegistered = apiSettings.isRegistered()
        if (isRegistered) {
            Log.d("wowSplashVM", "current exist")
            getUserAndMetaData()
        } else {
            api.getMetaDataCall().enqueue(object : Callback<ServerResponse<MetaDataModel>> {
                override fun onResponse(
                    call: Call<ServerResponse<MetaDataModel>>,
                    response: Response<ServerResponse<MetaDataModel>>
                ) {
                    if (response.isSuccessful) {
                        Log.d("wowSplashVM", "getMetaData success")
                        val metaDataResponse = response.body() as ServerResponse<MetaDataModel>
                        metaDataManager.setMetaDataObject(metaDataResponse.data!!)
                        val shouldUpdateVersion = metaDataManager.checkMinVersionFail()
                        navigationEvent.postValue(NavigationEvent(false, isRegistered, shouldUpdateVersion))
                    } else {
                        Log.d("wowSplashVM", "getMetaData fail")
                        navigationEvent.postValue(NavigationEvent(false, isRegistered, false))
                    }
                }

                override fun onFailure(call: Call<ServerResponse<MetaDataModel>>, t: Throwable) {
                    Log.d("wowVerificationVM", "getMetaData big fail")
                    navigationEvent.postValue(NavigationEvent(false, isRegistered, false))
                }
            })
        }
    }



    fun getUserAndMetaData() {
        Log.d("wowSplash", "init start")
        serverCallMap.put(0, api.getMe())
        serverCallMap.put(1, api.getMetaData())
        val requests = ArrayList<Observable<*>>()
        for (call in serverCallMap) {
            requests.add(call.value)
        }

        Observable.zip(requests) { objects ->
            Log.d("wowSplash", "Observable success")

            //parse client
            val eaterServerResponse = objects[0] as ServerResponse<Eater>
            val eater: Eater? = eaterServerResponse.data
            Log.d("wowSplash", "eater parsing success: " + eater?.id)

            //parse metaData
            val metaDataResponse = objects[1] as ServerResponse<MetaDataModel>
            metaDataManager.setMetaDataObject(metaDataResponse.data!!)
            Log.d("wowSplash", "metaData success")
            val shouldUpdateVersion = metaDataManager.checkMinVersionFail()
            if (eater == null) {
                Log.d("wowSplash", "eater null")
                navigationEvent.postValue(NavigationEvent(false, false, shouldUpdateVersion))
            } else {
                Log.d("wowSplash", "eater: $eater")
                eaterDataManager.currentEater = eater
                eventsManager.initSegment()
                if (eaterDataManager.isAfterLogin()) {
                    initRelevantRepositories()
                    navigationEvent.postValue(NavigationEvent(true, true, shouldUpdateVersion))
                } else {
                    navigationEvent.postValue(NavigationEvent(true, false, shouldUpdateVersion))
                }
            }
            Any()
        }.timeout(55000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                object : Consumer<Any> {
                    override fun accept(t: Any?) {
                        Log.d("wowSplash", "Observable accept success")
                    }
                }
            }, { result ->
                run {
                    Log.d("wowSplash", "wowException $result")
                    loginTries++
                    if (loginTries <= 3) {
                        Log.d("wowSplash", "tring to initSplash again #$loginTries")
                        initServerCall()
                    } else {
                        Log.d("wowSplash", "init tries reached limit")
                        errorEvent.postValue(true)
                    }
                }
            })
    }

    private fun initRelevantRepositories() {
        paymentManager.initPaymentManager()
    }

    fun setUserCampaignParam(sid: String?, cid: String?) {
        sid?.let{
           eaterDataManager.setUserCampaignParam(sid = it)
        }
        cid?.let{
            eaterDataManager.setUserCampaignParam(cid = it)
        }

    }
}