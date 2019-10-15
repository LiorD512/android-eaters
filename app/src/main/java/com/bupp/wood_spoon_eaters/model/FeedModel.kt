package com.bupp.wood_spoon_eaters.model

import com.google.gson.annotations.SerializedName

data class Feed(
    @SerializedName("title") val title: String = "",
    @SerializedName("search") var search: Search? = null
){
    fun hasItems(): Boolean{
        if(search != null && search!!.results != null){
            return search!!.results!!.size > 0
        }
        return false
    }
}

data class FeedRequest(
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lng") var lng: Double? = null,
    @SerializedName("addressId") var addressId: Long? = null,
    @SerializedName("timestamp") var timestamp: String? = null
)