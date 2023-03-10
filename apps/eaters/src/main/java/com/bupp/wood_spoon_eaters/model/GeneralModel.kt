package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.Metrics
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.ArrayList

@JsonClass(generateAdapter = true)
data class ServerResponse<T>(
    var code: Int = 0,
    var message: String? = null,
    var data: T? = null,
    var errors: List<WSError>? = null,
    val meta: Pagination? = null,
)

enum class ErrorEventType {
    PHONE_EMPTY,
    CODE_EMPTY,
    INVALID_PHONE,
    WRONG_PASSWORD,
    SERVER_ERROR,
    SOMETHING_WENT_WRONG
}

@JsonClass(generateAdapter = true)
data class WSError(
    @Json(name = "code") val code: Int?,
    @Json(name = "message") val msg: String?
)

@JsonClass(generateAdapter = true)
@Parcelize
data class WSImage(
    @Json(name = "url") val url: String?,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class CountriesISO(
    val name: String?,
    val value: String,
    val country_code: String?,
    val flag: String?
) : Parcelable

@JsonClass(generateAdapter = true)
data class MetaDataModel(
    @Json(name = "cuisines") val cuisines: List<CuisineLabel>? = listOf(),
    @Json(name = "diets") val diets: List<DietaryIcon>? = listOf(),
    @Json(name = "metrics") val metrics: List<Metrics>? = listOf(),
    @Json(name = "report_topics") val reportTopic: List<ReportTopic>? = listOf(),
    @Json(name = "notification_groups") val notificationsGroup: List<NotificationGroup>? = listOf(),
    @Json(name = "states") val states: List<State>? = listOf(),
    @Json(name = "countries") val countries: List<Country>? = listOf(),
    @Json(name = "welcome_screens") val welcome_screens: List<WelcomeScreen>? = listOf(),
)

@JsonClass(generateAdapter = true)
data class ReportTopic(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "report_issues") val reportIssues: List<ReportIssue>
)

@kotlinx.parcelize.Parcelize
@JsonClass(generateAdapter = true)
data class NotificationGroup(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String
) : Parcelable

@JsonClass(generateAdapter = true)
data class ReportIssue(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String
)

@JsonClass(generateAdapter = true)
data class Reports(
    @Json(name = "reports") val reviews: List<ReportRequest>
)

@JsonClass(generateAdapter = true)
data class ReportRequest(
    @Json(name = "topic_id") val topicId: Long? = null,
    @Json(name = "issue_id") val issueId: Long? = null,
    @Json(name = "body") val body: String? = null
)

@JsonClass(generateAdapter = true)
data class PrepTimeRange(
    @Json(name = "id") val id: Long,
    @Json(name = "icon") val icon: String,
    @Json(name = "min_time") val minTime: Int,
    @Json(name = "max_time") val maxTime: Int
)

@JsonClass(generateAdapter = true)
data class AppSettings(
    @Json(name = "settings") val settings: List<AppSetting>?,
    @Json(name = "ff") val ff: Map<String, Boolean>?
)

@JsonClass(generateAdapter = true)
data class AppSetting(
    val id: Long?,
    val key: String?,
    val data_type: String?,
    val value: Any?
)

@Keep
enum class AppSettingKnownTypes {
    string, integer, decimal, price, key_value, boolean, csv_array
}

enum class CloudinaryTransformationsType {
    @Json(name = "small")
    SMALL,
    MEDIUM, LARGE
}

@JsonClass(generateAdapter = true)
data class CloudinaryTransformations(
    var keyValueMap: Map<CloudinaryTransformationsType, String>?
) {
    fun getByType(type: CloudinaryTransformationsType): String? {
        //todo - Amitt! check this !
        return keyValueMap?.get(type)
    }
}

@JsonClass(generateAdapter = true)
data class WelcomeScreen(
    @Json(name = "text") var text: String?,
    @Json(name = "image_url") var url: String?
)


//icons grid data class
interface SelectableIcon {
    val id: Long
    val name: String
    val icon: String
    val iconSelected: String?
}

@Parcelize
@JsonClass(generateAdapter = true)
data class DietaryIcon(
    @Json(name = "name") override val name: String,
    @Json(name = "icon") override val icon: String,
    @Json(name = "icon_selected") override val iconSelected: String? = null,
    @Json(name = "icon_dish") val iconDish: String? = null,
    @Json(name = "id") override val id: Long
) : SelectableIcon, Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class CuisineLabel(
    @Json(name = "name") override val name: String,
    @Json(name = "icon") override val icon: String,
    @Json(name = "icon_selected") override val iconSelected: String? = null,
    @Json(name = "cover") val cover: String?,
    @Json(name = "id") override val id: Long
) : SelectableIcon, Parcelable


@JsonClass(generateAdapter = true)
data class PreSignedUrl(
    @Json(name = "key") val key: String,
    @Json(name = "url") val url: String
)