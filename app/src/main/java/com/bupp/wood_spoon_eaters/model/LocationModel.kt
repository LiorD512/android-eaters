package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class NewAddress(
    val googleAddressResponse: GoogleAddressResponse?,
    val address: Address?,
    val isDelivery: Boolean?
)

@Parcelize
data class AddressRequest(
    @SerializedName("street_line_1") var streetLine1: String = "",
    @SerializedName("street_line_2") var streetLine2: String = "",
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lng") var lng: Double? = null,
    @SerializedName("country_iso") var countryIso: String? = null,
    @SerializedName("state_iso") var stateIso: String? = null,
    @SerializedName("city_name") var cityName: String? = null,
    @SerializedName("dropoff_location") var dropoffLocation: String? = null, //Available values : delivery_to_door, pickup_outside
    @SerializedName("zipcode") var zipCode: String? = "",
    @SerializedName("notes") var notes: String? = ""
) : Parcelable

@Parcelize
data class Address(
    val id: Long? = null,
    var lat: Double? = null,
    var lng: Double? = null,
    var country: Country? = null,
    val state: State? = null,
    val city: City? = null,
    @SerializedName("dropoff_location") var dropOfLocationStr: String = "",
    @SerializedName("street_line_1") var streetLine1: String = "",
    @SerializedName("street_line_2") var streetLine2: String = "",
    @SerializedName("zipcode") val zipCode: String = "",
    @SerializedName("notes") val notes: String = ""
) : Parcelable {

    fun getDropoffLocationStr(): String {
        when (dropOfLocationStr) {
            "delivery_to_door" -> return "Delivery to door"
            else -> return "Pick up outside"
        }
    }
}

enum class DropOffLocation(str: String) {
    DELIVERY_TO_DOOR("Delivery to Door"),
    PICK_OUTSIDE("Pick outside")
}


@Parcelize
data class Country(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
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