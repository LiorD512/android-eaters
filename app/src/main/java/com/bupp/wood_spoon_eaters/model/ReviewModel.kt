package com.bupp.wood_spoon_eaters.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Review(
    @Json(name = "avg_accuracy_rating")  val accuracyRating: Double,
    @Json(name = "avg_delivery_rating")  val deliveryRating: Double,
    @Json(name = "avg_dish_rating")  val dishRating: Double,
    @Json(name = "reviews") val comments: List<Comment>
)

@JsonClass(generateAdapter = true)
data class Metrics(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "rating") val rating: Double
)

@JsonClass(generateAdapter = true)
data class Comment(
    @Json(name = "id") val id: Long,
    @Json(name = "body") val body: String,
    @Json(name = "eater") val eater: Eater
)

@JsonClass(generateAdapter = true)
data class ReviewRequest(
    @Json(name = "accuracy_rating")  var accuracyRating: Int? = null,
    @Json(name = "delivery_rating")  var deliveryRating: Int? = null,
    @Json(name = "dish_ratings") var dishMetrics: List<DishMetricsRequest>? = null,
    @Json(name = "body")  var body: String? = null
)

@JsonClass(generateAdapter = true)
data class DishMetricsRequest(
    @Json(name = "dish_id")  val id: Long,
    @Json(name = "rating")  val rating: Int //values possible : 1, 0
)
