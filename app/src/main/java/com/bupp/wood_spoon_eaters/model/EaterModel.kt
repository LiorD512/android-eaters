package com.bupp.wood_spoon_eaters.model

import android.net.Uri
import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList


data class Eater(
    @SerializedName("id") val id: Long,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("account_status") val accountStatus: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("invite_url") val inviteUrl: String? = "",
    @SerializedName("email") val email: String,
    @SerializedName("addresses") val addresses: ArrayList<Address>,
    @SerializedName("notification_groups") val notificationsGroup: ArrayList<NotificationGroup>
){
    fun getFullName(): String{
       return "$firstName $lastName"
    }

    fun getNotificationGroupIds(): ArrayList<Long>{
        val array: ArrayList<Long> = arrayListOf()
        for(item in notificationsGroup){
            array.add(item.id)
        }
        return array
    }
}

data class EaterRequest(
    @SerializedName("first_name") var firstName: String = "",
    @SerializedName("last_name") var lastName: String = "",
    @SerializedName("thumbnail") var thumbnail: String? = null,
    @SerializedName("email") var email: String = "",
    @SerializedName("addresses") var addresses: ArrayList<AddressRequest> = arrayListOf(),
    @SerializedName("time_zone") var timezone: String = TimeZone.getDefault().id,
    @SerializedName("device") var device: Device? = null,
    var tempThumbnail: Uri? = null
)

data class DeviceDetails(
    @SerializedName("device") val device: Device? = null
)

data class Device(
    @SerializedName("device_token") val deviceToken: String = "",
    @SerializedName("os_type") val osType: Int = 1,
    @SerializedName("device_type") val deviceType: String = "",
    @SerializedName("os_version") val osVersion: String = "",
    @SerializedName("app_version") val appVersion: String = ""
)

data class Trigger(
    @SerializedName("should_rate") var shouldRateOrder: Order?
)

data class EphemeralKey(
    @SerializedName("id") var id: String = "",
    @SerializedName("object") var keyObject: String = "",
    @SerializedName("secret") var secret: String = ""
)