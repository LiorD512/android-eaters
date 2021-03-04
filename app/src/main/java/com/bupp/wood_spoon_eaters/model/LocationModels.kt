package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class FeedUiStatus(val type: FeedUiStatusType)
enum class FeedUiStatusType{
    CURRENT_LOCATION,
    KNOWN_ADDRESS,
    HAS_LOCATION,
    KNOWN_ADDRESS_WITH_BANNER,
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
    @SerializedName("notes") var notes: String? = null,
    var addressSlug: String? = null
) : Parcelable{

    fun getUserLocationStr(): String{
        val cityNameStr = cityName?.let{ "$it,"} ?: ""
        return "$streetNumber $streetLine1, $cityNameStr ${stateIso ?: ""}"
    }

    fun toAddress(): Address {
        val fullStreetLine1 = "$streetNumber $streetLine1"
        return Address(id = null, lat = lat, lng = lng, streetLine1 = fullStreetLine1, streetLine2 = streetLine2, addressSlug = addressSlug)
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
    @SerializedName("notes") val notes: String? = null,
    val addressSlug: String? = null
) : Parcelable {

    fun getUserLocationStr(): String{
        if(city == null && state == null && addressSlug != null){
            return addressSlug
        }else{
            val street = streetLine1?.let{"${it},"} ?: ""
            val apt = streetLine2?.let{",${it}"} ?: ""
            return "$street ${city?.name ?: ""} ${state?.name ?: ""} $apt"
        }
    }
    fun getUserShortLocationStr(): String{
        return "$streetLine1"
    }
    fun getDropoffLocationStr(): String {
        return when (dropOfLocationStr) {
            "delivery_to_door" -> "Deliver to door"
            else -> "Meet outside"
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