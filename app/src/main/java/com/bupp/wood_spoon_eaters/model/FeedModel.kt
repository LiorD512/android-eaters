package com.bupp.wood_spoon_eaters.model

data class Feed(
    val title: String = "",
    var search: Search? = null
)

data class FeedRequest(
    var lat: Double? = null,
    var lng: Double? = null,
    var addressId: Long? = null,
    var timestamp: Int? = null
)