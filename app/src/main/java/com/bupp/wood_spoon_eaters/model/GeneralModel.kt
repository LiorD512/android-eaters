package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

data class ServerResponse<T> (
    var code: Int = 0,
    var message: String? = null,
    var data: T? = null
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
    val id: Long,
    val name: String
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
    val id: Long,
    val icon: String,
    @SerializedName("min_time") val minTime: Int,
    @SerializedName("max_time") val maxTime: Int
)

data class AppSetting(
    var id: Long?,
    var key: String?,
    var dataType: String?,
    var value: Any?
)

//orders grid data class

@Parcelize
data class DietaryIcon(
    override val name: String,
    override val icon: String,
    override val id: Long
): SelectableIcon, Parcelable

@Parcelize
data class CuisineLabel(
    override val name: String,
    override val icon: String,
    val cover: String,
    override val id: Long
): SelectableIcon, Parcelable

interface SelectableIcon {
    val id: Long
    val name: String
    val icon: String
}

interface SelectableString {
    val id: Long
    val name: String
}



data class PreSignedUrl(
    val key: String,
    val url: String
)