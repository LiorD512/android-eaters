package com.bupp.wood_spoon_eaters.managers

import com.bupp.wood_spoon_eaters.model.SearchRequest
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings

class SearchManager(val api: ApiService, val eaterDataManager: EaterDataManager, val settings: AppSettings) {

    var curSearch: SearchRequest = SearchRequest()

    fun updateCurSearch(lat: Double? = null,
                        lng: Double? = null,
                        addressId: Long? = null,
                        timestamp: Int? = null,
                        q: String? = "",
                        cuisineIds: ArrayList<Int>? = null,
                        dietIds: ArrayList<Long>? = null,
                        minPrice: Double? = null,
                        maxPrice: Double? = null): SearchRequest{
        if(q != null){ curSearch.q = q }
        if(lat != null){ curSearch.lat = lat }
        if(lng != null){ curSearch.lng = lat }
        if(dietIds != null){ curSearch.dietIds = dietIds }
        if(minPrice != null){ curSearch.minPrice = minPrice }
        if(maxPrice != null){ curSearch.maxPrice = maxPrice }
        if(addressId != null){ curSearch.addressId = addressId }
        if(timestamp != null){ curSearch.timestamp = timestamp }
        if(cuisineIds != null){ curSearch.cuisineIds = cuisineIds }
        return curSearch
    }

    fun getSearchRequest(): SearchRequest {
        val currentAddress = eaterDataManager.getLastChosenAddress()
        if(eaterDataManager.isUserChooseSpecificAddress()){
            curSearch.addressId = currentAddress?.id
        }else{
            curSearch.lat = currentAddress?.lat
            curSearch.lng = currentAddress?.lng
        }
        return curSearch
    }

    fun clearSearch(){
        curSearch = SearchRequest()
    }




}