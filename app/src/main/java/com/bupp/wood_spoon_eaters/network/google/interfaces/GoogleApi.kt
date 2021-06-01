package com.bupp.wood_spoon_eaters.network.google.interfaces


import com.bupp.wood_spoon_eaters.network.google.models.AddressIdResponse
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleApi {

    @GET(GEOCODING_URL_TEMPLATE)
    fun getAddressId(@Query("input") input: String): Call<AddressIdResponse>

    @GET(GEOCODING_URL_PLACE_ID_TEMPLATE)
    fun getAddress(@Query("placeid") placeId: String): Call<GoogleAddressResponse>

    companion object {
        val BASE_URL = "https://maps.googleapis.com/maps/api/place/"
        const val GEOCODING_URL_TEMPLATE = "autocomplete/json?language=en&types=geocode&key=AIzaSyCw2_dLJxQTCcM0Wujy2J-RX1uGBm-S0Bc"
        const val GEOCODING_URL_PLACE_ID_TEMPLATE = "details/json?language=en&key=AIzaSyCw2_dLJxQTCcM0Wujy2J-RX1uGBm-S0Bc"
        const val GEOCODING_LAT_LNG_TO_PLACE_ID = "nearbysearch/json?&key=YOUR_API_KEY"
    }
}

