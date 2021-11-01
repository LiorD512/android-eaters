package com.bupp.wood_spoon_eaters.features.main.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.repositories.FeedRepository
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import kotlinx.coroutines.launch

class SearchViewModel(val metaDataRepository: MetaDataRepository, val feedDataManager: FeedDataManager,
    val feedRepository: FeedRepository) : ViewModel() {

    val searchLiveData = MutableLiveData<List<SearchBaseItem>>()

    init {
        showDefaultSearchData()
    }

    private val searchListData: MutableMap<Int, MutableList<SearchBaseItem>> = mutableMapOf()



    private fun showDefaultSearchData() {
        val defaultData = mutableListOf<SearchBaseItem>()
        defaultData.add(getSearchTagsAdapterItems())
        defaultData.addAll(getRecentOrderAdapterItems())
        searchLiveData.postValue(defaultData)
    }

    private fun getSearchTagsAdapterItems(): SearchAdapterTag {
        val searchTags = metaDataRepository.getSearchTags()
        return SearchAdapterTag(searchTags)
//        return SearchAdapterTag(arrayListOf("sababa", "asasas", "sds", "asdasd", "Sdads"))
    }

    private fun getRecentOrders(): List<Order> {
        return emptyList()
    }

    private fun getRecentOrderAdapterItems(): List<SearchAdapterRecentOrder>{
        val recentOrderItems = mutableListOf<SearchAdapterRecentOrder>()
        val recentOrders = getRecentOrders()
        recentOrders.forEach {
            recentOrderItems.add(SearchAdapterRecentOrder(it))
        }
        return recentOrderItems
    }

    private fun loadSearchTags() {

    }

    fun searchInput(input: String) {
        viewModelScope.launch {
            val feedRequest = feedDataManager.getLastFeedRequest()
            feedRepository.getFeedBySearch(input, feedRequest)
        }
    }


    companion object {
        const val TAG = "wowSearchVM"
        const val SECTION_TAGS = 0
        const val SECTION_RECENT_ORDERS = 1
        const val SECTION_TITLE = 2
    }

}
