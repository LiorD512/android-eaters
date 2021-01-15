package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

data class ServerResponse<T> (
    @SerializedName("code") var code: Int = 0,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: T? = null,
    @SerializedName("errors") var errors: List<WSError>? = null,
    @SerializedName("meta") val meta: Pagination,

)

//data class WSServerError(
//    @SerializedName("code") val resultCode: Int? = null,
//    @SerializedName("message") val msg: String? = null,
//    @SerializedName("errors") var errors: List<WSError>? = null
//)
data class WSError(
    @SerializedName("code") val code: Int?,
    @SerializedName("message") val msg: String?
)

data class MetaDataModel(
    @SerializedName("cuisines") val cuisines: ArrayList<CuisineLabel>? = arrayListOf(),
    @SerializedName("diets") val diets: ArrayList<DietaryIcon>? = arrayListOf(),
    @SerializedName("metrics") val metrics: ArrayList<Metrics>? = arrayListOf(),
    @SerializedName("report_topics") val reportTopic: ArrayList<ReportTopic>? = arrayListOf(),
    @SerializedName("settings") val settings: ArrayList<AppSetting>? = arrayListOf(),
    @SerializedName("notification_groups") val notificationsGroup: ArrayList<NotificationGroup>? = arrayListOf()
)

data class ReportTopic(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("report_issues") val reportIssues: java.util.ArrayList<ReportIssue>
)

data class NotificationGroup(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)

data class ReportIssue(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String
)

data class Reports(
    @SerializedName("reports") val reviews: ArrayList<ReportRequest>
)

data class ReportRequest(
    @SerializedName("topic_id") val topicId: Long? = null,
    @SerializedName("issue_id") val issueId: Long? = null,
    @SerializedName("body") val body: String? = null
)

data class PrepTimeRange(
    @SerializedName("id") val id: Long,
    @SerializedName("icon") val icon: String,
    @SerializedName("min_time") val minTime: Int,
    @SerializedName("max_time") val maxTime: Int
)

data class AppSetting(
    @SerializedName("id") var id: Long?,
    @SerializedName("key") var key: String?,
    @SerializedName("dataType") var dataType: String?,
    @SerializedName("value") var value: Any?
)

//orders grid data class

@Parcelize
data class DietaryIcon(
    @SerializedName("name") override val name: String,
    @SerializedName("icon") override val icon: String,
    @SerializedName("id") override val id: Long
): SelectableIcon, Parcelable

@Parcelize
data class CuisineLabel(
    @SerializedName("name") override val name: String,
    @SerializedName("icon") override val icon: String,
    @SerializedName("cover") val cover: String,
    @SerializedName("id") override val id: Long
): SelectableIcon, Parcelable

interface SelectableIcon {
    val id: Long
    val name: String?
    val icon: String
}

interface SelectableString {
    val id: Long
    val name: String
}



data class PreSignedUrl(
    @SerializedName("key") val key: String,
    @SerializedName("url") val url: String
)