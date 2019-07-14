package com.bupp.wood_spoon_eaters.model

import com.bupp.wood_spoon_eaters.utils.Constants
import com.google.gson.annotations.SerializedName


data class SearchRequest(
    var lat: Double? = null,
    var lng: Double? = null,
    var addressId: Long? = null,
    var timestamp: Int? = null,
    var q: String? = "",
    var cuisineIds: ArrayList<Int>? = null,
    var dietIds: ArrayList<Long>? = null,
    var minPrice: Double? = null,
    var maxPrice: Double? = null
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