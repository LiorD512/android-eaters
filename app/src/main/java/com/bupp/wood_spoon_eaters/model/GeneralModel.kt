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
//        val settings: ArrayList<Settings>,
    val cuisines: ArrayList<CuisineLabel>? = arrayListOf(),
    val diets: ArrayList<DietaryIcon>? = arrayListOf()
//    @SerializedName("cancellation_reasonsval") val cancellationReasons: ArrayList<CancellationReason>? = arrayListOf(),
//    @SerializedName("cooking_methods") val cookingMethods: ArrayList<CookingMethod>? = arrayListOf(),
//    val countries: ArrayList<Country>? = arrayListOf(),
//    val ingredients: ArrayList<Ingredient>? = arrayListOf(),
//    @SerializedName("prep_time_ranges") val prepTimeRanges: ArrayList<PrepTimeRange>? = arrayListOf(),
//    val units: ArrayList<WoodUnit>? = arrayListOf()
)

data class PrepTimeRange(
    val id: Long,
    val icon: String,
    @SerializedName("min_time") val minTime: Int,
    @SerializedName("max_time") val maxTime: Int
)

data class AppSetting(
    val id: Long,
    val key: String,
    val value: String,
    val data_type: Int,
    val description: String,
    val is_client_accessible: Boolean,

    val updated_at: Date
)

//orders grid data class

data class DietaryIcon(
    override val name: String,
    override val icon: String,
    override val id: Long
): SelectableIcon

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