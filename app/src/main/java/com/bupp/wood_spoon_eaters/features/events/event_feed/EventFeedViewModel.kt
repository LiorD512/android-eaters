//package com.bupp.wood_spoon_eaters.features.events.event_feed
//
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import com.bupp.wood_spoon_eaters.di.abs.ProgressData
//import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
//import com.bupp.wood_spoon_eaters.managers.EaterDataManager
//import com.bupp.wood_spoon_eaters.managers.MetaDataManager
//import com.bupp.wood_spoon_eaters.model.*
//import com.bupp.wood_spoon_eaters.network.ApiService
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class EventFeedViewModel(val api: ApiService, val metaDataManager: MetaDataManager, val eaterDataManager: EaterDataManager) : ViewModel() {
//
//    var curEvent: Event? = null
//
//    private val TAG = "wowFeedVM"
//    val feedEvent: SingleLiveEvent<FeedEvent> = SingleLiveEvent()
//    data class FeedEvent(val isSuccess: Boolean = false, val event: Event)
//
//    data class LikeEvent(val isSuccess: Boolean = false)
//    val likeEvent: SingleLiveEvent<LikeEvent> = SingleLiveEvent()
//
//    fun setCurrentEvent(event: Event){
//        curEvent = event
//        feedEvent.postValue(FeedEvent(true, event))
//    }
//
//    val progressData = ProgressData()
////    val getEventById: SingleLiveEvent<InitEvent> = SingleLiveEvent()
////    data class InitEvent(val isSuccess: Boolean = true, val event: Event?)
////    fun validateEventCode(eventId: String) {
////        progressData.startProgress()
////        api.getEventById(eventId).enqueue(object : Callback<ServerResponse<Event>> {
////            override fun onResponse(call: Call<ServerResponse<Event>>, response: Response<ServerResponse<Event>>) {
////                progressData.endProgress()
////                if (response.isSuccessful) {
////                    Log.d("wowEventValidationVM", "validateEventCode success")
////                    val event = response.body()?.data
////                    if (event != null) {
////                        getEventById.postValue(InitEvent(true, event))
////                    } else {
////                        getEventById.postValue(InitEvent(false, null))
////                    }
////                } else {
////                    Log.d("wowEventValidationVM", "validateEventCode fail")
////                    getEventById.postValue(InitEvent(false, null))
////                }
////            }
////
////            override fun onFailure(call: Call<ServerResponse<Event>>, t: Throwable) {
////                Log.d("wowEventValidationVM", "validateEventCode big fail")
////                progressData.endProgress()
////                getEventById.postValue(InitEvent(false, null))
////            }
////
////        })
////    }
//
//
//
//}