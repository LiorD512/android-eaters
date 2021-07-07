package com.bupp.wood_spoon_eaters.model

import android.net.Uri
import android.os.Parcelable
import com.bupp.wood_spoon_eaters.di.abs.SerializeNulls
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import retrofit2.http.Field
import java.util.*

@kotlinx.parcelize.Parcelize
@JsonClass(generateAdapter = true)
data class Eater(
   @Json(name = "id") val id: Long,
   @Json(name = "phone_number") val phoneNumber: String,
   @Json(name = "account_status") val accountStatus: String,
   @Json(name = "first_name") val firstName: String?,
   @Json(name = "last_name") val lastName: String?,
   @Json(name = "thumbnail") val thumbnail: String?,
   @Json(name = "invite_url") val inviteUrl: String?,
   @Json(name = "email") val email: String?,
   @Json(name = "created_at") val createdAt: Date?,
   @Json(name = "orders_count") val ordersCount: Int = 0,
   @Json(name = "addresses") val addresses: List<Address>,
   @Json(name = "cuisines") var cuisines: List<CuisineLabel>? = null,
   @Json(name = "diets") var diets: List<DietaryIcon>? = null,
   @Json(name = "share_campaign") val shareCampaign: Campaign? = null,
   @Json(name = "notification_groups") val notificationsGroup: List<NotificationGroup>
): Parcelable{
    fun getFullName(): String{
        var first = "Anonymous"
        var last = ""
        if(firstName != null && firstName?.isNotEmpty())
            first = firstName
        if(lastName != null && lastName?.isNotEmpty())
            last = lastName
       return "$first $last"
    }

    fun getNotificationGroupIds(): ArrayList<Long>{
        val array: ArrayList<Long> = arrayListOf()
        for(item in notificationsGroup){
            array.add(item.id)
        }
        return array
    }

    fun getSelectedCuisines(): List<SelectableIcon>{
        return cuisines as List<SelectableIcon>
    }
}

@JsonClass(generateAdapter = true)
data class EaterRequest(
    @Json(name = "first_name") var firstName: String? = null,
    @Json(name = "last_name") var lastName: String? = null,
    @Json(name = "thumbnail") var thumbnail: String? = null,
    @Json(name = "email") var email: String? = null,
    @Json(name = "addresses") var addresses: List<AddressRequest>? = null,
    @Json(name = "time_zone") var timezone: String? = TimeZone.getDefault().id ?: null,
    @Json(name = "cuisine_ids") var cuisineIds: List<Int>? = null,
    @Json(name = "diet_ids") var dietIds: List<Int>? = null,
    var tempThumbnail: Uri? = null
)

@JsonClass(generateAdapter = true)
data class SettingsRequest(
    @Json(name = "notification_group_ids") @SerializeNulls var notification_group_ids: List<Long>? = null
)

@JsonClass(generateAdapter = true)
data class DeviceDetails(
    @Json(name = "device") val device: Device? = null
)

@JsonClass(generateAdapter = true)
data class Device(
    @Json(name = "device_token") val deviceToken: String = "",
    @Json(name = "os_type") val osType: Int = 1,
    @Json(name = "device_type") val deviceType: String = "",
    @Json(name = "os_version") val osVersion: String = "",
    @Json(name = "app_version") val appVersion: String = ""
)

@JsonClass(generateAdapter = true)
data class Trigger(
    @Json(name = "should_rate") var shouldRateOrder: Order? = null
)

@JsonClass(generateAdapter = true)
data class EphemeralKey(
    @Json(name = "id") var id: String = "",
    @Json(name = "object") var keyObject: String = "",
    @Json(name = "secret") var secret: String = ""
)