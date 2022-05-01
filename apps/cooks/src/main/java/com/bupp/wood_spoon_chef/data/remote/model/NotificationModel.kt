package com.bupp.wood_spoon_chef.data.remote.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class NotificationItem(
        @Json(name = "title") val title: String?,
        @Json(name = "notification") val notification: Notification?,
        @Json(name = "is_sticky") val isSticky: Boolean
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class NotificationHeader(
        @Json(name = "title") val title: String
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Notification(
        @Json(name = "id") val id: Long,
        @Json(name = "created_at") val created_at: Date,
        @Json(name = "updated_at") val updated_at: Date,
        @Json(name = "notification_type") val notificationType: NotificationType,
        @Json(name = "is_seen") val isSeen: Boolean,
        @Json(name = "is_read") val isRead: Boolean,
        @Json(name = "body") val body: String,
        @Json(name = "linkable_type") val linkableType: String,
        @Json(name = "linkable_id") val linkableId: Long
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class NotificationType(
        @Json(name = "first_name") val name: String,
        @Json(name = "first_name") val clientAction: ClientAction
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ClientAction(
        @Json(name = "name") val name: String,
        @Json(name = "required_linkable_type") val requiredLinkableType: String
) : Parcelable