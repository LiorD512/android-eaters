package com.bupp.wood_spoon_eaters.bottom_sheets.reviews

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.model.Eater
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class Review(
    @Json(name = "avg_accuracy_rating")  val accuracyRating: Double,
    @Json(name = "avg_delivery_rating")  val deliveryRating: Double,
    @Json(name = "avg_dish_rating")  val dishRating: Double,
    @Json(name = "reviews") val comments: List<Comment>
): Parcelable


@JsonClass(generateAdapter = true)
data class Metrics(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "rating") val rating: Double
)

@Parcelize
@JsonClass(generateAdapter = true)
data class Comment(
    @Json(name = "id") val id: Long,
    @Json(name = "body") val body: String,
    @Json(name = "eater") val eater: Eater
): Parcelable

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
