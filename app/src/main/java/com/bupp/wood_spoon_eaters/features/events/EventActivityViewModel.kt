package com.bupp.wood_spoon_eaters.features.events

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class EventActivityViewModel(val eaterDataManager: EaterDataManager, val apiService: ApiService, val metaDataManager: MetaDataManager, val orderManager: OrderManager): ViewModel() {

    private var hasActiveOrder: Boolean = false
    private var hasPendingOrder: Boolean = false

    val navigationEvent = SingleLiveEvent<NavigationEvent>()
    enum class NavigationEvent{
        GET_EVENT_BY_ID_SUCCESS,
        EVENT_FEED
    }

    val errorEvent = SingleLiveEvent<ErrorEvent>()
    enum class ErrorEvent{
        WRONG_CODE,
        FEED_FAILED
    }

    val progressData = ProgressData()
    val liveEventObj = MutableLiveData<Event>()

    fun validateEventCode(eventId: String) {
        progressData.startProgress()
        apiService.getEventById(eventId).enqueue(object : Callback<ServerResponse<Event>> {
            override fun onResponse(call: Call<ServerResponse<Event>>, response: Response<ServerResponse<Event>>) {
                progressData.endProgress()
                if (response.isSuccessful) {
                    Log.d("wowEventValidationVM", "validateEventCode success")
                    val event = response.body()?.data
                    liveEventObj.postValue(event)
                    updateEventTimeAndPlace(event)
                    if (event != null) {
                        navigationEvent.postValue(NavigationEvent.GET_EVENT_BY_ID_SUCCESS)
                    } else {
                        errorEvent.postValue(ErrorEvent.WRONG_CODE)
                    }
                } else {
                    Log.d("wowEventValidationVM", "validateEventCode fail")
                    errorEvent.postValue(ErrorEvent.WRONG_CODE)
                }
            }

            override fun onFailure(call: Call<ServerResponse<Event>>, t: Throwable) {
                Log.d("wowEventValidationVM", "validateEventCode big fail")
                progressData.endProgress()
                errorEvent.postValue(ErrorEvent.WRONG_CODE)
            }

        })
    }

    val initEvent: SingleLiveEvent<InitEvent> = SingleLiveEvent()
    data class InitEvent(val event: Event)
    fun initEvent() {
        liveEventObj.value?.let {
            initEvent.postValue(InitEvent(liveEventObj.value!!))
        }
    }

    val getCookEvent: SingleLiveEvent<CookEvent> = SingleLiveEvent()
    data class CookEvent(val isSuccess: Boolean = false, val cook: Cook?)
    fun getCurrentCook(id: Long) {
        val timestamp = eaterDataManager.getLastOrderTimeParam()
        val eventId = liveEventObj.value?.id
        apiService.getCook(id, timestamp, eventId).enqueue(object: Callback<ServerResponse<Cook>> {
            override fun onResponse(call: Call<ServerResponse<Cook>>, response: Response<ServerResponse<Cook>>) {
                if(response.isSuccessful){
                    val cook = response.body()?.data
                    Log.d("wowFeedVM","getCurrentCook success: ")
                    getCookEvent.postValue(CookEvent(true, cook))
                }else{
                    Log.d("wowFeedVM","getCurrentCook fail")
                    getCookEvent.postValue(CookEvent(false,null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Cook>>, t: Throwable) {
                Log.d("wowFeedVM","getCurrentCook big fail")
                getCookEvent.postValue(CookEvent(false,null))
            }
        })
    }

    fun getContainerPaddingBottom(): Int{
        fun Int.DptoPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
        var padding = 0
        if(hasActiveOrder){
            padding += 55.DptoPx()
        }
        if(hasPendingOrder){
            padding += 55.DptoPx()
        }
        return padding
    }

    val checkCartStatus: SingleLiveEvent<CheckCartStatusEvent> = SingleLiveEvent()
    data class CheckCartStatusEvent(val hasPendingOrder: Boolean)
    fun checkCartStatus() {
        hasPendingOrder = orderManager.haveCurrentActiveOrder()
        checkCartStatus.postValue(CheckCartStatusEvent(hasPendingOrder))
    }
//    fun checkCartStatus() {
//        if(orderManager.haveCurrentActiveOrder()){
//          heightHandling.postValue(HeightHandling(HeightHandlingType.CHECKOUT, ))
//        }
//        checkCartStatus.postValue(CheckCartStatusEvent(hasPendingOrder))
//    }

    val getActiveOrders: SingleLiveEvent<GetActiveOrdersEvent> = SingleLiveEvent()
    data class GetActiveOrdersEvent(val isSuccess: Boolean, val orders: ArrayList<Order>?, val showDialog: Boolean = false)

    fun checkForActiveOrder(showDialog: Boolean = false) {
        apiService.getTrackableOrders().enqueue(object : Callback<ServerResponse<ArrayList<Order>>> {
            override fun onResponse(call: Call<ServerResponse<ArrayList<Order>>>, response: Response<ServerResponse<ArrayList<Order>>>) {
                if (response.isSuccessful) {
//                    Log.d("wow", "phoneNumber sent: $phoneNumber")
                    Log.d("wowMainVM", "getTrackableOrders success")
                    val activeOrders = response.body()!!.data
                    if(activeOrders != null && activeOrders.size > 0){
                        getActiveOrders.postValue(GetActiveOrdersEvent(true, activeOrders, showDialog))
                    }else{
//                        hasActiveOrder = false
                        getActiveOrders.postValue(GetActiveOrdersEvent(false, null, showDialog))
                    }
                } else {
//                    hasActiveOrder = false
                    Log.d("wowMainVM", "getTrackableOrders fail")
                    getActiveOrders.postValue(GetActiveOrdersEvent(false, null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<ArrayList<Order>>>, t: Throwable) {
                Log.d("wowMainVM", "getTrackableOrders big fail")
                getActiveOrders.postValue(GetActiveOrdersEvent(false, null))
            }
        })
    }

    fun getDeliveryFeeString(): String {
        return metaDataManager.getDeliveryFeeStr()
    }

    fun getShareText(): String {
        val inviteUrl = eaterDataManager.currentEater?.inviteUrl
        val text = "Hey there, I just thought of you and realized you would love this new app. WoodSpoon is the first on-demand homemade food delivery app. You should definitely try it! Download WoodSpoon now and get 30% off your next dish with \"NEWSPOONIE\" promo code \n"
        return "$text \n $inviteUrl"
    }


//    fun getLastOrderTime(): String? {
//        return eaterDataManager.getLastOrderTimeString()
//    }
//
//    fun getCurrentAddress(): Address?{
//        val currentAddress = eaterDataManager.getLastChosenAddress()
//        return currentAddress
//    }

    private fun updateEventTimeAndPlace(event: Event?) {
        eaterDataManager.setEventTimeAndPlace(event)
    }

    fun getEventOrderTime(): String? {
        return liveEventObj.value?.let {
            return Utils.parseDateToDayDateHour(it.startsAt)
        }
    }

    fun getEventCurrentAddress(): Address?{
        return liveEventObj.value?.let {
            return it.location
        }
    }

    fun removeEventData() {
        eaterDataManager.disableEventDate()
    }


}