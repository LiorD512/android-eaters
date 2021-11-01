package com.bupp.wood_spoon_eaters.bottom_sheets.reviews

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionsViewType
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.EaterReview
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class Review(
    @Json(name = "reviews") val comments: List<Comment>
): Parcelable

@JsonClass(generateAdapter = true)
data class Metrics(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "rating") val rating: Double
)


sealed class CommentAdapterItem()

@Parcelize
@JsonClass(generateAdapter = true)
data class Comment(
    @Json(name = "id") val id: Long?,
    @Json(name = "rating") val rating: Double?,
    @Json(name = "review_text") val reviewText: String?,
    @Json(name = "review_date") val reviewDate: Date?,
    @Json(name = "eater") val eater: EaterReview?
): Parcelable, CommentAdapterItem()

class CommentSkeleton: CommentAdapterItem()

@JsonClass(generateAdapter = true)
data class ReviewRequest(
    @Json(name = "rating")  var rating: Int? = null,
    @Json(name = "review_text")  var reviewText: String? = null,
    @Json(name = "support_message") var supportMessage: String? = null,
)

@JsonClass(generateAdapter = true)
data class DishMetricsRequest(
    @Json(name = "dish_id")  val id: Long,
    @Json(name = "rating")  val rating: Int //values possible : 1, 0
)
