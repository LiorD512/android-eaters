//package com.bupp.wood_spoon_eaters.features.events.event_validation
//
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import com.bupp.wood_spoon_eaters.di.abs.ProgressData
//import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
//import com.bupp.wood_spoon_eaters.model.Event
//import com.bupp.wood_spoon_eaters.model.ServerResponse
//import com.bupp.wood_spoon_eaters.network.ApiService
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class GetEventByIdViewModel(val api: ApiService) : ViewModel() {
//
////    var curEventId: Long = -1
//    val progressData = ProgressData()
//
//    val getEventById: SingleLiveEvent<GetEventById> = SingleLiveEvent()
//    data class GetEventById(val isSuccess: Boolean = true, val event: Event?)
//    fun validateEventCode(eventId: String) {
//        progressData.startProgress()
//        api.getEventById(eventId).enqueue(object : Callback<ServerResponse<Event>> {
//            override fun onResponse(call: Call<ServerResponse<Event>>, response: Response<ServerResponse<Event>>) {
//                progressData.endProgress()
//                if (response.isSuccessful) {
//                    Log.d("wowEventValidationVM", "validateEventCode success")
//                    val event = response.body()?.data
//                    if (event != null) {
//                        getEventById.postValue(GetEventById(true, event))
//                    } else {
//                        getEventById.postValue(GetEventById(false, null))
//                    }
//                } else {
//                    Log.d("wowEventValidationVM", "validateEventCode fail")
//                    getEventById.postValue(GetEventById(false, null))
//                }
//            }
//
//            override fun onFailure(call: Call<ServerResponse<Event>>, t: Throwable) {
//                Log.d("wowEventValidationVM", "validateEventCode big fail")
//                progressData.endProgress()
//                getEventById.postValue(GetEventById(false, null))
//            }
//
//        })
//    }
//
//
//
//}