package com.bupp.wood_spoon_eaters.features.main.filter

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.managers.SearchManager
import com.bupp.wood_spoon_eaters.model.SearchRequest
import com.bupp.wood_spoon_eaters.model.SelectableIcon

class PickFiltersViewModel(val metaDataManager: MetaDataManager, val searchManager: SearchManager) : ViewModel(){

    data class RestoreDetailsEvent(val hasParmas: Boolean,
                                   val currentDiets: ArrayList<SelectableIcon>,
                                   val minPrice: Double?,
                                   val maxPrice: Double?)
    val restoreDetailsEvent: SingleLiveEvent<RestoreDetailsEvent> = SingleLiveEvent()

    fun getArrivalTimes(): ArrayList<SelectableIcon> {
        //todo - fix this shit
        return arrayListOf()
    }

    fun getDietaryList(): ArrayList<SelectableIcon>{
        return metaDataManager.getDietaryList()
    }

    fun updateSearchParams(price: Pair<Double?, Double?>?, dietsIds: ArrayList<Long>?) {
        if(price != null){
            searchManager.updateCurSearch(minPrice = price.first, maxPrice = price.second)
        }else{
            searchManager.updateCurSearch(minPrice = null, maxPrice = null)
        }
        if(dietsIds != null){
            searchManager.curSearch.dietIds?.clear()
            searchManager.updateCurSearch(dietIds = dietsIds)
        }else{
            searchManager.curSearch.dietIds = null
        }
    }

    fun getCurrentFilterParam() {
        val currentSearchParam = searchManager.curSearch
        var hasParams = false;
        val currentDiets = arrayListOf<SelectableIcon>()
        if(currentSearchParam.dietIds != null && currentSearchParam.dietIds!!.size > 0){
            for(item in getDietaryList()){
                for(item2 in currentSearchParam.dietIds!!){
                    if(item.id == item2){
                        hasParams = true
                        currentDiets.add(item)
                    }
                }
            }
        }
        if(currentSearchParam.maxPrice != null || currentSearchParam.minPrice != null){
            hasParams = true
        }
        restoreDetailsEvent.postValue(RestoreDetailsEvent(hasParams, currentDiets, currentSearchParam.minPrice, currentSearchParam.maxPrice ))
    }

    fun clearSearchParams(){
        searchManager.clearSearch()
    }

}