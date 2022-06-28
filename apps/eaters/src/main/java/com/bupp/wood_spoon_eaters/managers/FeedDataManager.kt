package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.core.location.LocationManagerCompat
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.SingleColumnTimePickerBottomSheet
import com.bupp.wood_spoon_eaters.common.UserSettings
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.utils.LocationUtils
import com.bupp.wood_spoon_eaters.managers.location.LocationManager.AddressDataType
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.DateUtils
import java.util.*

class FeedDataManager(
    private val context: Context,
    private val eaterDataManager: EaterDataManager,
    private val userSettings: UserSettings
) {

    private var isWaitingToLocationUpdate: Boolean = false
    private var currentDeliveryParam: SingleColumnTimePickerBottomSheet.DeliveryTimeParam? = null

    fun getFinalAddressLiveDataParam() = eaterDataManager.getFinalAddressLiveDataParam()
    fun getLocationLiveData() = eaterDataManager.getLocationData()


    private fun getFeedUnixTimestamp(): String?{
        currentDeliveryParam?.let{
            return when(it.deliveryTimeType){
                SingleColumnTimePickerBottomSheet.DeliveryType.TODAY -> {
                    DateUtils.parseUnixTimestamp(Date())
                }
                SingleColumnTimePickerBottomSheet.DeliveryType.FUTURE -> {
                    DateUtils.parseUnixTimestamp(it.date)
                }
                else -> { // DeliveryType.NON_FILTERED
                    null
                }
            }
        }
        return null
    }

    fun getCurrentDeliveryDate(): Date?{
        currentDeliveryParam.let{
            return it?.date
        }
    }

    fun getFeedUiStatus() = finalFeedUiStatus
    private val finalFeedUiStatus = SingleLiveEvent<FeedUiStatus>()

    private fun isGpsEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    fun initFeedDataManager() {
        val userChosenLocation = eaterDataManager.getLastChosenAddress()
        if (userChosenLocation == null) {

            val lastSelectedAddress = eaterDataManager.getFinalAddressLiveDataParam().value
            val knownAddresses = eaterDataManager.currentEater?.addresses
            val myLocation = getLocationLiveData().value
            val hasGpsPermission = userSettings.hasGPSPermission
            val isGpsEnabled = isGpsEnabled()

            if (isGpsEnabled && hasGpsPermission) {
                if (knownAddresses.isNullOrEmpty()) {
                    if (myLocation != null) {
                        finalFeedUiStatus.postValue(FeedUiStatus(FeedUiStatusType.CURRENT_LOCATION))
                        myLocation.toAddress().let {
                            eaterDataManager.updateSelectedAddress(it, AddressDataType.DEVICE_LOCATION)
                        }
                    } else {
                        finalFeedUiStatus.postValue(FeedUiStatus(FeedUiStatusType.HAS_GPS_ENABLED_BUT_NO_LOCATION))
                        isWaitingToLocationUpdate = true
                    }
                } else {
                    if (myLocation != null) {
                        val closestAddress = LocationUtils.getClosestAddressToLocation(myLocation.lat, myLocation.lng, knownAddresses)

                        if (closestAddress != null) {
                            finalFeedUiStatus.postValue(FeedUiStatus(FeedUiStatusType.KNOWN_ADDRESS))
                            eaterDataManager.updateSelectedAddress(closestAddress, AddressDataType.FULL_ADDRESS)
                            isWaitingToLocationUpdate = false
                        } else {
                            finalFeedUiStatus.postValue(FeedUiStatus(FeedUiStatusType.CURRENT_LOCATION))
                            myLocation.toAddress()?.let {
                                eaterDataManager.updateSelectedAddress(it, AddressDataType.DEVICE_LOCATION)
                            }
                            isWaitingToLocationUpdate = true
                        }
                    } else {
                        finalFeedUiStatus.postValue(FeedUiStatus(FeedUiStatusType.KNOWN_ADDRESS_WITH_BANNER))
                        isWaitingToLocationUpdate = true
                    }
                }
            } else { // GPS permission denied
                if (knownAddresses.isNullOrEmpty()) {
                    finalFeedUiStatus.postValue(FeedUiStatus(FeedUiStatusType.NO_GPS_ENABLED_AND_NO_LOCATION))
                    eaterDataManager.setDefaultFeedUi()
                } else {
                    finalFeedUiStatus.postValue(FeedUiStatus(FeedUiStatusType.KNOWN_ADDRESS_WITH_BANNER))
                    eaterDataManager.updateSelectedAddress(knownAddresses[0], AddressDataType.FULL_ADDRESS)
                }
            }
        } else {
            Log.d(
                TAG,
                "skip feed status update"
            )
        }
    }

    fun refreshFeedByLocationIfNeeded() {
        initFeedDataManager()
        eaterDataManager.stopLocationUpdates()
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
        feedRequest.timestamp = getFeedUnixTimestamp()

        return feedRequest
    }

    fun getLastFeedRequest(): FeedRequest {
        //being used in NewOrderActivity, uses params to init new Order.
        var feedRequest = FeedRequest()
        val lastAddress = getFinalAddressLiveDataParam().value
        lastAddress?.let {
            //address
            if (lastAddress.id != null) {
                feedRequest.addressId = lastAddress.id
            } else {
                feedRequest.lat = lastAddress.lat
                feedRequest.lng = lastAddress.lng
            }
        }

        //time
        feedRequest.timestamp = getFeedUnixTimestamp()

        return feedRequest
    }

    fun getFeedDeliveryParams(): Date?{
        currentDeliveryParam?.let{
            return it.date
        }
        return null
    }

    fun onTimePickerChanged(deliveryTimeParam: SingleColumnTimePickerBottomSheet.DeliveryTimeParam?) {
        this.currentDeliveryParam = deliveryTimeParam
    }

    companion object {
        private const val TAG = "wowFeedDataManager"
    }
}