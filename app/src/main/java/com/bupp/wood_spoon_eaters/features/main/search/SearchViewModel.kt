package com.bupp.wood_spoon_eaters.features.main.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.features.main.order_history.OrdersHistoryViewModel
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.FeedRepository
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import kotlinx.coroutines.launch

class SearchViewModel(
    private val metaDataRepository: MetaDataRepository, private val feedDataManager: FeedDataManager,
    private val feedRepository: FeedRepository, private val flowEventsManager: FlowEventsManager,
    private val eventsManager: EventsManager
) : ViewModel() {

    //    val searchLiveData = MutableLiveData<List<SearchBaseItem>>()
    val searchResultData: MutableLiveData<SearchLiveData> = MutableLiveData()

    data class SearchLiveData(val feedData: List<FeedAdapterItem>?, val isLargeItems: Boolean = false)

    private var searchTagData: MutableList<FeedAdapterItem> = mutableListOf()
    private var orderAgainData: List<FeedAdapterItem> = emptyList()
    private val searchDefaultData: HashMap<Int, List<FeedAdapterItem>> = hashMapOf()

    init {
        initList()
    }

    private fun initList() {
        searchDefaultData[SECTION_TAGS] = mutableListOf()
        searchDefaultData[SECTION_RECENT_ORDERS] = listOf()
    }

    //    private var curOrderAgainData: List<FeedAdapterItem>? = null
    fun getFinalAddressParams() = feedDataManager.getFinalAddressLiveDataParam()

//    fun showDefaultSearchData() {
//        val defaultData = mutableListOf<FeedAdapterItem>()
//        defaultData.add(getSearchTagsAdapterItems())
//        curOrderAgainData?.let {
//        defaultData.add(FeedAdapterTitle(title = "My recent orders", -1))
//            defaultData.addAll(it) }
//        searchResultData.postValue(SearchLiveData(defaultData))
//    }

    fun postDefaultData() {
        searchDefaultData.clear()
        searchDefaultData[SECTION_TAGS] = searchTagData
        searchDefaultData[SECTION_RECENT_ORDERS] = orderAgainData
        val data = mutableListOf<FeedAdapterItem>()
        searchDefaultData[SECTION_TAGS]?.let {
            if (it.isNotEmpty()) {
                data.add(FeedAdapterTitle(title = "Popular cuisines", -1))
                data.addAll(it)
            }
        }
        searchDefaultData[SECTION_RECENT_ORDERS]?.let {
            if (it.isNotEmpty()) {
                data.add(FeedAdapterTitle(title = "My recent orders", -1))
                data.addAll(it)
            }
        }
        if (data.isNotEmpty()) {
            searchResultData.postValue(SearchLiveData(data, false))
        } else {
            searchResultData.postValue(SearchLiveData(listOf(FeedAdapterEmptySearchTags()), false))
        }
    }

//    private fun getSearchTagsAdapterItems(): FeedAdapterSearchTag {
//        val searchTags = metaDataRepository.getSearchTags()
//        return FeedAdapterSearchTag(tags = searchTags)
//    }

    fun getSearchTags() {
        searchTagData.clear()
        viewModelScope.launch {
            val feedRequest = feedDataManager.getLastFeedRequest()
            if ((feedRequest.lat != null && feedRequest.lng != null) || feedRequest.addressId != null) {
                val searchTagsResult = feedRepository.getSearchTags(feedRequest)
                when (searchTagsResult.type) {
                    FeedRepository.FeedRepoStatus.SERVER_ERROR -> {
                        MTLogger.c(TAG, "getSearchTags - NetworkError")
//                      searchResultData.postValue(SearchLiveData(listOf(FeedAdapterNoNetworkSection(0)), result.isLargeItems))
                    }
                    FeedRepository.FeedRepoStatus.SOMETHING_WENT_WRONG -> {
                        MTLogger.c(TAG, "getSearchTags - GenericError")
                    }
                    FeedRepository.FeedRepoStatus.SUCCESS -> {
                        MTLogger.c(TAG, "getSearchTags - SUCCESS")
                        if(searchTagsResult.tags?.isNullOrEmpty() == false){
                            searchTagData = mutableListOf(FeedAdapterSearchTag(tags = searchTagsResult.tags))
                        }
                        postDefaultData()
                    }
                    else -> {
                        MTLogger.c(TAG, "getRecentOrders - NetworkError")
                    }
                }
            }
        }
    }

    fun getRecentOrders() {
        viewModelScope.launch {
            val feedRequest = feedDataManager.getLastFeedRequest()
            if ((feedRequest.lat != null && feedRequest.lng != null) || feedRequest.addressId != null) {
                val recentOrderResult = feedRepository.getRecentOrders(feedRequest)
                when (recentOrderResult.type) {
                    FeedRepository.FeedRepoStatus.SERVER_ERROR -> {
                        MTLogger.c(TAG, "getRecentOrders - NetworkError")
                        //                    searchResultData.postValue(SearchLiveData(listOf(FeedAdapterNoNetworkSection(0)), result.isLargeItems))
                    }
                    FeedRepository.FeedRepoStatus.SOMETHING_WENT_WRONG -> {
                        MTLogger.c(TAG, "getRecentOrders - GenericError")
                    }
                    FeedRepository.FeedRepoStatus.SUCCESS -> {
                        MTLogger.c(TAG, "getRecentOrders - SUCCESS")
                        orderAgainData = recentOrderResult.feed!!
                        postDefaultData()
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
            when (result.type) {
                FeedRepository.FeedRepoStatus.SERVER_ERROR -> {
                    MTLogger.c(TAG, "getFeedWith - NetworkError")
                    searchResultData.postValue(SearchLiveData(listOf(FeedAdapterNoNetworkSection(0)), result.isLargeItems))
                }
                FeedRepository.FeedRepoStatus.SOMETHING_WENT_WRONG -> {
                    MTLogger.c(TAG, "getFeedWith - GenericError")
                }
                FeedRepository.FeedRepoStatus.SUCCESS -> {
                    val hrefCount = getHrefItemsCount(result.feed)
                    MTLogger.c(TAG, "getFeedWith - Success - hrefCount: $hrefCount")
                    if (hrefCount > 0) {
                        handleHrefApiCalls(result.feed, hrefCount, input)
                    } else {
                        searchResultData.postValue(SearchLiveData(result.feed, result.isLargeItems))
                        val restaurants = result.feed?.filter { it.type == FeedAdapterViewType.RESTAURANT }
                        logQueryResult(input, restaurants?.size ?: 0)
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
                if (feedAdapterItem is FeedAdapterHref) {
                    feedAdapterItem.href?.let {
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
                                if (counter == hrefCount) {
                                    searchResultData.postValue(SearchLiveData(feedRepository.feed, isLargeItems = feedRepository.isLargeItems))
                                    val restaurants = feedRepository.feed?.filter { it.type == FeedAdapterViewType.RESTAURANT }
                                    logQueryResult(input, restaurants?.size ?: 0)
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
        val skeletons = mutableListOf<FeedAdapterSkeleton>()
        for (i in 0 until 2) {
            skeletons.add(FeedAdapterSkeleton())
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
