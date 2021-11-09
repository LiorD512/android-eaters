package com.bupp.wood_spoon_eaters.features.main.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.features.main.feed.FeedViewModel
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.FeedRepository
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import kotlinx.coroutines.launch

class SearchViewModel(val metaDataRepository: MetaDataRepository, val feedDataManager: FeedDataManager,
    val feedRepository: FeedRepository) : ViewModel() {

//    val searchLiveData = MutableLiveData<List<SearchBaseItem>>()
    val searchResultData: MutableLiveData<SearchLiveData> = MutableLiveData()
    data class SearchLiveData(val feedData: List<FeedAdapterItem>?, val isLargeItems: Boolean = false)

    init {
        showDefaultSearchData()
    }

    fun showDefaultSearchData() {
        val defaultData = mutableListOf<FeedAdapterItem>()
        defaultData.add(getSearchTagsAdapterItems())
        defaultData.addAll(getRecentOrderAdapterItems())
        searchResultData.postValue(SearchLiveData(defaultData))
    }

    private fun getSearchTagsAdapterItems(): FeedAdapterSearchTag {
        val searchTags = metaDataRepository.getSearchTags()
        return FeedAdapterSearchTag(tags = searchTags)
//        return SearchAdapterTag(arrayListOf("sababa", "asasas", "sds", "asdasd", "Sdads"))
    }

    private fun getRecentOrders(): List<Order> {
        return emptyList()
    }

    private fun getRecentOrderAdapterItems(): List<FeedAdapterItem>{
        val recentOrderItems = mutableListOf<FeedAdapterItem>()
        val recentOrders = getRecentOrders()
        recentOrders.forEach {
            //todo - fix this when server is ready
//            recentOrderItems.add(FeedAdapterItem(it))
        }
        return recentOrderItems
    }

    fun searchInput(input: String) {
        viewModelScope.launch {
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
                        handleHrefApiCalls(result.feed, hrefCount)
                    }else{
                        searchResultData.postValue(SearchLiveData(result.feed, result.isLargeItems))
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
                                    searchResultData.postValue(SearchLiveData(feedRepository.feed, isLargeItems = feedRepository.isLargeItems))
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


    companion object {
        const val TAG = "wowSearchVM"
        const val SECTION_TAGS = 0
        const val SECTION_RECENT_ORDERS = 1
        const val SECTION_TITLE = 2
    }

}
