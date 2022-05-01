package com.bupp.wood_spoon_eaters.repositories

import android.annotation.SuppressLint
import android.util.Log
import com.bupp.wood_spoon_eaters.common.FlavorConfigManager
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.base_repos.FeedRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedRepository(
    private val apiService: FeedRepositoryImpl, private val flavorConfigManager: FlavorConfigManager, private val cartManager: CartManager,
    private val metaDataManager: MetaDataRepository
) {


    private var lastFeedDataResult: FeedResult? = null

    data class FeedRepoResult(val type: FeedRepoStatus, val feed: List<FeedAdapterItem>? = null, val isLargeItems: Boolean = false)
    enum class FeedRepoStatus {
        SUCCESS,
        HREF_SUCCESS,
        SERVER_ERROR,
        SOMETHING_WENT_WRONG,
    }

    //todo - change this when server is ready
    private val isLargeItems = false

    @SuppressLint("LogNotTimber")
    suspend fun getFeed(feedRequest: FeedRequest): FeedRepoResult {
        val result = withContext(Dispatchers.IO) {
            apiService.getFeed(feedRequest.lat, feedRequest.lng, feedRequest.addressId, feedRequest.timestamp)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "getFeed - NetworkError")
                    FeedRepoResult(FeedRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "getFeed - GenericError")
                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "getFeed - Success")
                    val feedData = processFeedData(result.value.data)
                    FeedRepoResult(FeedRepoStatus.SUCCESS, feedData, isLargeItems)
                }
                else -> {
                    Log.d(TAG, "getFeed - wsError")
                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }

    @SuppressLint("LogNotTimber")
    suspend fun getFeedHref(href: String): FeedRepoResult {
        val result = withContext(Dispatchers.IO) {
//            val baseUrl = flavorConfigManager.getBaseUrl()
            apiService.getHrefCollection(href)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "getFeedHref - NetworkError")
                    FeedRepoResult(FeedRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "getFeedHref - GenericError")
                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "getFeedHref - Success")
                    val feedData = processFeedHrefData(result.value.data, href)
//                    val feedData = processFeedHrefData(emptyList(), href) //todo = check campaigns
                    FeedRepoResult(FeedRepoStatus.HREF_SUCCESS, feedData, isLargeItems)
                }
                else -> {
                    Log.d(TAG, "getFeed - wsError")
                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }


    private fun processFeedData(feedResult: FeedResult?, input: String? = null): List<FeedAdapterItem> {
        Log.d("wowProcessFeedData", "start ----")
        var localId: Long = -1
        val feedData = mutableListOf<FeedAdapterItem>()
        feedResult?.sections?.forEachIndexed { feedSectionIndex, feedSection ->
            if(input != null && feedData.size == 0 && (feedResult.sections.isNotEmpty() && feedResult.sections[0].collections?.get(0) ?: null is FeedRestaurantSection)){
                localId++
                val searchTitle = "Results for “${input.capitalize()}”"
                feedData.add(FeedAdapterSearchTitle(searchTitle, localId))
            }
            feedSection.title?.let {
                localId++
                feedData.add(FeedAdapterTitle(it, localId))
                Log.d("wowProcessFeedData", "adding title - $localId")
            }
            feedSection.full_href?.let {
                localId++
                feedData.add(FeedAdapterHref(it, localId))
                Log.d("wowProcessFeedData", "adding href  - $localId")
            }
            feedSection.collections?.forEachIndexed { index, feedSectionCollectionItem ->
                localId++
                when (feedSectionCollectionItem) {
                    is FeedCampaignSection -> {
                        Log.d("wowProcessFeedData", "adding camp  - $localId")
                        feedData.add(FeedAdapterCoupons(feedSectionCollectionItem, localId))
                    }
                    is FeedIsEmptySection -> {
                        Log.d("wowProcessFeedData", "adding empty - $localId")
                        feedData.add(FeedAdapterEmptyFeed(feedSectionCollectionItem, localId, isCartEmpty()))
                    }
                    is FeedSingleEmptySection -> {
                        Log.d("wowProcessFeedData", "adding empty2 - $localId")
                        feedData.add(FeedAdapterEmptySection(feedSectionCollectionItem, localId))
                    }
                    is FeedSearchEmptySection -> {
                        Log.d("wowProcessFeedData", "adding search empty - $localId")
                        feedData.add(FeedAdapterEmptySearch(feedSectionCollectionItem, localId))
                    }
                    is FeedComingSoonSection -> {
                        Log.d("wowProcessFeedData", "adding coming soon section")
                        feedData.add(FeedAdapterComingSoonSection(feedSectionCollectionItem, localId))
                    }
                    is FeedRestaurantSection -> {
                        Log.d("wowProcessFeedData", "adding rest  - $localId")
                        feedSectionCollectionItem.flagUrl = metaDataManager.getCountryFlagById(feedSectionCollectionItem.countryId)
                        feedSectionCollectionItem.countryIso = metaDataManager.getCountryIsoById(feedSectionCollectionItem.countryId)
                        if (isLargeItems) {
                            feedData.add(FeedAdapterLargeRestaurant(feedSectionCollectionItem, localId))
                        } else {
                            feedData.add(
                                FeedAdapterRestaurant(
                                    id = localId,
                                    restaurantSection = feedSectionCollectionItem,
                                    sectionTitle = feedSection.title,
                                    sectionOrder = feedSectionIndex + 1,
                                    restaurantOrderInSection = index + 1,

                                )
                            )
                        }
                    }
                }
            }
        }
        lastFeedDataResult = feedResult
        return feedData
    }

    private fun isCartEmpty(): Boolean {
        return cartManager.isCartEmpty()
    }

    private fun processFeedHrefData(data: List<FeedSectionCollectionItem>?, href: String): List<FeedAdapterItem> {
        val tempFeedResult = mutableListOf<FeedSection>()
        lastFeedDataResult?.sections?.forEachIndexed { index, section ->
            section.full_href?.let {
                if (it == href) {
                    Log.d("wowProcessFeedData", "handeling href - $href")
                    data?.let { data ->
                        if (data.isNotEmpty() && data[0].items!!.isNotEmpty()) {
                            Log.d("wowProcessFeedData", "update href section")
                            section.full_href = null
                            lastFeedDataResult?.sections!![index].collections = data.toMutableList()
                        } else {
                            Log.d("wowProcessFeedData", "href empty")
                            return@forEachIndexed
                        }

                    }
                }
            }
            tempFeedResult.add(section)
        }
        return processFeedData(FeedResult(tempFeedResult))
    }

    suspend fun getFeedBySearch(input: String, feedRequest: FeedRequest): FeedRepoResult {
        val result = withContext(Dispatchers.IO) {
            val lat = feedRequest.lat
            val lng = feedRequest.lng
            val addressId = feedRequest.addressId
            val timestamp = feedRequest.timestamp
            apiService.search(input, lat, lng, addressId, timestamp)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "getFeedBySearch - NetworkError")
                    FeedRepoResult(FeedRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "getFeedBySearch - GenericError")
                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "getFeedBySearch - Success")
                    val feedData = processFeedData(result.value.data, input)
                    FeedRepoResult(FeedRepoStatus.SUCCESS, feedData, isLargeItems)
                }
                else -> {
                    Log.d(TAG, "getFeedBySearch - wsError")
                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }

    suspend fun getRecentOrders(feedRequest: FeedRequest): FeedRepoResult {
        val result = withContext(Dispatchers.IO) {
            val lat = feedRequest.lat
            val lng = feedRequest.lng
            val addressId = feedRequest.addressId
            val timestamp = feedRequest.timestamp
            apiService.getRecentOrders(lat, lng, addressId, timestamp)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "getRecentOrders - NetworkError")
                    FeedRepoResult(FeedRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "getRecentOrders - GenericError")
                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "getRecentOrders - Success")
                    val data = result.value.data
                    val feedData = processFeedData(FeedResult(listOf(FeedSection(collections = data as MutableList<FeedSectionCollectionItem>))))
                    FeedRepoResult(FeedRepoStatus.SUCCESS, feedData, isLargeItems)
                }
                else -> {
                    Log.d(TAG, "getRecentOrders - wsError")
                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }

    data class SearchTagsResult(val type: FeedRepoStatus, val tags: List<String>? = null)
    suspend fun getSearchTags(feedRequest: FeedRequest): SearchTagsResult {
        val result = withContext(Dispatchers.IO) {
            val lat = feedRequest.lat
            val lng = feedRequest.lng
            val addressId = feedRequest.addressId
//            val timestamp = feedRequest.timestamp
            apiService.getSearchTags(lat, lng, addressId)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "getSearchTags - NetworkError")
                    SearchTagsResult(FeedRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "getSearchTagss - GenericError")
                    SearchTagsResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "getSearchTags - Success")
                    val data = result.value.data
                    SearchTagsResult(FeedRepoStatus.SUCCESS, data)
                }
                else -> {
                    Log.d(TAG, "getSearchTags - wsError")
                    SearchTagsResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }

    companion object {
        const val TAG = "wowFeedRepo"
    }


}