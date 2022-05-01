package com.bupp.wood_spoon_chef.data.remote.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class AddressRequest(
        @Json(name = "street_line_1") var streetLine1: String? = "",
        @Json(name = "street_line_2") var streetLine2: String = "",
        @Json(name = "lat") var lat: Double? = null,
        @Json(name = "lng") var lng: Double? = null,
        @Json(name = "country_iso") var countryIso: String? = null,
        @Json(name = "state_iso") var stateIso: String? = null,
        @Json(name = "city_name") var cityName: String? = null,
        @Json(name = "zipcode") var zipCode: String = "",
        @Json(name = "notes") var notes: String? = null,
        @Json(name = "street_number") var streetNumber: String = ""
) : Parcelable {
    fun getUserLocationStr(): String{
        val cityNameStr = cityName?.let{ "$it,"} ?: ""
        return "$streetNumber $streetLine1, $cityNameStr ${stateIso ?: ""}, $zipCode"
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class Address(
        @Json(name = "id") val id: Long? = null,
        @Json(name = "lat") val lat: Double? = null,
        @Json(name = "lng") val lng: Double? = null,
        @Json(name = "country") var country: Country? = null,
        @Json(name = "state") val state: State? = null,
        @Json(name = "city") val city: City? = null,
        @Json(name = "street_line_1") val streetLine1: String = "",
        @Json(name = "street_line_2") val streetLine2: String = "",
        @Json(name = "zipcode") val zipCode: String = "",
        @Json(name = "notes") val notes: String? = null
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Country(
        @Json(name = "id") override val id: Long,
        @Json(name = "name") override val name: String = "",
        @Json(name = "iso") val iso: String? = null,
        @Json(name = "flag_url") val flagUrl: String? = null
) : SelectableString, Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class State(
        @Json(name = "id") val id: Long,
        @Json(name = "name") val name: String,
        @Json(name = "iso") val iso: String? = null
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class City(
        @Json(name = "id") val id: Long,
        @Json(name = "name") val name: String
) : Parcelable