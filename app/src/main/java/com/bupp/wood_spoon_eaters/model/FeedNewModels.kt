package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.common.Constants
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
data class ServerResponse<T> (
    var code: Int = 0,
    var message: String? = null,
    var data: T? = null,
    var errors: List<WSError>? = null,
    val meta: Pagination? = null,
)

data class FeedSection(
    val title: String? = null,
    val href: String? = null,
    val collections: List<Parcelable>? = null
)


sealed class FeedSectionCollectionItem(
    @Json(name = "type") var type: String?
): Parcelable {
    abstract val id: Long?
    abstract val results: List<Parcelable>?
    abstract val pagination: Pagination
    fun cooksCount(): Int {
        results?.let{
            when (resource) {
                Constants.RESOURCE_TYPE_COOK -> {
                    return results!!.size
                }
                else -> return 0
            }
        }
        return 0
    }

    fun dishCount(): Int {
        results?.let{
            when (resource) {
                Constants.RESOURCE_TYPE_DISH -> {
                    return results!!.size
                }
                else -> return 0
            }
        }
        return 0
    }

    fun hasCooks(): Boolean {
        return cooksCount() > 0
    }

    fun hasDishes(): Boolean {
        return dishCount() > 0
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class CookSection(
    override val id: Long?,
    override val results: List<Cook>?,
    override val pagination: Pagination

): Parcelable, Search(Constants.RESOURCE_TYPE_COOK)



sealed class FeedModels(
    val viewType: FeedModelsViewType
)

enum class FeedModelsViewType{
    @Json(name = "available_coupons") COUPONS,
    @Json(name = "restaurant_overview") RESTAURANT,
}

data class FeedCampaign(
    val title: String?,
    val subtitle: String?,
    val thumbnail_url: String?
): FeedModels(FeedModelsViewType.COUPONS)