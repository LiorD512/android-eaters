package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.common.Constants
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlin.collections.ArrayList


data class SearchRequest(
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lng") var lng: Double? = null,
    @SerializedName("address_id") var addressId: Long? = null,
    @SerializedName("timestamp") var timestamp: String? = null,
    @SerializedName("q") var q: String? = "",
    @SerializedName("cuisine_ids") var cuisineIds: ArrayList<Long>? = null,
    @SerializedName("diet_ids") var dietIds: ArrayList<Long>? = null,
    @SerializedName("min_price") var minPrice: Double? = null,
    @SerializedName("max_price") var maxPrice: Double? = null,
    @SerializedName("asap_only") var isAsap: Boolean? = null
)

@Parcelize
data class Search(
    @SerializedName("id") var id: Long? = 0,
    @SerializedName("resource") var resource: String? = "",
    @SerializedName("results") var results: ArrayList<Parcelable>? = arrayListOf<Parcelable>(),
    @SerializedName("pagination") var pagination: Pagination = Pagination()
):Parcelable {
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
data class Pagination(
    @SerializedName("total_items") val totalItems: Int? = null,
    @SerializedName("item_count") val itemCount: Int? = null,
    @SerializedName("items_per_page") val itemsPerPage: Int? = null,
    @SerializedName("total_pages") val totalPages: Int? = null,
    @SerializedName("current_page") val currentPage: String? = null
):Parcelable