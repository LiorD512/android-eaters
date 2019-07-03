package com.bupp.wood_spoon_eaters.model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList


data class Cook(
    @SerializedName("id") val id: Long,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("thumbnail") val thumbnail: String
){
    fun getFullName(): String{
        return "$firstName $lastName"
    }
}

//data class EaterRequest(
//    @SerializedName("first_name") var firstName: String = "",
//    @SerializedName("last_name") var lastName: String = "",
//    @SerializedName("thumbnail") var thumbnail: String = "",
//    @SerializedName("email") var email: String = "",
//    @SerializedName("addresses") var addresses: ArrayList<AddressRequest> = arrayListOf(),
//    @SerializedName("device") var device: Device? = null
//)
