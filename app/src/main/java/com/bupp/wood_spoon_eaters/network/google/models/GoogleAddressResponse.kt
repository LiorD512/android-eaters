package com.bupp.wood_spoon_eaters.network.google.models

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json


class GoogleAddressResponse {

    @Json(name = "result")
    var results: ResultsItem? = null

    @Json(name = "status")
    var status: String? = null

    inner class AddressComponentsItem {

        @Json(name = "types")
        var types: List<String>? = null

        @Json(name = "short_name")
        var shortName: String? = null

        @Json(name = "long_name")
        var longName: String? = null
    }

    inner class Geometry {

        @Json(name = "viewport")
        var viewport: Viewport? = null

        @Json(name = "location")
        var location: Location? = null

        @Json(name = "location_type")
        var locationType: String? = null
    }


    inner class Location {

        @Json(name = "lng")
        var lng: Double = 0.toDouble()

        @Json(name = "lat")
        var lat: Double = 0.toDouble()
    }


    inner class ResultsItem {

        @Json(name = "formatted_address")
        var formattedAddress: String? = null

        @Json(name = "types")
        var types: List<String>? = null

        @Json(name = "geometry")
        var geometry: Geometry? = null

        @Json(name = "address_components")
        var addressComponents: List<AddressComponentsItem>? = null

        @Json(name = "place_id")
        var placeId: String? = null
    }

    inner class Viewport {

        @Json(name = "southwest")
        var southwest: Location? = null

        @Json(name = "northeast")
        var northeast: Location? = null
    }
}