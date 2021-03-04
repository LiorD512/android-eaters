package com.bupp.wood_spoon_eaters.managers

import com.bupp.wood_spoon_eaters.model.SearchRequest
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.model.CuisineLabel
import com.bupp.wood_spoon_eaters.repositories.FeedRepository

class SearchManager(private val feedRepository: FeedRepository, val eaterDataManager: EaterDataManager, val settings: AppSettings, private val deliveryTimeManager: DeliveryTimeManager) {

    var curSearch: SearchRequest = SearchRequest()


    suspend fun doSearch(str: String, cuisine: CuisineLabel? = null): FeedRepository.SearchResult {
        eaterDataManager.logUxCamEvent(Constants.EVENT_SEARCHED_ITEM, mapOf(Pair("query", str), Pair("cuisine", cuisine?.name ?: "null")))
        val searchRequest = refreshSearchRequest(str, cuisine)
        return feedRepository.getSearch(searchRequest)
    }

    suspend fun getCookById(cookId: Long): FeedRepository.CookResult {
        val feedRequest = eaterDataManager.getLastFeedRequest()
        val lat = feedRequest.lat
        val lng = feedRequest.lng
        val addressId = feedRequest.addressId
        return feedRepository.getCookById(cookId, addressId, lat, lng)
    }

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


    private fun refreshSearchRequest(str: String, cuisine: CuisineLabel?): SearchRequest {
        val currentAddress = eaterDataManager.getLastChosenAddress()
        if(currentAddress?.id != null){
            curSearch.addressId = currentAddress.id
            curSearch.lat = null
            curSearch.lng = null
        }else{
            curSearch.addressId = null
            curSearch.lat = currentAddress?.lat
            curSearch.lng = currentAddress?.lng
        }

        if(str.isNotEmpty()){
            curSearch.q = str
        }else{
            curSearch.q = ""
        }

        if(cuisine != null){
            curSearch.cuisineIds = arrayListOf()
            curSearch.cuisineIds?.add(cuisine.id)
        }else{
            curSearch.cuisineIds?.clear()
        }

        //time
        curSearch.timestamp = deliveryTimeManager.getDeliveryTimestamp()

        return curSearch
    }

    fun clearSearch(){
        curSearch = SearchRequest()
    }



}