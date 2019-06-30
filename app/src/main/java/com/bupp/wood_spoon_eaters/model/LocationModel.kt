package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val id: Long? = null,
    val ownerId: Long? = null,
    val ownerType: String? = "",
    val lat: Double? = null,
    val lng: Double? = null,
    var country: Country? = null,
    val state: State? = null,
    val city: City? = null,
    @SerializedName("street_line_1") val streetLine1: String = "",
    @SerializedName("street_line_2") val streetLine2: String = "",
    @SerializedName("zipcode") val zipCode: String = "",
    val notes: String = "",
    @SerializedName("country_iso") val countryIso: String = "",
    @SerializedName("state_iso") val stateIso: String = "",
    @SerializedName("city_name") val cityName: String = ""
) : Parcelable

@Parcelize
data class Country(
    override val id: Long,
    override val name: String,
    val iso: String? = null
) : SelectableString, Parcelable

@Parcelize
data class State(
    val id: Long? = null,
    val name: String,
    val iso: String,
    val taxRate: Double? = null
) : Parcelable

@Parcelize
data class City(
    val id: Long? = null,
    val name: String
) : Parcelable