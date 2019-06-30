package com.taliazhealth.predictix.network_google.models.google_api

import com.google.gson.annotations.SerializedName

class AddressResponse {

    @SerializedName("result")
    var results: ResultsItem? = null

    @SerializedName("status")
    var status: String? = null

    inner class AddressComponentsItem {

        @SerializedName("types")
        var types: List<String>? = null

        @SerializedName("short_name")
        var shortName: String? = null

        @SerializedName("long_name")
        var longName: String? = null
    }

    inner class Geometry {

        @SerializedName("viewport")
        var viewport: Viewport? = null

        @SerializedName("location")
        var location: Location? = null

        @SerializedName("location_type")
        var locationType: String? = null
    }


    inner class Location {

        @SerializedName("lng")
        var lng: Double = 0.toDouble()

        @SerializedName("lat")
        var lat: Double = 0.toDouble()
    }


    inner class ResultsItem {

        @SerializedName("formatted_address")
        var formattedAddress: String? = null

        @SerializedName("types")
        var types: List<String>? = null

        @SerializedName("geometry")
        var geometry: Geometry? = null

        @SerializedName("address_components")
        var addressComponents: List<AddressComponentsItem>? = null

        @SerializedName("place_id")
        var placeId: String? = null
    }

    inner class Viewport {

        @SerializedName("southwest")
        var southwest: Location? = null

        @SerializedName("northeast")
        var northeast: Location? = null
    }
}