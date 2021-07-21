package com.bupp.wood_spoon_eaters.repositories

import android.annotation.SuppressLint
import android.util.Log
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.base_repos.FeedRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedRepository(private val apiService: FeedRepositoryImpl) {


    data class ReviewResult(val type: FeedRepoStatus, val review: Review? = null)
    data class CookResult(val type: FeedRepoStatus, val cook: Cook? = null)
    data class FeedRepoResult(val type: FeedRepoStatus, val feed: List<FeedAdapterItem>? = null)
    enum class FeedRepoStatus {
        EMPTY,
        SUCCESS,
        SERVER_ERROR,
        SOMETHING_WENT_WRONG,
    }


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
                    FeedRepoResult(FeedRepoStatus.SUCCESS, feedData)
                }
                else -> {
                    Log.d(TAG, "getFeed - wsError")
                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }

    private fun processFeedData(feedResult: FeedResult?): List<FeedAdapterItem> {
        val feedData = mutableListOf<FeedAdapterItem>()
        feedResult?.sections?.forEach { section ->
            section.title?.let {
                feedData.add(FeedAdapterTitle(it))
            }
            section.collections?.forEach { collectionItem ->
                when (collectionItem) {
                    is FeedCampaignSection -> {
                        feedData.add(FeedAdapterCoupons(collectionItem))
                    }
                    is FeedRestaurantSection -> {
                        feedData.add(FeedAdapterRestaurant(collectionItem))
                    }
                }
            }
        }
        return feedData
    }

    suspend fun getCookById(cookId: Long, addressId: Long?, lat: Double?, lng: Double?): CookResult {
        val result = withContext(Dispatchers.IO) {
            apiService.getCookById(cookId, addressId, lat, lng)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "getCookById - NetworkError")
                    CookResult(FeedRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "getCookById - GenericError")
                    CookResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "getCookById - Success")
                    CookResult(FeedRepoStatus.SUCCESS, result.value.data)
                }
                else -> {
                    Log.d(TAG, "getCookById - wsError")
                    CookResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }

    suspend fun getCookReview(cookId: Long): ReviewResult {
        val result = withContext(Dispatchers.IO) {
            apiService.getCookReview(cookId)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "getCookReview - NetworkError")
                    ReviewResult(FeedRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "getCookReview - GenericError")
                    ReviewResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "getCookReview - Success")
                    ReviewResult(FeedRepoStatus.SUCCESS, result.value.data)
                }
                else -> {
                    Log.d(TAG, "getCookReview - wsError")
                    ReviewResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }

    data class SearchResult(val type: SearchStatus, val feed: List<Search>? = null)
    enum class SearchStatus {
        EMPTY,
        SUCCESS,
        SERVER_ERROR,
        SOMETHING_WENT_WRONG,
    }

    suspend fun getSearch(searchRequest: SearchRequest): SearchResult {
        val result = withContext(Dispatchers.IO) {
            apiService.search(searchRequest)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "getSearch - NetworkError")
                    SearchResult(SearchStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "getSearch - GenericError")
                    SearchResult(SearchStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "getSearch - Success")
                    if (result.value.data.isNullOrEmpty()) {
                        SearchResult(SearchStatus.EMPTY)
                    } else {
                        SearchResult(SearchStatus.SUCCESS, result.value.data)
                    }
                }
                else -> {
                    Log.d(TAG, "getSearch - wsError")
                    SearchResult(SearchStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }

    companion object {
        const val TAG = "wowFeedRepo"
    }


}