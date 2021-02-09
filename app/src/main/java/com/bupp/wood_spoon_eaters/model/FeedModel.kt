package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Feed(
    @SerializedName("title") var title: String? = null,
    @SerializedName("subtitle") var subtitle: String? = null,
    @SerializedName("search") var search: Search? = Search()
): Parcelable

data class FeedRequest(
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lng") var lng: Double? = null,
    @SerializedName("addressId") var addressId: Long? = null,
    @SerializedName("timestamp") var timestamp: String? = null
)

data class WSRangeTimePickerHours(
    val date: Date,
    val hours: List<Date>
)