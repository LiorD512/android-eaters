package com.example.matthias.mvvmcustomviewexample.custom

import android.util.Log
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesViewViewModel: KoinComponent{//}, EaterDataManager.EaterDataMangerListener {


    val TAG = "wowFavoritesViewVM"

    val api: ApiService by inject()
    val eaterDataManager: EaterDataManager by inject()
    val metaDataRepository: MetaDataRepository by inject()
    val deliveryTimeManager: DeliveryTimeManager by inject()
    private var listener: FavoritesViewListener? = null

    interface FavoritesViewListener{
        fun onDone(favorites: ArrayList<Dish>?)
    }

    fun setFavoritesViewListener(listener: FavoritesViewListener){
        this.listener = listener
    }

//    fun getDeliveryFeeString(): String {
//        return metaDataManager.getDeliveryFeeStr()
//    }

    fun fetchFavorites() {
        Log.d(TAG, "fetchFavorites start")
        val feedRequest = getFeedRequest()
        if(validFeedRequest(feedRequest)) {
            api.getEaterFavorites(feedRequest.lat, feedRequest.lng, feedRequest.addressId, feedRequest.timestamp)
                .enqueue(object : Callback<ServerResponse<Search>> {
                    override fun onResponse(call: Call<ServerResponse<Search>>, response: Response<ServerResponse<Search>>) {
                        if (response.isSuccessful) {
                            Log.d(TAG, "getEaterFavorites success")
                            val searchObj = response.body()?.data
                            val favOrders = searchObj?.results
                            if (favOrders != null) {
                                listener?.onDone(favorites = favOrders as ArrayList<Dish>)
                            } else {
                                listener?.onDone(null)
                            }
                        } else {
                            Log.d(TAG, "getEaterFavorites fail")
                            listener?.onDone(null)
                        }
                    }

                    override fun onFailure(call: Call<ServerResponse<Search>>, t: Throwable) {
                        Log.d(TAG, "getEaterFavorites big fail")
                        listener?.onDone(null)
                    }
                })
        }else{
            Log.d(TAG,"getFeed setLocationListener")
//            eaterDataManager.setLocationListener(this)
        }
    }

    fun getFeedRequest(): FeedRequest {
        var feedRequest = FeedRequest()
        //address
//        val currentAddress = eaterDataManager.getLastChosenAddress()
//        if (eaterDataManager.isUserChooseSpecificAddress()) {
//            feedRequest.addressId = currentAddress?.id
//        } else {
//            feedRequest.lat = currentAddress?.lat
//            feedRequest.lng = currentAddress?.lng
//        }

        //time
        feedRequest.timestamp = deliveryTimeManager.getDeliveryTimestamp()
        return feedRequest
    }

    private fun validFeedRequest(feedRequest: FeedRequest): Boolean {
        return (feedRequest.lat != null && feedRequest.lng != null) || feedRequest.addressId != null
    }

    //calling this method when tryign to get favorites, but user has no lat\lng.. waiting for addressManager to catch location and try again.
//    override fun onAddressChanged(currentAddress: Address?) { todo - nyccccccccc fix
//        if(currentAddress != null){
//            Log.d(TAG,"getFeed onLocationChanged")
//            eaterDataManager.setLastChosenAddress(currentAddress)
//            fetchFavorites()
//        }
//    }


}