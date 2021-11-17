package com.bupp.wood_spoon_eaters.features.main.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.FeedRepository
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val metaDataRepository: MetaDataRepository, private val feedDataManager: FeedDataManager,
                      private val feedRepository: FeedRepository, private val flowEventsManager: FlowEventsManager,
                      private val eventsManager: EventsManager) : ViewModel() {

//    val searchLiveData = MutableLiveData<List<SearchBaseItem>>()
    val searchResultData: MutableLiveData<SearchLiveData> = MutableLiveData()
    data class SearchLiveData(val feedData: List<FeedAdapterItem>?, val isLargeItems: Boolean = false)

//    init {
//        getRecentOrders()
//    }

    private var curOrderAgainData: List<FeedAdapterItem>? = null
    fun getFinalAddressParams() = feedDataManager.getFinalAddressLiveDataParam()

    fun showDefaultSearchData() {
        val defaultData = mutableListOf<FeedAdapterItem>()
        defaultData.add(getSearchTagsAdapterItems())
        curOrderAgainData?.let {
        defaultData.add(FeedAdapterTitle(title = "My recent orders", -1))
            defaultData.addAll(it) }
        searchResultData.postValue(SearchLiveData(defaultData))
    }

    private fun getSearchTagsAdapterItems(): FeedAdapterSearchTag {
        val searchTags = metaDataRepository.getSearchTags()
        return FeedAdapterSearchTag(tags = searchTags)
    }

    fun getRecentOrders() {
        viewModelScope.launch {
            val feedRequest = feedDataManager.getLastFeedRequest()
            if((feedRequest.lat != null && feedRequest.lng != null) || feedRequest.addressId != null){
                val recentOrderResult = feedRepository.getRecentOrders(feedRequest)
                when(recentOrderResult.type){
                    FeedRepository.FeedRepoStatus.SERVER_ERROR -> {
                        MTLogger.c(TAG, "getRecentOrders - NetworkError")
    //                    searchResultData.postValue(SearchLiveData(listOf(FeedAdapterNoNetworkSection(0)), result.isLargeItems))
                    }
                    FeedRepository.FeedRepoStatus.SOMETHING_WENT_WRONG -> {
                        MTLogger.c(TAG, "getRecentOrders - GenericError")
                    }
                    FeedRepository.FeedRepoStatus.SUCCESS -> {
                        MTLogger.c(TAG, "getRecentOrders - SUCCESS")
                        curOrderAgainData = recentOrderResult.feed
                        showDefaultSearchData()
                    }
                    else -> {
                        MTLogger.c(TAG, "getRecentOrders - NetworkError")
                    }
                }
            }
        }
    }

//    private fun getRecentOrderAdapterItems(): List<FeedAdapterItem>{
//        val recentOrderItems = mutableListOf<FeedAdapterItem>()
//        val recentOrders = getRecentOrders()
//        recentOrders.forEach {
//            //todo - fix this when server is ready
////            recentOrderItems.add(FeedAdapterRestaurant(restaurantSection = it))
//        }
//        return recentOrderItems
//    }



    fun searchInput(input: String) {
        viewModelScope.launch {
            searchResultData.postValue(getSkeletonItems())
            val feedRequest = feedDataManager.getLastFeedRequest()
            val result = feedRepository.getFeedBySearch(input, feedRequest)
            when(result.type){
                FeedRepository.FeedRepoStatus.SERVER_ERROR -> {
                    MTLogger.c(TAG, "getFeedWith - NetworkError")
//                    searchResultData.postValue(SearchLiveData(listOf(FeedAdapterNoNetworkSection(0)), result.isLargeItems))
                }
                FeedRepository.FeedRepoStatus.SOMETHING_WENT_WRONG -> {
                    MTLogger.c(TAG, "getFeedWith - GenericError")
                }
                FeedRepository.FeedRepoStatus.SUCCESS -> {
                    val hrefCount = getHrefItemsCount(result.feed)
                    MTLogger.c(TAG, "getFeedWith - Success - hrefCount: $hrefCount")
                    if(hrefCount > 0){
                        handleHrefApiCalls(result.feed, hrefCount, input)
                    }else{
                        searchResultData.postValue(SearchLiveData(result.feed, result.isLargeItems))
                        logQueryResult(input, result.feed?.size ?: 0)
                    }
                }
                else -> {
                    MTLogger.c(TAG, "getFeedWith - NetworkError")
                }
            }
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

    private fun handleHrefApiCalls(feed: List<FeedAdapterItem>?, hrefCount: Int, input: String) {
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
                                    searchResultData.postValue(SearchLiveData(feedRepository.feed, isLargeItems = feedRepository.isLargeItems))
                                    logQueryResult(input, feedRepository.feed?.size ?: 0)
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

    private fun getSkeletonItems(): SearchLiveData {
        val skeletons = mutableListOf<FeedAdapterSearchSkeleton>()
        for(i in 0 until 2){
            skeletons.add(FeedAdapterSearchSkeleton())
        }
        return SearchLiveData(skeletons)
    }

    fun logTagEvent(eventName: String, tag: String) {
        logEvent(eventName, mapOf(Pair("cuisine_name", tag)))
    }

    private fun logQueryResult(query: String, count: Int) {
        val data = mutableMapOf<String, String>()
        data["search_text"] = query
        data["results_count"] = count.toString()
        logEvent(Constants.EVENT_SEARCH_QUERY, data)
    }

    fun logRestaurantClick(restaurantInitParams: RestaurantInitParams) {
        val data = mutableMapOf<String, String>()
        data["home_chef_id"] = restaurantInitParams.restaurantId.toString()
        data["home_chef_name"] = restaurantInitParams.chefName.toString()
        data["kitchen_name"] = restaurantInitParams.restaurantName.toString()
        data["home_chef_rating"] = restaurantInitParams.rating.toString()
        data["home_chef_availability"] = restaurantInitParams.cookingSlot?.getAvailabilityString().toString()
        logEvent(Constants.EVENT_SEARCH_RESTAURANT_CLICK, data)
    }

    fun logEvent(eventName: String, params: Map<String, String>? = null) {
        eventsManager.logEvent(eventName, params)
    }




//    private fun getSendOtpData(isSuccess: Boolean): Map<String, String> {
//        val data = mutableMapOf<String, String>()
//        data["number"] = this.phonePrefix+this.phone
//        data["success"] = isSuccess.toString()
//        return data
//    }


    companion object {
        const val TAG = "wowSearchVM"
        const val SECTION_TAGS = 0
        const val SECTION_RECENT_ORDERS = 1
        const val SECTION_TITLE = 2
    }

}
