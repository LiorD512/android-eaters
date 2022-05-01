package com.bupp.wood_spoon_chef.utils

import android.util.Log
import com.bupp.wood_spoon_chef.data.remote.model.AddressRequest
import com.google.android.libraries.places.api.model.Place
import java.util.*


object GoogleAddressParserUtil {

    const val TAG = "wowGgleAddressParseUtil"

    private val allowed_types = setOf(
            "route", //street
            "country", //country
            "locality", //city
            "postal_code",
            "street_number",
            "sublocality_level_1", //district
            "administrative_area_level_1" //state
    )

    fun parsePlaceToAddressRequest(place: Place): AddressRequest {

        val addressRequest = AddressRequest()
        place.latLng?.let {
            addressRequest.lat = it.latitude
            addressRequest.lng = it.longitude
        }

        val addressComponents = place.addressComponents?.asList()
        addressComponents?.forEach { address->
            val data = address.types.intersect(this.allowed_types).toString().replace("[", "").replace("]", "")
            val result = data.split(",").map { it.trim() }
            result.forEach { item ->
                Log.d(TAG, "parseLocationToAddress: $item")
                if (item.isNotEmpty()) {
                    Log.d(TAG, "parseLocationToAddress: ${address.name}")
                    when (item) {
                        "route" -> {
                            addressRequest.streetLine1 = address.name
                        }
                        "country" -> {
                            addressRequest.countryIso = address.shortName
                        }
                        "sublocality_level_1", "sublocality", "locality" -> {
                            addressRequest.cityName = address.name
                        }
                        "postal_code" -> {
                            addressRequest.zipCode = address.name
                        }
                        "street_number" -> {
                            addressRequest.streetNumber = address.name
                        }
                        "administrative_area_level_1" -> {
                            addressRequest.stateIso = address.shortName
                        }
                    }
                }
            }

        }
        return addressRequest
    }
    
}
