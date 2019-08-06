package com.bupp.wood_spoon_eaters.model

import com.google.gson.annotations.SerializedName
import java.util.ArrayList


data class Review(
    @SerializedName("avg_accuracy_rating")  val accuracyRating: Double,
    @SerializedName("avg_delivery_rating")  val deliveryRating: Double,
    @SerializedName("avg_dish_rating")  val dishRating: Double,
    @SerializedName("reviews") val comments: ArrayList<Comment>
)

data class Metrics(
    val id: Long,
    val name: String,
    val description: String,
    val rating: Double
)

data class Comment(
    val id: Long,
    val body: String,
    val eater: Eater
)



data class ReviewRequest(
    @SerializedName("accuracy_rating")  var accuracyRating: Int? = null,
    @SerializedName("delivery_rating")  var deliveryRating: Int? = null,
    @SerializedName("dish_ratings") var dishMetrics: ArrayList<DishMetricsRequest>? = null,
    @SerializedName("body")  var body: String? = null
)

data class DishMetricsRequest(
    @SerializedName("dish_id")  val id: Long,
    @SerializedName("rating")  val rating: Int //values possible : 1, 0
)
