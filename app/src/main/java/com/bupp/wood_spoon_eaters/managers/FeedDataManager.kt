package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.LocationUtils


class FeedDataManager(private val context: Context,
    private val eaterDataManager: EaterDataManager, private val appSettings: AppSettings) {

    private var isWaitingToLocationUpdate: Boolean = false

    fun getFinalAddressLiveDataParam() = eaterDataManager.getFinalAddressLiveDataParam()
    fun getLocationLiveData() = eaterDataManager.getLocationData()

    fun getFeedUiStatus() = finalFeedUiStatus
    private val finalFeedUiStatus = MutableLiveData<FeedUiStatus>()

    private fun isGpsEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }


    fun initFeedDataManager(){
        val lastSelectedAddress = eaterDataManager.getFinalAddressLiveDataParam().value
        val knownAddresses = eaterDataManager.currentEater?.addresses
        val myLocation = getLocationLiveData().value
        val hasGpsPermission = appSettings.hasGPSPermission
        val isGpsEnabled = isGpsEnabled()
        Log.d(
            TAG,
            "getLocationStatus: lastSelectedAddress: $lastSelectedAddress, hasGpsPermission: $hasGpsPermission," +
                    " knownAddresses size: ${knownAddresses?.size}, myLocation: $myLocation"
        )
        if (isGpsEnabled && hasGpsPermission) {
            Log.d(TAG, "has gpsPermission")
            if (knownAddresses.isNullOrEmpty()) {
                Log.d(TAG, "don't have known address")
                if (myLocation != null) {
                    finalFeedUiStatus.postValue(FeedUiStatus(FeedUiStatusType.CURRENT_LOCATION))
                    myLocation.toAddress()?.let {
                        eaterDataManager.updateSelectedAddress(it)
                    }
                } else {
                    finalFeedUiStatus.postValue(FeedUiStatus(FeedUiStatusType.HAS_GPS_ENABLED_BUT_NO_LOCATION))
//                    eaterDataManager.setDefaultFeedUi()
                    //start location updates
                    Log.d(TAG, "starts location update")
                    isWaitingToLocationUpdate = true
                }
            } else {
                Log.d(TAG, "has known address")
                if (myLocation != null) {
                    val closestAddress = LocationUtils.getClosestAddressToLocation(myLocation.lat, myLocation.lng, knownAddresses)
                    if (closestAddress != null) {
                        Log.d(TAG, "using closest address: $closestAddress")
                        finalFeedUiStatus.postValue(FeedUiStatus(FeedUiStatusType.KNOWN_ADDRESS))
                        eaterDataManager.updateSelectedAddress(closestAddress)
                        isWaitingToLocationUpdate = false
                    } else {
                        Log.d(TAG, "using current location: $myLocation")
                        finalFeedUiStatus.postValue(FeedUiStatus(FeedUiStatusType.CURRENT_LOCATION))
                        myLocation.toAddress()?.let {
                            eaterDataManager.updateSelectedAddress(it)
                        }
                        isWaitingToLocationUpdate = true
                    }
                } else {
                    finalFeedUiStatus.postValue(FeedUiStatus(FeedUiStatusType.KNOWN_ADDRESS_WITH_BANNER))
                    eaterDataManager.updateSelectedAddress(knownAddresses[0])
                    //start location updates
                    Log.d(TAG, "starts location update")
                    isWaitingToLocationUpdate = true
                }
            }
        } else { // GPS permission denied
            Log.d(TAG, "don't have gpsPermission or Gps disabled")
            if (knownAddresses.isNullOrEmpty()) {
                finalFeedUiStatus.postValue(FeedUiStatus(FeedUiStatusType.NO_GPS_ENABLED_AND_NO_LOCATION))
                eaterDataManager.setDefaultFeedUi()
            } else {
                finalFeedUiStatus.postValue(FeedUiStatus(FeedUiStatusType.KNOWN_ADDRESS_WITH_BANNER))
                eaterDataManager.updateSelectedAddress(knownAddresses[0])
            }
        }
    }

    fun refreshFeedByLocationIfNeeded() {
        if(isWaitingToLocationUpdate){
            initFeedDataManager()
            eaterDataManager.stopLocationUpdates()
        }
    }


    fun getFeedRequestWithAddress(currentAddress: Address): FeedRequest {
        var feedRequest = FeedRequest()
        //address
        if (currentAddress.id != null) {
            feedRequest.addressId = currentAddress.id
        } else {
            feedRequest.lat = currentAddress.lat
            feedRequest.lng = currentAddress.lng
        }

        //time
        feedRequest.timestamp = eaterDataManager.getDeliveryTimestamp()

        return feedRequest
    }

    fun getLastFeedRequest(): FeedRequest {
        var feedRequest = FeedRequest()
        val lastAddress = getFinalAddressLiveDataParam().value
        lastAddress?.let{
            //address
            if (lastAddress.id != null) {
                feedRequest.addressId = lastAddress.id
            } else {
                feedRequest.lat = lastAddress.lat
                feedRequest.lng = lastAddress.lng
            }
        }

        //time
        feedRequest.timestamp = eaterDataManager.getDeliveryTimestamp()

        return feedRequest
    }

    fun getUser(): Eater?{
        return eaterDataManager.currentEater
    }


    companion object {
        private const val TAG = "wowFeedDataManager"

    }
}