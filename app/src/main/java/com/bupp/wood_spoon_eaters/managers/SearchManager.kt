package com.bupp.wood_spoon_eaters.managers

import android.util.Log
import com.bupp.wood_spoon_eaters.model.Search
import com.bupp.wood_spoon_eaters.model.SearchRequest
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchManager(val api: ApiService) {

    var curSearch: SearchRequest = SearchRequest()

    fun updateCurSearch(lat: Double? = null,
                        lng: Double? = null,
                        addressId: Int? = null,
                        timestamp: Int? = null,
                        q: String? = "",
                        cuisineIds: ArrayList<Int>? = null,
                        dietIds: ArrayList<Int>? = null,
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



}