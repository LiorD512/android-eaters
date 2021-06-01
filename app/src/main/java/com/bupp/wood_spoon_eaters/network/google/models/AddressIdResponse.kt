package com.bupp.wood_spoon_eaters.network.google.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

class AddressIdResponse {

    var predictions: List<PredictionsItem>? = null

    var status: String? = null


    inner class PredictionsItem {

        var description: String? = null

        var id: String? = null

        @Json(name = "place_id")
        var placeId: String? = null

    }
}