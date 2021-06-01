package com.bupp.wood_spoon_eaters.features.main.search

import android.util.Log
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.managers.SearchManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.features.main.feed.FeedViewModel
import com.bupp.wood_spoon_eaters.repositories.FeedRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel(val api: ApiService, val metaDataRepository: MetaDataRepository, private val searchManager: SearchManager, val eaterDataManager: EaterDataManager) : ViewModel() {

//    val getCookEvent: SingleLiveEvent<Cook?> = SingleLiveEvent()

    data class LikeEvent(val isSuccess: Boolean = false)
    val likeEvent: SingleLiveEvent<LikeEvent> = SingleLiveEvent()

    data class SearchEvent(
        val isSuccess: Boolean = false,
        val cooks: List<Cook>?,
        val dishes: List<Dish>?
    )
    data class NextSearchEvent(
        val isSuccess: Boolean = false,
        val searchResponse: ArrayList<Search>?,
        val searchId: Long
    )

    data class SuggestionEvent(
        val isSuccess: Boolean = false
    )

    val searchEvent: SingleLiveEvent<SearchEvent> = SingleLiveEvent()
    val nextSearchEvent: SingleLiveEvent<NextSearchEvent> = SingleLiveEvent()
    val suggestionEvent: SingleLiveEvent<SuggestionEvent> = SingleLiveEvent()

    var searchResult: List<Search>? = null


    fun getCuisineLabels(): List<CuisineLabel> {
        return metaDataRepository.getCuisineList()
    }

    fun search(str: String) {
        viewModelScope.launch {
            val searchResult = searchManager.doSearch(str)
            handleResult(searchResult)
        }
    }

    private fun handleResult(searchResult: FeedRepository.SearchResult) {
        when(searchResult.type){
            FeedRepository.SearchStatus.EMPTY -> {

            }
            FeedRepository.SearchStatus.SUCCESS -> {
                arrangeData(searchResult.feed)
            }
            FeedRepository.SearchStatus.SOMETHING_WENT_WRONG -> {

            }
            FeedRepository.SearchStatus.SERVER_ERROR -> {

            }
        }
    }

//    fun getCurrentCook(cookId: Long) {
//        viewModelScope.launch {
////                progressData.startProgress()
//            val result = searchManager.getCookById(cookId)
////                progressData.endProgress()
//            when (result.type) {
//                FeedRepository.FeedRepoStatus.SERVER_ERROR -> {
//                    Log.d(FeedViewModel.TAG, "NetworkError")
////                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
//                }
//                FeedRepository.FeedRepoStatus.SOMETHING_WENT_WRONG -> {
//                    Log.d(FeedViewModel.TAG, "GenericError")
////                        errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
//                }
//                FeedRepository.FeedRepoStatus.SUCCESS -> {
//                    Log.d(FeedViewModel.TAG, "Success")
//                    getCookEvent.postValue(result.cook)
//                }
//                else -> {
//                    Log.d(FeedViewModel.TAG, "NetworkError")
////                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
//                }
//            }
//        }
//    }


    fun getDishesByCuisineId(cuisine: CuisineLabel) {
        viewModelScope.launch {
            val searchResult = searchManager.doSearch("", cuisine = cuisine)
            handleResult(searchResult)
        }
    }

    fun clearSearchQuery(){
        searchManager.updateCurSearch(q = null, cuisineIds = null)
    }


    private fun arrangeData(searchResult: List<Search>?) {
        this.searchResult = searchResult
        var cooks: ArrayList<Cook>? = null
        var dishes: ArrayList<Dish>? = null
        for(item in searchResult!!){
            if(item.hasCooks()){
                cooks = item.results as ArrayList<Cook>
            }
            if(item.hasDishes()){
                dishes = item.results as ArrayList<Dish>
            }
        }
        searchEvent.postValue(SearchEvent(true, cooks, dishes))
    }


    fun suggestDish(dishName: String, dishDetails: String) {
        api.postDishSuggestion(dishName,dishDetails).enqueue(object: Callback<ServerResponse<Any>> {
            override fun onResponse(call: Call<ServerResponse<Any>>, response: Response<ServerResponse<Any>>) {
                if(response.isSuccessful){
                    Log.d("wowSearchVM","suggestDish success")
                    suggestionEvent.postValue(SuggestionEvent(true))
                }else{
                    Log.d("wowSearchVM","suggestDish fail")
                    suggestionEvent.postValue(SuggestionEvent(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Any>>, t: Throwable) {
                Log.d("wowSearchVM","suggestDish big fail")
                suggestionEvent.postValue(SuggestionEvent(false))
            }
        })
    }

    fun likeDish(id: Long) {
        api.likeDish(id).enqueue(object: Callback<ServerResponse<Any>>{
            override fun onResponse(call: Call<ServerResponse<Any>>, response: Response<ServerResponse<Any>>) {
                likeEvent.postValue(LikeEvent(response.isSuccessful))
            }

            override fun onFailure(call: Call<ServerResponse<Any>>, t: Throwable) {
                Log.d("wowSingleDishVM","likeDish big fail")
                likeEvent.postValue(LikeEvent(false))
            }

        })
    }

    fun unlikeDish(id: Long) {
        api.unlikeDish(id).enqueue(object: Callback<ServerResponse<Any>>{
            override fun onResponse(call: Call<ServerResponse<Any>>, response: Response<ServerResponse<Any>>) {
                likeEvent.postValue(LikeEvent(response.isSuccessful))
            }

            override fun onFailure(call: Call<ServerResponse<Any>>, t: Throwable) {
                Log.d("wowSingleDishVM","unlikeDish big fail")
                likeEvent.postValue(LikeEvent(false))
            }

        })
    }



}
