package com.bupp.wood_spoon_eaters.features.main.feed

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.managers.CampaignManager
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.FeedRepository
import kotlinx.coroutines.launch

class FeedViewModel(
    private val feedDataManager: FeedDataManager, private val feedRepository: FeedRepository, private val flowEventsManager: FlowEventsManager,
    private val campaignManager: CampaignManager
): ViewModel() {

    val progressData = ProgressData()

    fun initFeed(){
        feedDataManager.initFeedDataManager()

        viewModelScope.launch {
            flowEventsManager.fireEvent(FlowEventsManager.FlowEvents.VISIT_FEED)
        }
    }


    fun getLocationLiveData() = feedDataManager.getLocationLiveData()
    fun getFinalAddressParams() = feedDataManager.getFinalAddressLiveDataParam()
    fun getDeliveryTimeLiveData() = feedDataManager.getDeliveryTimeLiveData()

    val feedUiStatusLiveData = feedDataManager.getFeedUiStatus()
    val campaignLiveData = campaignManager.getCampaignLiveData()

    val favoritesLiveData = feedDataManager.getFavoritesLiveData
    fun refreshFavorites() {
        viewModelScope.launch {
            feedDataManager.refreshFavorites()
        }
    }


    fun refreshFeedByLocationIfNeeded() {
        feedDataManager.refreshFeedByLocationIfNeeded()
    }

    fun refreshFeedForNewAddress(address: Address) {
        Log.d(TAG,"refreshFeedForNewAddress: $address")
        val feedRequest = feedDataManager.getFeedRequestWithAddress(address)
        Log.d(TAG,"refreshFeedForNewAddress feedRequest: $feedRequest")
        getFeedWith(feedRequest)
    }

    fun onPullToRefresh() {
        val feedRequest = feedDataManager.getLastFeedRequest()
        getFeedWith(feedRequest)
        refreshFavorites()
    }

    val feedResultData: MutableLiveData<FeedLiveData> = MutableLiveData()
    data class FeedLiveData(val feedData: List<FeedAdapterItem>?)
    private fun getFeedWith(feedRequest: FeedRequest) {
        if(validFeedRequest(feedRequest)){
            viewModelScope.launch {
                progressData.startProgress()
                val feedRepository = feedRepository.getFeed(feedRequest)
                when (feedRepository.type) {
                    FeedRepository.FeedRepoStatus.SERVER_ERROR -> {
                        Log.d(TAG, "NetworkError")
//                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                        progressData.endProgress()
                    }
                    FeedRepository.FeedRepoStatus.SOMETHING_WENT_WRONG -> {
                        Log.d(TAG, "GenericError")
//                        errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
                        progressData.endProgress()
                    }
                    FeedRepository.FeedRepoStatus.SUCCESS -> {
                        Log.d(TAG, "Success")
                        feedResultData.postValue(FeedLiveData(feedRepository.feed))
                    }
                    else -> {
                        Log.d(TAG, "NetworkError")
//                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                        progressData.endProgress()
                    }
                }
//                progressData.endProgress()
            }
        }else{
            Log.d("wowFeedVM","getFeed setLocationListener")
            feedResultData.postValue(FeedLiveData(null))
            progressData.endProgress()
        }
    }

    private fun validFeedRequest(feedRequest: FeedRequest): Boolean {
        return (feedRequest.lat != null && feedRequest.lng != null) || feedRequest.addressId != null
    }

    fun getEaterFirstName(): String?{
        return feedDataManager.getUser()?.firstName
    }



    companion object{
        const val TAG = "wowFeedVM"
    }


}