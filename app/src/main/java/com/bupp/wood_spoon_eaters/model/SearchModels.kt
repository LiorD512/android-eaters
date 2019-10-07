package com.bupp.wood_spoon_eaters.model

import com.bupp.wood_spoon_eaters.utils.Constants
import com.google.gson.annotations.SerializedName


data class SearchRequest(
    var lat: Double? = null,
    var lng: Double? = null,
    @SerializedName("address_id") var addressId: Long? = null,
    var timestamp: String? = null,
    var q: String? = "",
    @SerializedName("cuisine_ids") var cuisineIds: ArrayList<Int>? = null,
    @SerializedName("diet_ids") var dietIds: ArrayList<Long>? = null,
    @SerializedName("min_price") var minPrice: Double? = null,
    @SerializedName("max_price") var maxPrice: Double? = null
)


data class Search(
    var id: Long?,
    var resource: String?,
    var results: ArrayList<*>? = null,
    var pagination: Pagination?
) {
    fun cooksCount(): Int {
        when (resource) {
            Constants.RESOURCE_TYPE_COOK -> {
                return results!!.size
            }
            else -> return 0
        }
    }

    fun dishCount(): Int {
        when (resource) {
            Constants.RESOURCE_TYPE_DISH -> {
                return results!!.size
            }
            else -> return 0
        }
    }

    fun hasCooks(): Boolean {
        return cooksCount() > 0
    }

    fun hasDishes(): Boolean {
        return dishCount() > 0
    }
}

data class Pagination(
    @SerializedName("total") val total: Int?,
    @SerializedName("per_page") val perPage: Int?,
    @SerializedName("next_page") val nextPage: String?,
    @SerializedName("total_pages") val totalPages: Int?,
    @SerializedName("current_page") val currentPage: String?
)