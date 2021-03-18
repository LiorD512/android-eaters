package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Cook(
    @SerializedName("id") val id: Long,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("video") val video: String?,
    @SerializedName("profession") val profession: String,
    @SerializedName("about") val about: String,
    @SerializedName("birthdate") val birthdate: Date,
    @SerializedName("pickup_address") var pickupAddress: Address? = null,
    @SerializedName("place_of_birth") val country: Country? = null,
    @SerializedName("certificates") val certificates: List<String>,
    @SerializedName("cuisines") val cuisines: ArrayList<CuisineLabel>,
    @SerializedName("diets") val diets: ArrayList<DietaryIcon>,
    @SerializedName("avg_rating") val rating: Double,
    @SerializedName("reviews_count") var reviewCount: Int = 0,
    @SerializedName("dishes") val dishes: ArrayList<Dish>
): Parcelable {
    fun getFullName(): String{
        return "$firstName $lastName"
    }
}