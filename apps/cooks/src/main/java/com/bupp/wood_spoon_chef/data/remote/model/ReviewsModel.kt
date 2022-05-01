package com.bupp.wood_spoon_chef.data.remote.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class Review(
    @Json(name = "reviews") val comments: List<Comment>?,
    @Json(name = "breakdown") val breakdown: ReviewBreakdown?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ReviewBreakdown(
    @Json(name = "5") val countOf5: Int = 0,
    @Json(name = "4") val countOf4: Int = 0,
    @Json(name = "3") val countOf3: Int = 0,
    @Json(name = "2") val countOf2: Int = 0,
    @Json(name = "1") val countOf1: Int = 0,
) : Parcelable

@JsonClass(generateAdapter = true)
data class Metrics(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "rating") val rating: Double
)

sealed class CommentAdapterItem

@Parcelize
@JsonClass(generateAdapter = true)
data class Comment(
    @Json(name = "review_date") val reviewDate: Date?,
    @Json(name = "rating") val rating: Int?,
    @Json(name = "review_text") val reviewText: String?,
    @Json(name = "eater") val eater: Eater?
) : Parcelable, CommentAdapterItem()

class CommentSkeleton : CommentAdapterItem()