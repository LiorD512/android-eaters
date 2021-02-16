package com.bupp.wood_spoon_eaters.features.main.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.FeedRepository
import kotlinx.coroutines.launch

class FeedViewModel(
    private val feedDataManager: FeedDataManager, private val feedRepository: FeedRepository): ViewModel() {


    fun initFeed(){
        feedDataManager.initFeedDataManager()
    }

    fun getLocationLiveData() = feedDataManager.getLocationLiveData()
    fun getFinalAddressParams() = feedDataManager.getFinalAddressLiveDataParam()

    val feedUiStatusLiveData = feedDataManager.getFeedUiStatus()



    fun refreshFeedByLocationIfNeeded() {
        feedDataManager.refreshFeedByLocationIfNeeded()
    }

    fun refreshFeedForNewAddress(address: Address) {
        Log.d(TAG,"refreshFeedForNewAddress: $address")
        val feedRequest = feedDataManager.getFeedRequestWithAddress(address)
        Log.d(TAG,"refreshFeedForNewAddress feedRequest: $feedRequest")
        getFeedWith(feedRequest)
    }


    val feedResultData: SingleLiveEvent<OldFeedEvent> = SingleLiveEvent()
    data class OldFeedEvent(val isSuccess: Boolean = false, val feedArr: List<Feed>?)
    private fun getFeedWith(feedRequest: FeedRequest) {
        if(validFeedRequest(feedRequest)){
            viewModelScope.launch {
//                progressData.startProgress()
                val feedRepository = feedRepository.getFeed(feedRequest)
//                progressData.endProgress()
                when (feedRepository.type) {
                    FeedRepository.FeedRepoStatus.SERVER_ERROR -> {
                        Log.d(TAG, "NetworkError")
//                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                    FeedRepository.FeedRepoStatus.SOMETHING_WENT_WRONG -> {
                        Log.d(TAG, "GenericError")
//                        errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
                    }
                    FeedRepository.FeedRepoStatus.SUCCESS -> {
                        Log.d(TAG, "Success")
                        feedResultData.postValue(OldFeedEvent(true, feedRepository.feed))
                    }
                    else -> {
                        Log.d(TAG, "NetworkError")
//                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                }
            }
        }else{
            Log.d("wowFeedVM","getFeed setLocationListener")
            feedResultData.postValue(OldFeedEvent(false,null))
        }
    }


    private fun validFeedRequest(feedRequest: FeedRequest): Boolean {
        return (feedRequest.lat != null && feedRequest.lng != null) || feedRequest.addressId != null
    }

    fun getEaterFirstName(): String?{
        return feedDataManager.getUser()?.firstName
    }



    val getCookEvent: SingleLiveEvent<CookEvent> = SingleLiveEvent()
    data class CookEvent(val isSuccess: Boolean = false, val cook: Cook?)
    fun getCurrentCook(id: Long) {//todo - nyc
//        val currentAddress = eaterDataManager.getLastChosenAddress()
//        api.getCook(cookId = id, lat = currentAddress?.lat, lng = currentAddress?.lng).enqueue(object: Callback<ServerResponse<Cook>>{
//            override fun onResponse(call: Call<ServerResponse<Cook>>, response: Response<ServerResponse<Cook>>) {
//                if(response.isSuccessful){
//                    val cook = response.body()?.data
//                    Log.d("wowFeedVM","getCurrentCook success: ")
//                    getCookEvent.postValue(CookEvent(true, cook))
//                }else{
//                    Log.d("wowFeedVM","getCurrentCook fail")
//                    getCookEvent.postValue(CookEvent(false,null))
//                }
//            }
//
//            override fun onFailure(call: Call<ServerResponse<Cook>>, t: Throwable) {
//                Log.d("wowFeedVM","getCurrentCook big fail")
//                getCookEvent.postValue(CookEvent(false,null))
//            }
//        })
    }

//    fun getShareText(): String {
//        val inviteUrl = eaterDataManager.currentEater?.shareCampaign?.inviteUrl
//        val text = eaterDataManager.currentEater?.shareCampaign?.shareText
//        return "$text \n $inviteUrl"
//    }



    companion object{
        const val TAG = "wowFeedVM"
    }


}