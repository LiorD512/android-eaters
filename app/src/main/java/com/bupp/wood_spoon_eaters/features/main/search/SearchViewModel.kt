package com.bupp.wood_spoon_eaters.features.main.search

import android.util.Log
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.managers.SearchManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel(val api: ApiService, val metaDataManager: MetaDataManager, val searchManager: SearchManager) : ViewModel() {

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
    data class DishDetailsEvent(
        val isSuccess: Boolean = false,
        val dish: Dish?
    )


    data class SuggestionEvent(
        val isSuccess: Boolean = false
    )

    val searchEvent: SingleLiveEvent<SearchEvent> = SingleLiveEvent()
    val nextSearchEvent: SingleLiveEvent<NextSearchEvent> = SingleLiveEvent()
    val dishDetailsEvent: SingleLiveEvent<DishDetailsEvent> = SingleLiveEvent()
    val suggestionEvent: SingleLiveEvent<SuggestionEvent> = SingleLiveEvent()

    var searchResult: ArrayList<Search>? = null
    var cooks: ArrayList<Cook>? = null
    var dishes: ArrayList<Dish>? = null

    fun getCuisineLabels(): ArrayList<CuisineLabel> {
        return metaDataManager.getCuisineList()
    }

    fun search(str: String) {
        val curSearchObj = searchManager.getSearchRequest()
        doSearch(curSearchObj)
    }

    private fun doSearch(curOrderObj: SearchRequest) {
        api.search(curOrderObj).enqueue(object: Callback<ServerResponse<ArrayList<Search>>> {
            override fun onResponse(call: Call<ServerResponse<ArrayList<Search>>>, response: Response<ServerResponse<ArrayList<Search>>>) {
                if(response.isSuccessful){
                    Log.d("wowSearchVM","search success")
                    val searchResult: ArrayList<Search>? = response.body()?.data
                    arrangeData(searchResult)
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

    fun getFullDishDetails(id: Long) {
        api.getDishDetails(id).enqueue(object: Callback<ServerResponse<Dish>> {
            override fun onResponse(call: Call<ServerResponse<Dish>>, response: Response<ServerResponse<Dish>>) {
                if(response.isSuccessful){
                    Log.d("wowSearchVM","getDishDetails success")
                    val dish = response.body()?.data
                    dishDetailsEvent.postValue(DishDetailsEvent(true, dish))
                }else{
                    Log.d("wowSearchVM","getDishDetails fail")
                    dishDetailsEvent.postValue(DishDetailsEvent(false, null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Dish>>, t: Throwable) {
                Log.d("wowSearchVM","getDishDetails big fail: ${t.message}")
                dishDetailsEvent.postValue(DishDetailsEvent(false, null))
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

}
