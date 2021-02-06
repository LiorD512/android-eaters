package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class LocationStatus(val type: LocationStatusType, val address: Address? = null)
enum class LocationStatusType{
    CURRENT_LOCATION,
    KNOWN_LOCATION,
    HAS_LOCATION,
    KNOWN_LOCATION_WITH_BANNER,
    NO_GPS_ENABLED_AND_NO_LOCATION,
    HAS_GPS_ENABLED_BUT_NO_LOCATION,
    NO_GPS_PERMISSION,

}

@Parcelize
data class AddressRequest(
    @SerializedName("street_number") var streetNumber: String? = null,
    @SerializedName("street_line_1") var streetLine1: String? = null,
    @SerializedName("street_line_2") var streetLine2: String? = null,
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lng") var lng: Double? = null,
    @SerializedName("country_iso") var countryIso: String? = null,
    @SerializedName("state_iso") var stateIso: String? = null,
    @SerializedName("city_name") var cityName: String? = null,
    @SerializedName("dropoff_location") var dropoffLocation: String? = null, //Available values : delivery_to_door, pickup_outside
    @SerializedName("zipcode") var zipCode: String? = null,
    @SerializedName("notes") var notes: String? = null
) : Parcelable{

    fun getUserLocationStr(): String{
        return "$streetNumber $streetLine1, ${"$cityName," ?: ""} ${stateIso ?: ""}"
    }

    fun getDropoffLocationStr(): String {
        return when (dropoffLocation) {
            "delivery_to_door" -> "Delivered To Your Door"
            else -> "Pick up outside"
        }
    }

}


@Parcelize
data class Address(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lng") var lng: Double? = null,
    @SerializedName("country") var country: Country? = null,
    @SerializedName("state") val state: State? = null,
    @SerializedName("city") val city: City? = null,
    @SerializedName("dropoff_location") var dropOfLocationStr: String? = null,
    @SerializedName("street_line_1") var streetLine1: String? = null,
    @SerializedName("street_line_2") var streetLine2: String? = null,
    @SerializedName("zipcode") val zipCode: String? = null,
    @SerializedName("notes") val notes: String? = null
) : Parcelable {

    fun getUserLocationStr(): String{
        return "$streetLine1, ${city?.name ?: ""} ${state?.name ?: ""}"
    }
    fun getDropoffLocationStr(): String {
        return when (dropOfLocationStr) {
            "delivery_to_door" -> "Delivered To Your Door"
            else -> "Pick up outside"
        }
    }
}


@Parcelize
data class Country(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("iso") val iso: String? = null,
    @SerializedName("flag_url") val flagUrl: String? = ""
) : Parcelable

@Parcelize
data class State(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("iso") val iso: String? = null
) : Parcelable

@Parcelize
data class City(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String
) : Parcelable