package com.bupp.wood_spoon_eaters.model

data class Feed(
    val title: String = "",
    var search: Search? = null
){
    fun hasItems(): Boolean{
        if(search != null && search!!.results != null){
            return search!!.results!!.size > 0
        }
        return false
    }
}

data class FeedRequest(
    var lat: Double? = null,
    var lng: Double? = null,
    var addressId: Long? = null,
    var timestamp: String? = null
)