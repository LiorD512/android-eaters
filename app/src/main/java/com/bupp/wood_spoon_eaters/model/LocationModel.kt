package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddressRequest(
        @SerializedName("street_line_1") var streetLine1: String = "",
        @SerializedName("street_line_2") var streetLine2: String = "",
        @SerializedName("lat") var lat: Double? = null,
        @SerializedName("lng") var lng: Double? = null,
        @SerializedName("country_iso") var countryIso: String? = null,
        @SerializedName("state_iso") var stateIso: String? = null,
        @SerializedName("city_name") var cityName: String? = null,
        @SerializedName("zipcode") var zipCode: String = "",
        @SerializedName("notes") var notes: String = ""
) : Parcelable

@Parcelize
data class Address(
        val id: Long? = null,
        val lat: Double? = null,
        val lng: Double? = null,
        var country: Country? = null,
        val state: State? = null,
        val city: City? = null,
        @SerializedName("street_line_1") val streetLine1: String = "",
        @SerializedName("street_line_2") val streetLine2: String = "",
        @SerializedName("zipcode") val zipCode: String = "",
        @SerializedName("notes") val notes: String = ""
) : Parcelable

@Parcelize
data class Country(
        @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String,
        @SerializedName("iso") val iso: String? = null,
        @SerializedName("flag_url") val flagUrl: String? = null
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