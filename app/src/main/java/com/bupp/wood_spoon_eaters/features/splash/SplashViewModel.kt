package com.bupp.wood_spoon_eaters.features.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.model.Client
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.MetaDataModel
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.ApiSettings
import com.bupp.wood_spoon_eaters.utils.AppSettings
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class SplashViewModel(val apiSettings: ApiSettings, val eaterDataManager: EaterDataManager, val appSettings: AppSettings, val api: ApiService, val metaDataManager: MetaDataManager) : ViewModel() {

    var serverCallMap = mutableMapOf<Int, Observable<*>>()
    val navigationEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()

    data class NavigationEvent(
        val isSuccess: Boolean = false,
        val isRegistered: Boolean = false,
        val shouldUpdateVersion: Boolean = false
    )


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
            val shouldUpdateVersion = metaDataManager.checkMinVersionFail()
            if (eater == null) {
                navigationEvent.postValue(NavigationEvent(false, false, shouldUpdateVersion))
            } else {
                eaterDataManager.currentEater = eater
                if (eaterDataManager.isAfterLogin()) {
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
            }, { result -> Log.d("wowSplash", "wowException $result") })

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