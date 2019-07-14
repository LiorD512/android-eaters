package com.bupp.wood_spoon_eaters.network.google.interfaces


import com.taliazhealth.predictix.network_google.models.google_api.AddressIdResponse
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
        //todo !!!! importent ! replace key !
        const val GEOCODING_URL_TEMPLATE = "autocomplete/json?language=en&types=geocode&key=AIzaSyCMo8XwzKZl1vuSBYe0xHvjlEnQ56O9lho"
        const val GEOCODING_URL_PLACE_ID_TEMPLATE = "details/json?language=en&key=AIzaSyCMo8XwzKZl1vuSBYe0xHvjlEnQ56O9lho"
    }
}

