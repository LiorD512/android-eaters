package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class Feed(
    @Json(name = "title") var title: String? = null,
    @Json(name = "subtitle") var subtitle: String? = null,
    @Json(name = "search") var search: Search? = null
): Parcelable

@JsonClass(generateAdapter = true)
data class FeedRequest(
    @Json(name = "lat") var lat: Double? = null,
    @Json(name = "lng") var lng: Double? = null,
    @Json(name = "addressId") var addressId: Long? = null,
    @Json(name = "timestamp") var timestamp: String? = null
)

@JsonClass(generateAdapter = true)
data class WSRangeTimePickerHours(
    val title: String? = null,
    val date: Date,
    val hours: List<Date>
)