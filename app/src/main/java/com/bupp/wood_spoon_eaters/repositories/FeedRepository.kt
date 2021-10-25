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
    private val apiService: FeedRepositoryImpl, private val flavorConfigManager: FlavorConfigManager, private val cartManager: CartManager) {


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
//            apiService.getFeed(40.845381, -73.866364, null, feedRequest.timestamp)
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
            val baseUrl = flavorConfigManager.getBaseUrl()
            apiService.getHrefCollection(baseUrl + href)
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


    private fun processFeedData(feedResult: FeedResult?): List<FeedAdapterItem> {
        Log.d("wowProcessFeedData", "start ----")
        var localId: Long = -1
        val feedData = mutableListOf<FeedAdapterItem>()
        feedResult?.sections?.forEachIndexed { feedSectionIndex, feedSection ->
            feedSection.title?.let {
                localId++
                feedData.add(FeedAdapterTitle(it, localId))
                Log.d("wowProcessFeedData", "adding title - $localId")
            }
            feedSection.href?.let {
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
                    is FeedRestaurantSection -> {
                        Log.d("wowProcessFeedData", "adding rest  - $localId")
                        if (isLargeItems) {
                            feedData.add(FeedAdapterLargeRestaurant(feedSectionCollectionItem, localId))
                        } else {
                            feedData.add(
                                FeedAdapterRestaurant(
                                    id = localId,
                                    restaurantSection = feedSectionCollectionItem,
                                    sectionTitle = feedSection.title,
                                    sectionOrder = feedSectionIndex + 1,
                                    restaurantOrderInSection = index + 1
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
            section.href?.let {
                if (it == href) {
                    Log.d("wowProcessFeedData", "handeling href - $href")
                    data?.let { data ->
                        if (data.isNotEmpty() && data[0].items!!.isNotEmpty()) {
                            Log.d("wowProcessFeedData", "update href section")
                            section.href = null
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

    companion object {
        const val TAG = "wowFeedRepo"
    }


}