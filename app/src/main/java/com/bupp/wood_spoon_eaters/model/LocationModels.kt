package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
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
@JsonClass(generateAdapter = true)
data class AddressRequest(
    @Json(name = "street_number") var streetNumber: String? = null,
    @Json(name = "street_line_1") var streetLine1: String? = null,
    @Json(name = "street_line_2") var streetLine2: String? = null,
    @Json(name = "lat") var lat: Double? = null,
    @Json(name = "lng") var lng: Double? = null,
    @Json(name = "country_iso") var countryIso: String? = null,
    @Json(name = "state_iso") var stateIso: String? = null,
    @Json(name = "city_name") var cityName: String? = null,
    @Json(name = "dropoff_location") var dropoffLocation: String? = null, //Available values : delivery_to_door, pickup_outside
    @Json(name = "zipcode") var zipCode: String? = null,
    @Json(name = "notes") var notes: String? = null,
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
@JsonClass(generateAdapter = true)
data class Address(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "lat") var lat: Double? = null,
    @Json(name = "lng") var lng: Double? = null,
    @Json(name = "country") var country: Country? = null,
    @Json(name = "state") val state: State? = null,
    @Json(name = "city") val city: City? = null,
    @Json(name = "dropoff_location") var dropOfLocationStr: String? = null,
    @Json(name = "street_line_1") var streetLine1: String? = null,
    @Json(name = "street_line_2") var streetLine2: String? = null,
    @Json(name = "zipcode") val zipCode: String? = null,
    @Json(name = "notes") val notes: String? = null,
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
@JsonClass(generateAdapter = true)
data class Country(
    @Json(name = "id") val id: Long? = 0,
    @Json(name = "name") val name: String = "",
    @Json(name = "iso") val iso: String? = null,
    @Json(name = "flag_url") val flagUrl: String? = ""
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class State(
    @Json(name = "id") val id: Long?,
    @Json(name = "name") val name: String?,
    @Json(name = "iso") val iso: String? = null
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class City(
    @Json(name = "id") val id: Long?,
    @Json(name = "name") val name: String?
) : Parcelable