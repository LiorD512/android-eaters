package com.bupp.wood_spoon_eaters.features.main.search

import android.util.Log
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.managers.MetaDataRepository
import com.bupp.wood_spoon_eaters.managers.SearchManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel(val api: ApiService, val metaDataRepository: MetaDataRepository, val searchManager: SearchManager, val eaterDataManager: EaterDataManager, val eventsManager: EventsManager) : ViewModel() {

    data class LikeEvent(val isSuccess: Boolean = false)
    val likeEvent: SingleLiveEvent<LikeEvent> = SingleLiveEvent()

    data class SearchEvent(
        val isSuccess: Boolean = false,
        val cooks: ArrayList<Cook>?,
        val dishes: ArrayList<Dish>?
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

    var searchResult: ArrayList<Search>? = null


    fun getCuisineLabels(): ArrayList<CuisineLabel> {
        return metaDataRepository.getCuisineList()
    }

    fun search(str: String) {
        val curSearchObj = searchManager.getSearchRequest(str, null)
        doSearch(curSearchObj)
    }

    fun getDishesByCusineId(id: Long) {
        val cuisineId: ArrayList<Long> = arrayListOf<Long>()
        cuisineId.add(id)
        val curSearchObj = searchManager.getSearchRequest("", cuisineIds = cuisineId)
        doSearch(curSearchObj)
    }

    fun clearSearchQuery(){
        searchManager.updateCurSearch(q = null, cuisineIds = null)
    }

    private fun doSearch(curOrderObj: SearchRequest) {
        api.search(curOrderObj).enqueue(object: Callback<ServerResponse<ArrayList<Search>>> {
            override fun onResponse(call: Call<ServerResponse<ArrayList<Search>>>, response: Response<ServerResponse<ArrayList<Search>>>) {
                if(response.isSuccessful){
                    Log.d("wowSearchVM","search success")
                    val searchResult: ArrayList<Search>? = response.body()?.data
                    arrangeData(searchResult)
                    eventsManager.logUxCamEvent(Constants.UXCAM_EVENT_SEARCHED_ITEM)
                }else{
                    Log.d("wowSearchVM","search fail")
                    searchEvent.postValue(SearchEvent(false, null, null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<ArrayList<Search>>>, t: Throwable) {
                Log.d("wowSearchVM","search big fail")
                searchEvent.postValue(SearchEvent(false, null, null))
            }
        })
    }

    private fun arrangeData(searchResult: ArrayList<Search>?) {
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

    private fun getNext(searchId: Long, nextPage: String){
        api.getNextSearch(searchId, nextPage).enqueue(object: Callback<ServerResponse<ArrayList<Search>>> {
            override fun onResponse(call: Call<ServerResponse<ArrayList<Search>>>, response: Response<ServerResponse<ArrayList<Search>>>) {
                if(response.isSuccessful){
                    Log.d("wowSearchVM","getNextSearch success")
                    val searchResult: ArrayList<Search>? = response.body()?.data
                    nextSearchEvent.postValue(NextSearchEvent(true, searchResult, searchId))
                }else{
                    Log.d("wowSearchVM","getNextSearch fail")
                    nextSearchEvent.postValue(NextSearchEvent(false, null, searchId))
                }
            }

            override fun onFailure(call: Call<ServerResponse<ArrayList<Search>>>, t: Throwable) {
                Log.d("wowSearchVM","getNextSearch big fail")
                nextSearchEvent.postValue(NextSearchEvent(false, null, searchId))
            }
        })
    }

    val getCookEvent: SingleLiveEvent<CookEvent> = SingleLiveEvent()
    data class CookEvent(val isSuccess: Boolean = false, val cook: Cook?)
    fun getCurrentCook(id: Long) {
        val currentAddress = eaterDataManager.getLastChosenAddress()
        api.getCook(cookId = id, lat = currentAddress?.lat, lng = currentAddress?.lng).enqueue(object: Callback<ServerResponse<Cook>>{
            override fun onResponse(call: Call<ServerResponse<Cook>>, response: Response<ServerResponse<Cook>>) {
                if(response.isSuccessful){
                    val cook = response.body()?.data
                    Log.d("wowFeedVM","getCurrentCook success: ")
                    getCookEvent.postValue(CookEvent(true, cook))
                }else{
                    Log.d("wowFeedVM","getCurrentCook fail")
                    getCookEvent.postValue(CookEvent(false,null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Cook>>, t: Throwable) {
                Log.d("wowFeedVM","getCurrentCook big fail")
                getCookEvent.postValue(CookEvent(false,null))
            }
        })
    }

    fun suggestDish(dishName: String, dishDetails: String) {
        api.postDishSuggestion(dishName,dishDetails).enqueue(object: Callback<ServerResponse<Void>> {
            override fun onResponse(call: Call<ServerResponse<Void>>, response: Response<ServerResponse<Void>>) {
                if(response.isSuccessful){
                    Log.d("wowSearchVM","suggestDish success")
                    suggestionEvent.postValue(SuggestionEvent(true))
                }else{
                    Log.d("wowSearchVM","suggestDish fail")
                    suggestionEvent.postValue(SuggestionEvent(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
                Log.d("wowSearchVM","suggestDish big fail")
                suggestionEvent.postValue(SuggestionEvent(false))
            }
        })
    }

    fun likeDish(id: Long) {
        api.likeDish(id).enqueue(object: Callback<ServerResponse<Void>>{
            override fun onResponse(call: Call<ServerResponse<Void>>, response: Response<ServerResponse<Void>>) {
                likeEvent.postValue(LikeEvent(response.isSuccessful))
            }

            override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
                Log.d("wowSingleDishVM","likeDish big fail")
                likeEvent.postValue(LikeEvent(false))
            }

        })
    }

    fun unlikeDish(id: Long) {
        api.unlikeDish(id).enqueue(object: Callback<ServerResponse<Void>>{
            override fun onResponse(call: Call<ServerResponse<Void>>, response: Response<ServerResponse<Void>>) {
                likeEvent.postValue(LikeEvent(response.isSuccessful))
            }

            override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
                Log.d("wowSingleDishVM","unlikeDish big fail")
                likeEvent.postValue(LikeEvent(false))
            }

        })
    }



}
