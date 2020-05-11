package com.bupp.wood_spoon_eaters.features.main.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedViewModel(val api: ApiService, val settings: AppSettings, val eaterDataManager: EaterDataManager, val metaDataManager: MetaDataManager): ViewModel(), EaterDataManager.EaterDataMangerListener {


    private val TAG = "wowFeedVM"
    val feedEvent: SingleLiveEvent<FeedEvent> = SingleLiveEvent()
    data class FeedEvent(val isSuccess: Boolean = false, val feedArr: ArrayList<Feed>?)

    data class LikeEvent(val isSuccess: Boolean = false)
    val likeEvent: SingleLiveEvent<LikeEvent> = SingleLiveEvent()

    fun getEaterFirstName(): String?{
        return eaterDataManager.currentEater?.firstName
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
            feedEvent.postValue(FeedEvent(false,null))
            eaterDataManager.setLocationListener(this)
        }
    }


    override fun onAddressChanged(currentAddress: Address?) {
        if(currentAddress != null){
            Log.d("wowFeedVM","getFeed onLocationChanged")
            eaterDataManager.setLastChosenAddress(currentAddress)
            getFeed()
        }
    }

    private fun validFeedRequest(feedRequest: FeedRequest): Boolean {
        return (feedRequest.lat != null && feedRequest.lng != null) || feedRequest.addressId != null
    }

    private fun getFeedRequest(): FeedRequest {
        var feedRequest = FeedRequest()
        //address
        val currentAddress = eaterDataManager.getLastChosenAddress()
        if(eaterDataManager.isUserChooseSpecificAddress()){
            feedRequest.addressId = currentAddress?.id
        }else{
            feedRequest.lat = currentAddress?.lat
            feedRequest.lng = currentAddress?.lng
        }

        //time
        feedRequest.timestamp = eaterDataManager.getFeedSearchTimeStringParam()


        return feedRequest
    }

    fun hasFavorites(): Boolean {
        return settings.hasFavoriets()
    }

    val getCookEvent: SingleLiveEvent<CookEvent> = SingleLiveEvent()
    data class CookEvent(val isSuccess: Boolean = false, val cook: Cook?)
    fun getCurrentCook(id: Long) {
        api.getCook(id).enqueue(object: Callback<ServerResponse<Cook>>{
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

//    fun getDeliveryFeeString(): String {
//        return metaDataManager.getDeliveryFeeStr()
//    }

    fun getShareText(): String {
        val inviteUrl = eaterDataManager.currentEater?.inviteUrl
        val text = "Hey there, I just thought of you and realized you would love this new app. WoodSpoon is the first on-demand homemade food delivery app. You should definitely try it! Download WoodSpoon now and get 30% off your next dish with \"NEWSPOONIE\" promo code \n"
        return "$text \n $inviteUrl"
    }


}