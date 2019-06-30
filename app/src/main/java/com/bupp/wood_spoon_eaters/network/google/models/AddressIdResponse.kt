package com.taliazhealth.predictix.network_google.models.google_api

import com.google.gson.annotations.SerializedName

class AddressIdResponse {

    @SerializedName("predictions")
    var predictions: List<PredictionsItem>? = null

    @SerializedName("status")
    var status: String? = null


    inner class PredictionsItem {

        @SerializedName("description")
        var description: String? = null

        @SerializedName("id")
        var id: String? = null

        @SerializedName("place_id")
        var placeId: String? = null

    }
}