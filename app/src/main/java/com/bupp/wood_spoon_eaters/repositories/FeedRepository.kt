package com.bupp.wood_spoon_eaters.repositories

import android.util.Log
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.base_repos.FeedRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedRepository(private val apiService: FeedRepositoryImpl) {


    data class FeedRepoResult(val type: FeedRepoStatus, val feed: List<Feed>? = null)
    enum class FeedRepoStatus {
        EMPTY,
        SUCCESS,
        SERVER_ERROR,
        SOMETHING_WENT_WRONG,
    }


    suspend fun getFeed(feedRequest: FeedRequest): FeedRepoResult {
        val result = withContext(Dispatchers.IO){
            apiService.getFeed(feedRequest.lat, feedRequest.lng, feedRequest.addressId, feedRequest.timestamp)
        }
        result.let{
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d("wowUserRepository","initUserRepo - NetworkError")
                    FeedRepoResult(FeedRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d("wowUserRepository","initUserRepo - GenericError")
                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d("wowUserRepository","initUserRepo - Success")
                    FeedRepoResult(FeedRepoStatus.SUCCESS, result.value.data)
                }
                else -> {
                    Log.d("wowUserRepository","initUserRepo - wsError")
                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }




}