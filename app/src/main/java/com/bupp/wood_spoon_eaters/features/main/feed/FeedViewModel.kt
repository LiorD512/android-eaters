package com.bupp.wood_spoon_eaters.features.main.feed

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.SingleColumnTimePickerBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.managers.CampaignManager
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.FeedRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils
import kotlinx.coroutines.launch

class FeedViewModel(
    private val feedDataManager: FeedDataManager, private val feedRepository: FeedRepository, private val flowEventsManager: FlowEventsManager,
    campaignManager: CampaignManager, private val eventsManager: EventsManager
): ViewModel() {

    val progressData = ProgressData()

    fun initFeed(){
        feedSkeletonEvent.postValue(getSkeletonItems())
        feedDataManager.initFeedDataManager()

        viewModelScope.launch {
            flowEventsManager.fireEvent(FlowEventsManager.FlowEvents.VISIT_FEED)
        }
    }

    fun getLocationLiveData() = feedDataManager.getLocationLiveData()
    fun getFinalAddressParams() = feedDataManager.getFinalAddressLiveDataParam()

    val feedUiStatusLiveData = feedDataManager.getFeedUiStatus()
    val campaignLiveData = campaignManager.getCampaignLiveData()

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
    }

    val feedSkeletonEvent = MutableLiveData<FeedLiveData>()
    val feedResultData: MutableLiveData<FeedLiveData> = MutableLiveData()
    data class FeedLiveData(val feedData: List<FeedAdapterItem>?, val isLargeItems: Boolean = false)
    private fun getFeedWith(feedRequest: FeedRequest) {
        if(validFeedRequest(feedRequest)){
            feedSkeletonEvent.postValue(getSkeletonItems())
            viewModelScope.launch {
//                progressData.startProgress()
                val feedRepository = feedRepository.getFeed(feedRequest)
                when (feedRepository.type) {
                    FeedRepository.FeedRepoStatus.SERVER_ERROR -> {
                        MTLogger.c(TAG, "getFeedWith - NetworkError")
//                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
//                        progressData.endProgress()
                    }
                    FeedRepository.FeedRepoStatus.SOMETHING_WENT_WRONG -> {
                        MTLogger.c(TAG, "getFeedWith - GenericError")
//                        errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
//                        progressData.endProgress()
                    }
                    FeedRepository.FeedRepoStatus.SUCCESS -> {
                        val hrefCount = getHrefItemsCount(feedRepository.feed)
                        MTLogger.c(TAG, "getFeedWith - Success - hrefCount: $hrefCount  ")
                        if(hrefCount > 0){
                            handleHrefApiCalls(feedRepository.feed, hrefCount)
                        }else{
                            feedResultData.postValue(FeedLiveData(feedRepository.feed, feedRepository.isLargeItems))
                        }
//                        progressData.endProgress()
                    }
                    else -> {
                        MTLogger.c(TAG, "getFeedWith - NetworkError")
//                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
//                        progressData.endProgress()
                    }
                }
//                progressData.endProgress()
            }
        }else{
            MTLogger.c("wowFeedVM","getFeed setLocationListener")
            feedResultData.postValue(FeedLiveData(null))
            progressData.endProgress()
        }
    }

    private fun getHrefItemsCount(feed: List<FeedAdapterItem>?): Int {
        var hrefCounter = 0
        feed?.let {
            it.forEach { feedAdapterItem ->
                if (feedAdapterItem is FeedAdapterHref) {
                    hrefCounter++
                }
            }
        }
        MTLogger.c(TAG, "getHrefItemsCount: $hrefCounter")
        return hrefCounter
    }

    private fun handleHrefApiCalls(feed: List<FeedAdapterItem>?, hrefCount: Int) {
        viewModelScope.launch {
            var counter = 0
            feed?.forEach { feedAdapterItem ->
                if(feedAdapterItem is FeedAdapterHref){
                    feedAdapterItem.href?.let{
                        val feedRepository = feedRepository.getFeedHref(it)
                        counter++
                        when (feedRepository.type) {
                            FeedRepository.FeedRepoStatus.SERVER_ERROR -> {
                                MTLogger.c(TAG, "handleHrefApiCalls - NetworkError")
                            }
                            FeedRepository.FeedRepoStatus.SOMETHING_WENT_WRONG -> {
                                MTLogger.c(TAG, "handleHrefApiCalls - GenericError")
                            }
                            FeedRepository.FeedRepoStatus.HREF_SUCCESS -> {
                                MTLogger.c(TAG, "handleHrefApiCalls - Success counter: $counter, hrefCounter: $hrefCount")
                                if(counter == hrefCount){
                                    feedResultData.postValue(FeedLiveData(feedRepository.feed, isLargeItems = feedRepository.isLargeItems))
                                }
                            }
                            else -> {
                                MTLogger.c(TAG, "handleHrefApiCalls - NetworkError")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getSkeletonItems(): FeedLiveData {
        val skeletons = mutableListOf<FeedAdapterSkeleton>()
        for(i in 0 until 2){
            skeletons.add(FeedAdapterSkeleton())
        }
        return FeedLiveData(skeletons)
    }

    private fun validFeedRequest(feedRequest: FeedRequest): Boolean {
        return (feedRequest.lat != null && feedRequest.lng != null) || feedRequest.addressId != null
    }

//    fun getEaterFirstName(): String?{
//        return feedDataManager.getUser()?.firstName
//    }

    fun onTimePickerChanged(deliveryTimeParam: SingleColumnTimePickerBottomSheet.DeliveryTimeParam?) {
        feedDataManager.onTimePickerChanged(deliveryTimeParam)
        logEvent(Constants.EVENT_CHANGE_DELIVERY_DATE, getDateChangedData(deliveryTimeParam))
        onPullToRefresh()
    }

    fun logEvent(eventName: String, params: Map<String, String>? = null) {
        when(eventName){
            Constants.EVENT_CHANGE_DELIVERY_DATE -> {
                eventsManager.logEvent(eventName, params)
            }
            else -> {
                eventsManager.logEvent(eventName)
            }
        }
    }

    private fun getDateChangedData(deliveryTimeParam: SingleColumnTimePickerBottomSheet.DeliveryTimeParam?): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["selected_date"] = DateUtils.parseDateToUsDate(deliveryTimeParam?.date)
        data["day"] = DateUtils.parseDateToDayName(deliveryTimeParam?.date)
        return data
    }


    companion object{
        const val TAG = "wowFeedVM"
    }


}