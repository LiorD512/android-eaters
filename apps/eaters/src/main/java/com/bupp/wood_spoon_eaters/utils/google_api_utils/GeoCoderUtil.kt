package com.bupp.wood_spoon_eaters.utils.google_api_utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.google.android.gms.common.util.CollectionUtils
import java.util.*


object GeoCoderUtil {

    fun execute(
        context: Context,
        address: AddressRequest,
        callback: LoadDataCallback<AddressRequest>
    ) {
        var parsedState = "" // not returned in the GeoCoder result
        var parsedZipCode: String? = null
        var parsedCityName: String? = null
        var parsedStreetName: String? = null
        var parsedCountryIso: String? = null
        var parsedStreetNumber: String? = null
        val locationModel: AddressRequest
        try {
            val addresses: MutableList<Address>
            val geocoder = Geocoder(context, Locale.ENGLISH)
            addresses =
                geocoder.getFromLocation(address.lat!!, address.lng!!, 1)
            if (!CollectionUtils.isEmpty(addresses)) {
                val fetchedAddress = addresses[0]
                if (fetchedAddress.maxAddressLineIndex > -1) {
//                    val address = fetchedAddress.getAddressLine(0)
                    fetchedAddress.locality?.let {
                        parsedCityName = it
                    }
                    fetchedAddress.featureName?.let {
                        parsedStreetNumber = it
                    }
                    fetchedAddress.thoroughfare?.let {
                        parsedStreetName = it
                    }
                    fetchedAddress.postalCode?.let {
                        parsedZipCode = it
                    }
                    fetchedAddress.countryCode?.let {
                        parsedCountryIso = it
                    }
                }
                locationModel = AddressRequest().apply {
                    streetNumber = parsedStreetNumber ?: address.streetNumber
                    streetLine1 = parsedStreetName ?: address.streetLine1
                    countryIso = parsedCountryIso ?: address.countryIso
                    cityName = parsedCityName ?: address.cityName
                    zipCode = parsedZipCode ?: address.zipCode
                    stateIso = address.stateIso
                    lat = address.lat
                    lng = address.lng
                }
                callback.onDataLoaded(locationModel)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback.onDataNotAvailable(1, "Something went wrong!")
        }
    }


    fun parseDummyAddress(addressRequest: AddressRequest): AddressRequest {
        if(addressRequest.cityName == null){
            addressRequest.cityName = ""
        }
        if(addressRequest.streetLine1 == null){
            addressRequest.streetLine1 = addressRequest.cityName
        }
        if(addressRequest.streetNumber == null){
            addressRequest.streetNumber = ""
        }
        return addressRequest
    }
}


interface LoadDataCallback<T> {
    fun onDataLoaded(response: T) {
    }

    fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {

    }
}