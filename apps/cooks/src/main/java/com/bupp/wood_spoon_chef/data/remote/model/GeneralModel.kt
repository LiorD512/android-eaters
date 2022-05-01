package com.bupp.wood_spoon_chef.data.remote.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.bupp.wood_spoon_chef.data.remote.network.base.WSError
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

data class CountriesISO(
        val name: String?,
        val value: String,
        val country_code: String?,
        val flag: String?
)

@Parcelize
@JsonClass(generateAdapter = true)
data class DeviceDetails(
        @Json(name = "device") val device: Device? = null
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Device(
        @Json(name = "device_token") val deviceToken: String = "",
        @Json(name = "os_type") val osType: Int = 1,
        @Json(name = "device_type") val deviceType: String = "",
        @Json(name = "os_version") val osVersion: String = "",
        @Json(name = "app_version") val appVersion: String = ""
) : Parcelable

@JsonClass(generateAdapter = true)
data class MetaDataModel(
        @Json(name = "settings") val settings: List<AppSetting>? = mutableListOf(),
        @Json(name = "ff") val ff: Map<String, Boolean>? = mutableMapOf(),
        @Json(name = "cancellation_reasons") val cancellationReasons: List<CancellationReason>? = mutableListOf(),
        @Json(name = "cooking_methods") val cookingMethods: List<CookingMethod>? = mutableListOf(),
        @Json(name = "countries") val countries: List<Country>? = mutableListOf(),
        @Json(name = "dish_categories") val dishCategories: List<DishCategory>? = mutableListOf(),
        @Json(name = "cuisines") val cuisines: List<CuisineIcon>? = mutableListOf(),
        @Json(name = "diets") val diets: List<DietaryIcon>? = mutableListOf(),
        @Json(name = "ingredients") val ingredients: List<Ingredient>? = mutableListOf(),
        @Json(name = "prep_time_ranges") val prepTimeRanges: List<PrepTimeRange>? = mutableListOf(),
        @Json(name = "units") val units: List<WoodUnit>? = mutableListOf()
)

@Parcelize
@JsonClass(generateAdapter = true)
data class PrepTimeRange(
        @Json(name = "id") val id: Long,
        @Json(name = "icon") val icon: String,
        @Json(name = "min_time") val minTime: Int,
        @Json(name = "max_time") val maxTime: Int
) : Parcelable {
    fun getRangeStr(): String {
        return "$minTime - $maxTime"
    }
}

@Keep
enum class AppSettingKnownTypes {
    string, integer, decimal, price, key_value, boolean, csv_array
}

@JsonClass(generateAdapter = true)
data class AppSetting(
        @Json(name = "id") var id: Long?,
        @Json(name = "key") var key: String?,
        @Json(name = "data_type") var dataType: String?,
        @Json(name = "value") var value: Any?
)

//icons grid data class
interface SelectableIcon {
    val id: Long
    val name: String
    val icon: String
    val iconSelected: String?
}

interface SelectableString {
    val id: Long
    val name: String
}

@Parcelize
@JsonClass(generateAdapter = true)
data class DietaryIcon(
        @Json(name = "name") override val name: String,
        @Json(name = "icon") override val icon: String,
        @Json(name = "icon_selected") override val iconSelected: String?,
        @Json(name = "id") override val id: Long
) : SelectableIcon, Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class CuisineIcon(
        @Json(name = "name") override val name: String,
        @Json(name = "icon") override val icon: String,
        @Json(name = "icon_selected") override val iconSelected: String?,
        @Json(name = "id") override val id: Long
) : SelectableIcon, Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class DishCategory(
        @Json(name = "id") val id: Long,
        @Json(name = "name") val name: String
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class PreSignedUrl(
        @Json(name = "key") val key: String,
        @Json(name = "url") val url: String
) : Parcelable