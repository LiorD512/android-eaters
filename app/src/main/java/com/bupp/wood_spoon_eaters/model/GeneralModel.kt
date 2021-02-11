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

enum class ErrorEventType {
    PHONE_EMPTY,
    CODE_EMPTY,
    INVALID_PHONE,
    WRONG_PASSWORD,
    SERVER_ERROR,
    SOMETHING_WENT_WRONG
}

data class WSError(
    @SerializedName("code") val code: Int?,
    @SerializedName("message") val msg: String?
)

data class CountriesISO(
    val name: String?,
    val value: String,
    val country_code: String?,
    val flag: String?
)

data class MetaDataModel(
    @SerializedName("cuisines") val cuisines: List<CuisineLabel>? = listOf(),
    @SerializedName("diets") val diets: List<DietaryIcon>? = listOf(),
    @SerializedName("metrics") val metrics: List<Metrics>? = listOf(),
    @SerializedName("report_topics") val reportTopic: List<ReportTopic>? = listOf(),
    @SerializedName("settings") val settings: List<AppSetting>? = listOf(),
    @SerializedName("notification_groups") val notificationsGroup: List<NotificationGroup>? = listOf(),
    @SerializedName("states") val states: List<State>? = listOf()
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