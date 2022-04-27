package com.bupp.wood_spoon_eaters.utils

import android.location.Location
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.model.Address


object LocationUtils {

    fun getClosestAddressToLocation(currentLat: Double?, currentLng: Double?, userAddresses: List<Address>): Address? {
        if (currentLat != null && currentLng != null && userAddresses.isNotEmpty()) {
            for (address in userAddresses) {
                if (isLocationsNear(currentLat, currentLng, address.lat!!, address.lng!!)) {
                    return address
                }
            }
        }
        return null
    }

    fun isLocationsNear(lat1: Double, lng1: Double, lat2: Double, lng2: Double, distanceMetersThreshold: Int? = null): Boolean {
        val loc1 = Location("")
        loc1.latitude = lat1
        loc1.longitude = lng1

        val loc2 = Location("")
        loc2.latitude = lat2
        loc2.longitude = lng2

        return loc1.distanceTo(loc2) < distanceMetersThreshold ?: Constants.MINIMUM_LOCATION_DISTANCE
    }
}