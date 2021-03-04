package com.bupp.wood_spoon_eaters.managers.location


import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.location.Location
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.dialogs.OrderUpdateErrorDialog
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.City
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlin.math.ln


/**
 * Created by MonkeyFather on 15/05/2018.
 */

class LocationManager(val context: Context, private val metaDataRepository: MetaDataRepository) {


    fun setDefaultAddress() {
        val lat = metaDataRepository.getDefaultLat()
        val lng = metaDataRepository.getDefaultLng()
        val name = metaDataRepository.getDefaultFeedLocationName()
        setSelectedAddressAndUpdateParams(Address(lat = lat, lng = lng, streetLine1 = name))
    }


    /////////////////////////////////////////
    /////////////    LOCATION    ////////////
    /////////////////////////////////////////
    private var lastChosenAddress: Address? = null
    var previousChosenAddress: Address? = null

    fun getLocationData() = locationLiveData
    private val locationLiveData = LocationLiveData(context)


    data class FinalAddressParam(
        val id: Long? = null,
        val lat: Double? = null,
        val lng: Double? = null,
        val locationTitle: String? = null,
        val shortTitle: String? = null
    )

    fun getFinalAddressLiveDataParam() = finalAddressLiveDataParam
    private val finalAddressLiveDataParam = MutableLiveData<FinalAddressParam>()

    fun setSelectedAddressAndUpdateParams(selectedAddress: Address?) {
        Log.d(TAG, "setSelectedAddressAndUpdateParams: $selectedAddress")
        if(selectedAddress != null) {
            previousChosenAddress = lastChosenAddress
            lastChosenAddress = selectedAddress.copy()
            finalAddressLiveDataParam.postValue(
                FinalAddressParam(
                    selectedAddress.id,
                    selectedAddress.lat,
                    selectedAddress.lng,
                    selectedAddress.getUserLocationStr(),
                    selectedAddress.getUserShortLocationStr()
                )
            )
        }else{
            finalAddressLiveDataParam.postValue(FinalAddressParam())
        }
    }

    fun forceStopLocationUpdates(forceStop: Boolean) {
        locationLiveData.setForcedStop(forceStop)
    }

    fun getLastChosenAddress(): Address? {
        return lastChosenAddress
    }

    fun rollBackToPreviousAddress() {
        setSelectedAddressAndUpdateParams(previousChosenAddress)
    }

    companion object {

        private val TAG = "wowLocationManager"

    }


}