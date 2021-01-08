package com.bupp.wood_spoon_eaters.managers

import com.bupp.wood_spoon_eaters.model.SearchRequest
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.common.AppSettings

class SearchManager(val api: ApiService, val eaterDataManager: EaterDataManager, val settings: AppSettings) {

    var curSearch: SearchRequest = SearchRequest()

    fun updateCurSearch(lat: Double? = null,
                        lng: Double? = null,
                        addressId: Long? = null,
                        timestamp: String? = null,
                        q: String? = "",
                        cuisineIds: ArrayList<Long>? = null,
                        dietIds: ArrayList<Long>? = null,
                        minPrice: Double? = null,
                        maxPrice: Double? = null,
                        isAsap: Boolean? = null): SearchRequest{
        if(q != null){ curSearch.q = q }
        if(lat != null){ curSearch.lat = lat }
        if(lng != null){ curSearch.lng = lat }
        if(dietIds != null){ curSearch.dietIds = dietIds }
        if(minPrice != null){ curSearch.minPrice = minPrice }
        if(maxPrice != null){ curSearch.maxPrice = maxPrice }
        if(addressId != null){ curSearch.addressId = addressId }
        if(timestamp != null){ curSearch.timestamp = timestamp }
        if(cuisineIds != null){ curSearch.cuisineIds = cuisineIds }
        if(isAsap != null){ curSearch.isAsap = isAsap }
        return curSearch
    }

    fun getSearchRequest(str: String, cuisineIds: ArrayList<Long>?): SearchRequest {
        val currentAddress = eaterDataManager.getLastChosenAddress()
        if(eaterDataManager.isUserChooseSpecificAddress()){
            curSearch.addressId = currentAddress?.id
            curSearch.lat = null
            curSearch.lng = null
        }else{
            curSearch.addressId = null
            curSearch.lat = currentAddress?.lat
            curSearch.lng = currentAddress?.lng
        }
        if(!str.isNullOrEmpty()){
            curSearch.q = str
        }else{
            curSearch.q = ""
        }

        if(cuisineIds != null && cuisineIds.size!! > 0){
            curSearch.cuisineIds = arrayListOf()
            curSearch.cuisineIds?.addAll(cuisineIds)
        }else{
            curSearch.cuisineIds?.clear()
        }

        //time
        curSearch.timestamp = eaterDataManager.getLastOrderTimeParam()

        return curSearch
    }

    fun clearSearch(){
        curSearch = SearchRequest()
    }




}