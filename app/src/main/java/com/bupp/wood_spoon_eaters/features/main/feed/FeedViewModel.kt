package com.bupp.wood_spoon_eaters.features.main.feed

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.managers.LocationManager
import com.bupp.wood_spoon_eaters.managers.location.GpsUtils
import com.bupp.wood_spoon_eaters.managers.location.LocationLiveData
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedViewModel(
    private val locationManager: LocationManager,
    val api: ApiService, val settings: AppSettings, val eaterDataManager: EaterDataManager, val metaDataRepository: MetaDataRepository): ViewModel(), EaterDataManager.EaterDataMangerListener {

    companion object{
        const val TAG = "wowFeedVM"
    }

    val updateMainHeaderUiEvent = MutableLiveData<Address>()
    val locationErrorEvent = MutableLiveData<LocationErrorType>()
    enum class LocationErrorType{
        SHOW_LOCATION_ERROR_BANNER,
        SHOW_LOCATION_ERROR_NO_GPS_NO_LOCATION,
        SHOW_LOCATION_ERROR_HAS_GPS_NO_LOCATION,
    }

    fun checkLocationStatus() {
        val locationResult = eaterDataManager.getLocationStatus()
        when(locationResult.type){
            EaterDataManager.LocationStatusType.CURRENT_LOCATION, EaterDataManager.LocationStatusType.KNOWN_LOCATION -> {
                getFeedWith(locationResult.address!!)
            }
            EaterDataManager.LocationStatusType.KNOWN_LOCATION_WITH_BANNER -> {
                getFeedWith(locationResult.address!!)
                locationErrorEvent.postValue(LocationErrorType.SHOW_LOCATION_ERROR_BANNER)
            }
            EaterDataManager.LocationStatusType.NO_GPS_ENABLED_AND_NO_LOCATION -> {
                locationErrorEvent.postValue(LocationErrorType.SHOW_LOCATION_ERROR_NO_GPS_NO_LOCATION)
            }
            EaterDataManager.LocationStatusType.HAS_GPS_ENABLED_BUT_NO_LOCATION -> {
                locationErrorEvent.postValue(LocationErrorType.SHOW_LOCATION_ERROR_HAS_GPS_NO_LOCATION)
            }
        }
        val location = locationResult.address
        location?.let{
            updateMainHeaderUiEvent.postValue(it)
        }
    }


    private fun getFeedWith(address: Address) {
        Log.d("wowFeedVM","address: $address")
        val feedRequest = getFeedRequest(address)
        //todo - ny - remove when flow is ready
        if(validFeedRequest(feedRequest)){
            api.getFeed(feedRequest.lat, feedRequest.lng, feedRequest.addressId, feedRequest.timestamp).enqueue(object: Callback<ServerResponse<ArrayList<Feed>>>{
                override fun onResponse(call: Call<ServerResponse<ArrayList<Feed>>>, response: Response<ServerResponse<ArrayList<Feed>>>) {
                    if(response.isSuccessful){
                        val feedArr = response.body()?.data
                        Log.d("wowFeedVM","getFeed success: ${feedArr.toString()}")
                        oldfeedEventOld.postValue(oldFeedEvent(true, feedArr))
                    }else{
                        Log.d("wowFeedVM","getFeed fail")
                        oldfeedEventOld.postValue(oldFeedEvent(false,null))
                    }
                }

                override fun onFailure(call: Call<ServerResponse<ArrayList<Feed>>>, t: Throwable) {
                    Log.d("wowFeedVM","getFeed big fail")
                    oldfeedEventOld.postValue(oldFeedEvent(false,null))
                }
            })
        }else{
            Log.d("wowFeedVM","getFeed setLocationListener")
            oldfeedEventOld.postValue(oldFeedEvent(false,null))
//            eaterDataManager.setLocationListener(this)
        }
    }





//    val feedEvent: LiveEventData<FeedEvent> = LiveEventData()
//    data class FeedEvent(val error: LocationErrorType? = null, val feed: Flow<PagingData<FeedFlow>>)
//
//    val feed: Flow<PagingData<FeedFlow>> = Pager(PagingConfig(pageSize = 10)) {
//        FeedPagingSource(api)
//    }.flow
//        .cachedIn(viewModelScope).also {
//            Log.d("wowFeedVM","flowwwww")
//        }



//    private val locationData = LocationLiveData(applicationContext)
//    fun getLocationData() = locationManager.locationData


    val oldfeedEventOld: SingleLiveEvent<oldFeedEvent> = SingleLiveEvent()
    data class oldFeedEvent(val isSuccess: Boolean = false, val feedArr: ArrayList<Feed>?)

    data class LikeEvent(val isSuccess: Boolean = false)
    val likeEvent: SingleLiveEvent<LikeEvent> = SingleLiveEvent()

    fun getEaterFirstName(): String?{
        return eaterDataManager.currentEater?.firstName
    }


    fun getFeed(){
        val feedRequest = getOldFeedRequest()
        Log.d("wowFeedVM","getFeed getFeedRequest: $feedRequest")
        if(validFeedRequest(feedRequest)){
            api.getFeed(feedRequest.lat, feedRequest.lng, feedRequest.addressId, feedRequest.timestamp).enqueue(object: Callback<ServerResponse<ArrayList<Feed>>>{
                override fun onResponse(call: Call<ServerResponse<ArrayList<Feed>>>, response: Response<ServerResponse<ArrayList<Feed>>>) {
                    if(response.isSuccessful){
                        val feedArr = response.body()?.data
                        Log.d("wowFeedVM","getFeed success: ${feedArr.toString()}")
                        oldfeedEventOld.postValue(oldFeedEvent(true, feedArr))
                    }else{
                        Log.d("wowFeedVM","getFeed fail")
                        oldfeedEventOld.postValue(oldFeedEvent(false,null))
                    }
                }

                override fun onFailure(call: Call<ServerResponse<ArrayList<Feed>>>, t: Throwable) {
                    Log.d("wowFeedVM","getFeed big fail")
                    oldfeedEventOld.postValue(oldFeedEvent(false,null))
                }
            })
        }else{
            Log.d("wowFeedVM","getFeed setLocationListener")
            oldfeedEventOld.postValue(oldFeedEvent(false,null))
//            eaterDataManager.setLocationListener(this)
        }
    }


    override fun onAddressChanged(currentAddress: Address?) {
        if(currentAddress != null){
            Log.d("wowFeedVM","getFeed onLocationChanged")
            eaterDataManager.setLastChosenAddress(currentAddress)
            getFeed()
        }
    }

    private fun validFeedRequest(feedRequest: FeedRequest): Boolean {
        return (feedRequest.lat != null && feedRequest.lng != null) || feedRequest.addressId != null
    }

    private fun getFeedRequest(currentAddress: Address): FeedRequest {
        var feedRequest = FeedRequest()
        //address
        if(eaterDataManager.isUserChooseSpecificAddress()){
            feedRequest.addressId = currentAddress.id
        }else{
            feedRequest.lat = currentAddress.lat
            feedRequest.lng = currentAddress.lng
        }

        //time
        feedRequest.timestamp = eaterDataManager.getFeedSearchTimeStringParam()

        return feedRequest
    }

    private fun getOldFeedRequest(): FeedRequest {
        var feedRequest = FeedRequest()
        //address
        val currentAddress = eaterDataManager.getLastChosenAddress()
        if(eaterDataManager.isUserChooseSpecificAddress()){
            feedRequest.addressId = currentAddress?.id
        }else{
            feedRequest.lat = currentAddress?.lat
            feedRequest.lng = currentAddress?.lng
        }

        //time
        feedRequest.timestamp = eaterDataManager.getFeedSearchTimeStringParam()

        return feedRequest
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

//    fun getDeliveryFeeString(): String {
//        return metaDataManager.getDeliveryFeeStr()
//    }

    fun getShareText(): String {
        val inviteUrl = eaterDataManager.currentEater?.shareCampaign?.inviteUrl
        val text = eaterDataManager.currentEater?.shareCampaign?.shareText
        return "$text \n $inviteUrl"
    }


}