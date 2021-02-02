package com.bupp.wood_spoon_eaters.utils

import android.util.Log
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place


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



    fun parseLocationToAddress(place: Place): AddressRequest {

        var addressRequest: AddressRequest = AddressRequest()
        place.latLng?.let{
            addressRequest.lat = it.latitude
            addressRequest.lng = it.longitude
        }

        val addressComponents = place.addressComponents?.asList()
        addressComponents?.forEach {

            val data = it.types.intersect(this.allowed_types)
            Log.d(TAG, "parseLocationToAddress: $data")
            if(data.isNotEmpty()){
                Log.d(TAG, "parseLocationToAddress: ${it.name}")
                when(data.toString().replace("[", "").replace("]", "")){
                    "route" -> {
                        addressRequest.streetLine1 = it.name
                    }
                    "country" -> {
                        addressRequest.countryIso = it.shortName
                    }
                    "sublocality_level_1", "sublocality" -> {
                        addressRequest.cityName = it.name
                    }
                    "postal_code" -> {
                        addressRequest.zipCode = it.name
                    }
                    "street_number" -> {
                        addressRequest.streetNumber = it.name
                    }
                    "administrative_area_level_1" -> {
                        addressRequest.stateIso = it.shortName
                    }
                }
            }
        }
        return addressRequest
//            var streetLine1 = getStreet(addressComponents)
//            var stateName = getStateName(addressComponents)
//            var cityName = getCityName(addressComponents)
//            Log.d(TAG, "stateName: $stateName")
//            Log.d(TAG, "streetLine1: $streetLine1")
//            Log.d(TAG, "cityName: $cityName")
//        parse(place)
        }

    private fun parseMyLocation(myLocationAddress: Address): AddressRequest {
        var addressRequest = AddressRequest()

        addressRequest.lat = myLocationAddress.lat
        addressRequest.lng = myLocationAddress.lng
        addressRequest.streetLine1 = myLocationAddress.streetLine1
        addressRequest.streetLine2 = myLocationAddress.streetLine2

        return addressRequest
    }

//    fun parseLocationToAddressRequest(place: Place, streetLine1: String, streetLine2: String, notes: String): AddressRequest? {
//        val addressComponents = place.addressComponents?.asList()
//        val latLng = place.latLng
//
//        addressComponents?.let {
//            it.forEach {
//                Log.d(TAG, "updateCookAccount addressComponents for each: ${it.shortName}")
//            }
//        }
//
//        if (addressComponents.isNullOrEmpty()) {
//            Log.d(TAG, "updateCookAccount address failed")
//            return null
//        } else {
////            Log.d(TAG, "updateCookAccount start")
//
////            Log.d(TAG, "updateCookAccount address 2")
////            var countryNames = getCountry(addressComponents)
////            var countryName = countryNames.first
////            var countryIso = countryNames.second
////
////
////            Log.d(TAG, "updateCookAccount address 3")
////            var stateNames = getStateName(addressComponents, countryName, countryIso)
////            var stateIso = stateNames.second
////
////            Log.d(TAG, "updateCookAccount address 4")
////            var streetLine1 = getStreet(addressComponents, streetLine1)
////            var cityName = getCityName(addressComponents)
////            var zipCode = getZipCode(addressComponents)
////
////            var lat: Double? = 0.0
////            var lng: Double? = 0.0
////
////            if (address.Location().lat != 0.0 && address.Location().lng != 0.0) {
////                Log.d(TAG, "updateCookAccount address.Location(): ")
////                Log.d(TAG, "updateCookAccount address.Location(): ${address.Location()}")
////                lat = address.Location().lat
////                lng = address.Location().lng
////            } else {
////                Log.d(TAG, "updateCookAccount address.results?.geometry:")
////                address?.let{
////                    Log.d(TAG, "updateCookAccount address ok")
////                    it.results?.let{
////                        Log.d(TAG, "updateCookAccount results ok")
////                        it.geometry?.let{
////                            Log.d(TAG, "updateCookAccount geometry ok ${it}")
////                            it.location?.let {
////                                Log.d(TAG, "updateCookAccount location ok")
////                                lat = it.lat
////                                lng = it.lng
////                            }
////                        }
////                    }
////                }
////            }
////
//            var newAddress: AddressRequest = AddressRequest()
////            newAddress.streetLine1 = streetLine1
////            newAddress.streetLine2 = streetLine2
////            newAddress.countryIso = countryIso
////            newAddress.stateIso = stateIso
////            newAddress.cityName = cityName
////            newAddress.zipCode = zipCode
////            newAddress.notes = notes
////            newAddress.lat = lat
////            newAddress.lng = lng
//            return newAddress
//        }
//    }
//
//
//    private fun getCityName(addrComponents: List<AddressComponent>): String? {
//        try {
//            for (i in addrComponents.indices) {
//                if (!addrComponents[i].types.isNullOrEmpty()) {
//                    if (addrComponents[i].types[0] == "locality") {
//                        addrComponents[i].shortName.let {
//                            Log.d(TAG, "updateCookAccount getCityName done $it")
//                            return it
//                        }
//                    }
//                }
//            }
//        } catch (ex: Exception) {
//            Log.d(TAG, "ex: $ex")
//        }
//        return null
//    }
//
//    private fun getStateName(addrComponents: List<AddressComponent>): Pair<String, String>? {
//        try {
//            for (i in addrComponents.indices) {
//                if (!addrComponents[i].types.isNullOrEmpty()) {
//                    if (addrComponents[i].types[0] == "administrative_area_level_1") {
//                        addrComponents[i].name.let { name ->
//                            addrComponents[i].shortName?.let { shortName ->
//                                Log.d(TAG, "updateCookAccount getStateName done $shortName")
//                                return Pair(name, shortName)
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (ex: Exception) {
//            Log.d(TAG, "ex: $ex")
//        }
//        return null
//    }
//
//    private fun getCountry(addrComponents: List<AddressComponent>): Pair<String, String>? {
//        try {
//            for (i in addrComponents.indices) {
//                if (!addrComponents[i].types.isNullOrEmpty()) {
//                    if (addrComponents[i].types[0] == "country") {
//                        addrComponents[i].name.let { name ->
//                            addrComponents[i].shortName?.let { shortName ->
//                                return Pair(name, shortName)
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (ex: Exception) {
//            Log.d(TAG, "ex: $ex")
//        }
//        return null
//    }
//
//    private fun getStreet(addrComponents: List<AddressComponent>): String? {
//        try {
//            for (i in addrComponents.indices) {
//                if (!addrComponents[i].types.isNullOrEmpty()) {
//                    if (addrComponents[i].types[0] == "route") {
//                        addrComponents[i].name.let {
//                            Log.d(TAG, "updateCookAccount getCityName getStreet $it")
//                            return it
//                        }
//                    }
//                }
//            }
//        } catch (ex: Exception) {
//            Log.d(TAG, "ex: $ex")
//        }
//        return null
//    }
//
//    private fun getZipCode(addrComponents: List<AddressComponent>): String? {
//        try {
//            for (i in addrComponents.indices) {
//                if (!addrComponents[i].types.isNullOrEmpty()) {
//                    if (addrComponents[i].types[0] == "postal_code") {
//                        addrComponents[i].name.let {
//                            Log.d(TAG, "updateCookAccount getCityName getZipCode $it")
//                            return it
//                        }
//                    }
//                }
//            }
//        } catch (ex: Exception) {
//            Log.d(TAG, "ex: $ex")
//        }
//        return null
//    }
}
