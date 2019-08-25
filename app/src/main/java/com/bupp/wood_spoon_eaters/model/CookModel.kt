package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.utils.Utils
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
    @SerializedName("place_of_birth") val country: Country = Country(),
    @SerializedName("certificates") val certificates: ArrayList<String>,
    @SerializedName("cuisines") val cuisines: ArrayList<CuisineLabel>,
    @SerializedName("diets") val diets: ArrayList<DietaryIcon>,
    @SerializedName("avg_rating") val rating: Double,
    @SerializedName("dishes") val dishes: ArrayList<Dish>
): Parcelable {
    fun getFullName(): String{
        return "$firstName $lastName"
    }

    fun getAge(): Int {
        //todo: get birthday from server and set age
        return Utils.getDiffYears(birthdate)
    }
}

//data class EaterRequest(
//    @SerializedName("first_name") var firstName: String = "",
//    @SerializedName("last_name") var lastName: String = "",
//    @SerializedName("thumbnail") var thumbnail: String = "",
//    @SerializedName("email") var email: String = "",
//    @SerializedName("addresses") var addresses: ArrayList<AddressRequest> = arrayListOf(),
//    @SerializedName("device") var device: Device? = null
//)
