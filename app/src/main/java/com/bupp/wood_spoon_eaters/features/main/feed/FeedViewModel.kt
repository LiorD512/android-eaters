package com.bupp.wood_spoon_eaters.features.main.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterAddressManager
import com.bupp.wood_spoon_eaters.managers.LocationManager
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Feed
import com.bupp.wood_spoon_eaters.model.FeedRequest
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedViewModel(val api: ApiService, val settings: AppSettings, val eaterAddressManager: EaterAddressManager): ViewModel(),
    LocationManager.LocationManagerListener {


    private val TAG = "wowFeedVM"
    val feedEvent: SingleLiveEvent<FeedEvent> = SingleLiveEvent()
    data class FeedEvent(val isSuccess: Boolean = false, val feedArr: ArrayList<Feed>?)

    fun getEaterFirstName(): String?{
        return settings.currentEater?.firstName
    }


    fun getFeed(){
        val feedRequest = getFeedRequest()
        Log.d("wowFeedVM","getFeed getFeedRequest: $feedRequest")
        if(validFeedRequest(feedRequest)){
            api.getFeed(feedRequest.lat, feedRequest.lng, feedRequest.addressId, feedRequest.timestamp).enqueue(object: Callback<ServerResponse<ArrayList<Feed>>>{
                override fun onResponse(call: Call<ServerResponse<ArrayList<Feed>>>, response: Response<ServerResponse<ArrayList<Feed>>>) {
                    if(response.isSuccessful){
                        val feedArr = response.body()?.data
                        Log.d("wowFeedVM","getFeed success: ${feedArr.toString()}")
                        feedEvent.postValue(FeedEvent(true, feedArr))
                    }else{
                        Log.d("wowFeedVM","getFeed fail")
                        feedEvent.postValue(FeedEvent(false,null))
                    }
                }

                override fun onFailure(call: Call<ServerResponse<ArrayList<Feed>>>, t: Throwable) {
                    Log.d("wowFeedVM","getFeed big fail")
                    feedEvent.postValue(FeedEvent(false,null))
                }
            })
        }else{
            Log.d("wowFeedVM","getFeed setLocationListener")
            eaterAddressManager.setLocationListener(this)
        }
    }

    override fun onLocationChanged(mLocation: Address) {
        Log.d("wowFeedVM","getFeed onLocationChanged")
        eaterAddressManager.setLastChosenAddress(mLocation)
        eaterAddressManager.removeLocationListener(this)
        getFeed()
    }

    private fun validFeedRequest(feedRequest: FeedRequest): Boolean {
        return (feedRequest.lat != null && feedRequest.lng != null) || feedRequest.addressId != null
    }

    private fun getFeedRequest(): FeedRequest {
        var feedRequest = FeedRequest()
        val currentAddress = eaterAddressManager.getLastChosenAddress()
        if(settings.isUserChooseSpecificAddress()){
            feedRequest.addressId = currentAddress?.id
        }else{
            feedRequest.lat = currentAddress?.lat
            feedRequest.lng = currentAddress?.lng
        }
        return feedRequest
    }

    fun hasFavorites(): Boolean {
        return settings.hasFavoriets()
    }


}