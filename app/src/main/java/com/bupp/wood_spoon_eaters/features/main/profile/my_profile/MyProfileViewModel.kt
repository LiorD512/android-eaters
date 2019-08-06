package com.bupp.wood_spoon_eaters.features.main.profile.my_profile

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

//    val getLastOrders: SingleLiveEvent<LastOrdersEvent> = SingleLiveEvent()
//    data class LastOrdersEvent(val isSuccess: Boolean = false, val lastOrders: ArrayList<Dish>? = null)
//
//    fun getLastOrders() {
//        val feedRequest = getFeedRequest()
//        Log.d(TAG, "getFeed getFeedRequest: $feedRequest")
//        if (validFeedRequest(feedRequest)) {
//            api.getEaterOrdered(feedRequest.lat, feedRequest.lng, feedRequest.addressId, feedRequest.timestamp).enqueue(object : Callback<ServerResponse<Search>> {
//                override fun onResponse(call: Call<ServerResponse<Search>>, response: Response<ServerResponse<Search>>) {
//                    if (response.isSuccessful) {
//                        val searchObject = response.body()?.data
//                        if (searchObject != null) {
//                            val lastOrder = searchObject?.results
//                            getLastOrders.postValue(LastOrdersEvent(true, lastOrder as ArrayList<Dish>))
//                        } else {
//                            getLastOrders.postValue(LastOrdersEvent(false, null))
//                        }
//                    } else {
//                        Log.d(TAG, "postOrder fail")
//                        getLastOrders.postValue(LastOrdersEvent(false, null))
//                    }
//                }
//
//                override fun onFailure(call: Call<ServerResponse<Search>>, t: Throwable) {
//                    Log.d(TAG, "postOrder big fail")
//                    getLastOrders.postValue(LastOrdersEvent(false, null))
//                }
//            })
//        }else{
//            Log.d(TAG,"getFeed setLocationListener")
//            eaterDataManager.setLocationListener(this)
//        }
//
//    }
//
//    override fun onAddressChanged(currentAddress: Address?) {
//        eaterDataManager.setLastChosenAddress(currentAddress)
//        getLastOrders()
//    }
//
//    fun getFeedRequest(): FeedRequest {
//        var feedRequest = FeedRequest()
//        //address
//        val currentAddress = eaterDataManager.getLastChosenAddress()
//        if (eaterDataManager.isUserChooseSpecificAddress()) {
//            feedRequest.addressId = currentAddress?.id
//        } else {
//            feedRequest.lat = currentAddress?.lat
//            feedRequest.lng = currentAddress?.lng
//        }
//
//        //time
//        feedRequest.timestamp = eaterDataManager.getLastOrderTimeParam()
//        return feedRequest
//    }
//
//    private fun validFeedRequest(feedRequest: FeedRequest): Boolean {
//        return (feedRequest.lat != null && feedRequest.lng != null) || feedRequest.addressId != null
//    }
//
//    fun initProfileData() {
//        ()
////        getLastOrders()
//    }

    //get me live event


    //get last orders live event


    //get favorites live event
//    val getFavorites: SingleLiveEvent<FavoritesEvent> = SingleLiveEvent()
//    data class FavoritesEvent(val isSuccess: Boolean = false, val favorites: Search? = null)
//
//    val onNetworkDone: SingleLiveEvent<OnNetworkDone> = SingleLiveEvent()
//    data class OnNetworkDone(val isDone: Boolean = false)
//
//    var serverCallMap = mutableMapOf<Int, Observable<*>>()
//    fun initProfileData() {
//        //get me
//        //get last orders
//        //get favorites
//        serverCallMap.put(0, api.getMe())
//
//        val feedRequest = getFeedRequest()
//        Log.d(TAG, "getFeed getFeedRequest: $feedRequest")
//        if (validFeedRequest(feedRequest)) {
//            serverCallMap.put(1 ,api.getEaterOrdered(feedRequest.lat, feedRequest.lng, feedRequest.addressId, feedRequest.timestamp))
//        }
//
//        val requests = ArrayList<Observable<*>>()
//        for (call in serverCallMap) {
//            requests.add(call.value)
//        }
//
//        Observable.zip(requests) { objects ->
//            Log.d("wowProfileVM", "Observable success")
//
//            //parse client
//            val eaterServerResponse = objects[0] as ServerResponse<Eater>
//            val eater: Eater? = eaterServerResponse.data
//            Log.d("wowProfileVM", "eater parsing success: " + eater?.id)
//            getUserDetails.postValue(GetUserDetails(true, eater))
//
//            //parse Last Orders
//            val lastOrderResponse = objects[1] as ServerResponse<Search>
//            val lastOrderSearchObj = lastOrderResponse.data
//            Log.d("wowProfileVM","getLastOrders success: ${lastOrderSearchObj.toString()}")
//            getLastOrders.postValue(LastOrdersEvent(true, lastOrderSearchObj))
//
//            Any()
//        }.timeout(55000, TimeUnit.MILLISECONDS)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe({
//                object : Consumer<Any> {
//                    override fun accept(t: Any?) {
//                        Log.d("wowProfileVM", "Observable accept success")
//                        onNetworkDone.postValue(OnNetworkDone(true))
//                    }
//                }
//            }, { result ->
//                Log.d("wowProfileVM", "wowException $result") })
//    }




}
