package com.bupp.wood_spoon_eaters.model

import android.net.Uri
import com.google.gson.annotations.SerializedName
import kotlin.collections.ArrayList


data class Eater(
    @SerializedName("id") val id: Long,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("account_status") val accountStatus: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("email") val email: String,
    @SerializedName("addresses") val addresses: ArrayList<Address>,
    val fullName: String = "$firstName $lastName"
)

data class EaterRequest(
    @SerializedName("first_name") var firstName: String = "",
    @SerializedName("last_name") var lastName: String = "",
    @SerializedName("thumbnail") var thumbnail: String = "",
    @SerializedName("email") var email: String = "",
    @SerializedName("addresses") var addresses: ArrayList<AddressRequest> = arrayListOf(),
    @SerializedName("device") var device: Device? = null,
    var tempThumbnail: Uri? = null
)

data class Device(
    val deviceToken: String = "",
    val osType: Int = 1,
    val deviceType: String = "",
    val osVersion: String = "",
    val appVersion: String = ""
)