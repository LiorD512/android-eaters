package com.bupp.wood_spoon_eaters.managers.location

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.repositories.*

/**
 * Created by MonkeyFather on 15/05/2018.
 */

class LocationManager(val context: Context, private val appSettingsRepository: AppSettingsRepository) {

    fun clearUserAddresses() {
        Log.d(TAG, "clearUserAddresses")
        finalAddressLiveDataParam.postValue(FinalAddressParam(null))
        setDefaultAddress()
        lastChosenAddress = null
        previousChosenAddress = null
    }

    fun setDefaultAddress() {
        Log.d(TAG, "setDefaultAddress")
        val lat = appSettingsRepository.getDefaultLat()
        val lng = appSettingsRepository.getDefaultLng()
        val name = appSettingsRepository.getDefaultFeedLocationName()
        setSelectedAddressAndUpdateParams(Address(lat = lat, lng = lng, streetLine1 = name), AddressDataType.DEFAULT)
    }

    /////////////////////////////////////////
    /////////////    LOCATION    ////////////
    /////////////////////////////////////////
    private var lastChosenAddress: Address? = null
    var previousChosenAddress: Address? = null

    fun getLocationData() = locationLiveData
    private val locationLiveData = LocationLiveData(context)


    data class FinalAddressParam(
        val address: Address? = null,
        val id: Long? = null,
        val lat: Double? = null,
        val lng: Double? = null,
        val locationTitle: String? = null,
        val shortTitle: String? = null,
        val addressType: AddressDataType? = null
    )

    enum class AddressDataType {
        FULL_ADDRESS,
        DEVICE_LOCATION,
        DEFAULT
    }

    fun getFinalAddressLiveDataParam() = finalAddressLiveDataParam
    private val finalAddressLiveDataParam = MutableLiveData<FinalAddressParam>()

    fun setSelectedAddressAndUpdateParams(selectedAddress: Address?, addressType: AddressDataType? = null) {
        Log.d(TAG, "setSelectedAddressAndUpdateParams: $selectedAddress")
        if (selectedAddress != null) {
            previousChosenAddress = lastChosenAddress
            lastChosenAddress = selectedAddress.copy()
            finalAddressLiveDataParam.postValue(
                FinalAddressParam(
                    selectedAddress,
                    selectedAddress.id,
                    selectedAddress.lat,
                    selectedAddress.lng,
                    selectedAddress.getUserLocationStr(),
                    selectedAddress.getUserShortLocationStr(),
                    addressType
                )
            )
        } else {
            setDefaultAddress()
//            finalAddressLiveDataParam.postValue(FinalAddressParam())
        }
    }

    fun forceStopLocationUpdates(forceStop: Boolean) {
        locationLiveData.setForcedStop(forceStop)
    }

    fun getLastChosenAddress(): Address? {
        return lastChosenAddress
    }

    fun rollBackToPreviousAddress() {
        setSelectedAddressAndUpdateParams(previousChosenAddress, AddressDataType.FULL_ADDRESS)
    }

    companion object {
        private val TAG = "wowLocationManager"
    }


}