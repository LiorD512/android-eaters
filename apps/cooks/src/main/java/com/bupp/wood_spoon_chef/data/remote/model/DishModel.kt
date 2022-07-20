package com.bupp.wood_spoon_chef.data.remote.model

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import com.bupp.wood_spoon_chef.di.abs.SerializeNulls
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class Dish(
    @Json(name = "id") var id: Long? = null,
    @Json(name = "name") var name: String = "",
    @Json(name = "price") var price: Price? = null,
    @Json(name = "units_sold") var unitSold: Int = 0,
    @Json(name = "orders_count") var ordersCount: Int = 0,
    @Json(name = "offers_count") var offersCount: Int = 0,
    @Json(name = "total_profit") var totalProfit: Price? = null,
    @Json(name = "cuisines") var cuisines: List<Cuisine>? = null,
    @Json(name = "diets") var diets: List<DietaryIcon>? = null,
    @Json(name = "description") var description: String = "",
    @Json(name = "instructions") var instruction: String? = null,
    @Json(name = "image_gallery") var imageGallery: List<String>? = null,
    @Json(name = "status") var status: DishStatus? = null,
    @Json(name = "portion_size") var portionSize: String? = null,
    @Json(name = "video") var video: String? = null,
    @Json(name = "ups_item_weight") var upsItemWeight: Double? = null,
    @Json(name = "cooking_methods") var cookingMethods: List<CookingMethod>? = null,
    @Json(name = "cooking_time") var cookingTime: String = "",
    @Json(name = "prep_time_range") var prepTimeRangeId: PrepTimeRange? = null,
    @Json(name = "dietary_accommodations") var accommodations: String? = null,
    @Json(name = "ingredients") var ingredients: String? = null,
    @Json(name = "ingredients_cost") var ingredientsCost: Price? = null,
    @Json(name = "suggested_retail_price") var suggestedPrice: Price? = null,
    @Json(name = "min_retail_price") var minPrice: Price? = null,
    @Json(name = "max_retail_price") var maxPrice: Price? = null,
    @Json(name = "avg_rating") var rating: Double? = null,
    @Json(name = "calorific_value") var calorificValue: Int = 0,
    @Json(name = "category") var category: DishCategory? = null,
    var dishType: Int = 0
) : Parcelable {
    fun getMediaList(): List<MediaList> {
        val mediaList = arrayListOf<MediaList>()
        imageGallery?.forEach {
            mediaList.add(MediaList(it, true))
        }
        video?.let {
            mediaList.add(MediaList(it, false))
        }
        return mediaList
    }

    fun isActive(): Boolean {
        return status == DishStatus.ACTIVE
    }

    fun isDraft(): Boolean {
        return status == DishStatus.DRAFT
    }

    fun isUnpublished(): Boolean {
        return status == DishStatus.HIDDEN
    }

    fun toDishRequest(): DishRequest {
        return DishRequest(
            id = id,
            name = name,
            description = description,
            dietaryIds = getDishDietaryIds(),
            cuisineIds = getSelectedCuisineId(),
            cookingMethodIds = getSelectedMethodId(),
            instruction = instruction,
            imageGallery = imageGallery?.toMutableList(),
            video = video,
            cookingTime = cookingTime,
            prepTimeRangeId = prepTimeRangeId?.id,
            accommodations = accommodations,
            portionSize = portionSize,
            ingredients = ingredients,
            price = price?.cents?.toInt(),
            currentStatus = status,
            dishCategoryId = category?.id,
            dishCategory = category
        )
    }

    private fun getDishDietaryIds(): MutableList<Long>? {
        diets?.let {
            val dietsIds = mutableListOf<Long>()
            for (diet in it) {
                dietsIds.add(diet.id)
            }
            return dietsIds
        }
        return null
    }

    private fun getSelectedCuisineId(): MutableList<Long>? {
        cuisines?.let {
            val cuisineIds = mutableListOf<Long>()
            for (cuisine in it) {
                cuisineIds.add(cuisine.id!!)
            }
            return cuisineIds
        }
        return null
    }

    private fun getSelectedMethodId(): MutableList<Long>? {
        cookingMethods?.let {
            val cookingMethodsIds = mutableListOf<Long>()
            for (cookingMethod in it) {
                cookingMethodsIds.add(cookingMethod.id)
            }
            return cookingMethodsIds
        }
        return null
    }

    fun getCuisineAsString(): String {
        var dietsStr = ""
        cuisines?.forEach {
            dietsStr += "${it.name}, "
        }
        if(dietsStr.length > 2){
            return dietsStr.substring(0, dietsStr.length - 2)
        }
        return dietsStr
    }

}

data class MediaList(
    val media: String,
    val isImage: Boolean
)

@Parcelize
@JsonClass(generateAdapter = true)
data class SectionWithDishes(
    @Json(name = "dishes") val dishes: List<Dish>?,
    @Json(name = "sections") val sections: List<Section>?
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Section(
    @Json(name = "title") val title: String?,
    @Json(name = "dish_ids") val dishIds: List<Long>?
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class DishIngredient(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "ingredient") val ingredient: Ingredient?,
    @Json(name = "quantity") val quantity: Double?,
    @Json(name = "unit") var unit: WoodUnit,
    @Json(name = "is_adjustable") val isAdjustable: Boolean = false
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class DishRequest(
    @Json(name = "id") var id: Long? = null,
    @Json(name = "name") var name: String = "",
    @Json(name = "cuisine_ids") @SerializeNulls var cuisineIds: MutableList<Long>? = mutableListOf(),
    @Json(name = "diet_ids") @SerializeNulls var dietaryIds: MutableList<Long>? = mutableListOf(),
    @Json(name = "description") var description: String = "",
    @Json(name = "instructions") var instruction: String? = null,
    @Json(name = "image_gallery") @SerializeNulls var imageGallery: MutableList<String?>? = mutableListOf(),
    @Json(name = "cooking_method_ids") var cookingMethodIds: MutableList<Long>? = null,
    @Json(name = "cooking_time") var cookingTime: String? = null,
    @Json(name = "prep_time_range_id") var prepTimeRangeId: Long? = null,
    @Json(name = "video") @SerializeNulls var video: String? = null,
    @Json(name = "dietary_accommodations") var accommodations: String? = null,
    @Json(name = "portion_size") var portionSize: String? = null,
    @Json(name = "category_id") var dishCategoryId: Long? = null,
    @Json(name = "ingredients") var ingredients: String? = null,
    @Json(name = "ups_item_weight") var upsItemWeight: Double? = null,
    @Json(name = "batch_discount") var batchDiscount: Int? = null,
    @Json(name = "is_batch_enabled") var isBatchEnabled: Boolean? = null,
    @Json(name = "original_batch_size") var batchItemCounter: Int? = null,
    @Json(name = "dish_ingredients") var dishIngredientRequests: MutableList<DishIngredientRequest>? = null,
    @Json(name = "price") var price: Int? = null,
    var tempThumbnail: Uri? = null,
    var tempImageGallery: MutableList<Uri?> = mutableListOf(),
    var tempVideo: Uri? = null,
    var freeDelivery: Boolean = true,
    var currentStatus: DishStatus? = null,
    var dishCategory: DishCategory? = null
) : Parcelable {
    fun hasTempMedia(): Boolean {
        return tempThumbnail != null || tempImageGallery.size > 0 || tempVideo != null
    }

    fun getDishStatus(): NewDishStatus {
        return if (id == null) {
            NewDishStatus.NEW_DISH
        } else {
            when (currentStatus) {
                DishStatus.DRAFT -> {
                    NewDishStatus.EDIT_DRAFT
                }
                DishStatus.HIDDEN -> {
                    NewDishStatus.EDIT_UNPUBLISHED
                }
                else -> {
                    NewDishStatus.EDIT_DISH
                }
            }
        }
    }

    fun removeVideo() {
        when (getDishStatus()) {
            NewDishStatus.NEW_DISH -> {
                tempVideo = null
            }
            else -> {
                video = null
                tempVideo = null
            }
        }
    }

    fun removeMedia(uri: Uri) {
        when (getDishStatus()) {
            NewDishStatus.NEW_DISH -> {
                tempImageGallery.remove(uri)
            }
            NewDishStatus.EDIT_DISH -> {
                val removeFromGallery = imageGallery?.remove(uri.toString())
                if (removeFromGallery == false) {
                    tempImageGallery.remove(uri)
                }
            }
            NewDishStatus.EDIT_DRAFT -> {
                imageGallery?.remove(uri.toString())
            }
        }
    }

    fun hasMainPhoto(): Boolean {
        return tempThumbnail != null || !imageGallery.isNullOrEmpty()
    }

    fun addOrUpdateMainPhoto(preSignedUrlKey: String) {
        when (getDishStatus()) {
            NewDishStatus.NEW_DISH -> {
                if (imageGallery == null) {
                    imageGallery = mutableListOf()
                }
                imageGallery!!.add(preSignedUrlKey)
            }
            NewDishStatus.EDIT_DISH -> {
                imageGallery!![0] = preSignedUrlKey
            }
            NewDishStatus.EDIT_DRAFT, NewDishStatus.EDIT_UNPUBLISHED -> {
                if (imageGallery == null) {
                    imageGallery = mutableListOf()
                    imageGallery!!.add(preSignedUrlKey)
                } else if (imageGallery!!.isNotEmpty()) {
                    imageGallery!![0] = preSignedUrlKey
                } else {
                    imageGallery!!.add(preSignedUrlKey)
                }
            }
        }
    }
}

@Keep
enum class NewDishStatus {
    NEW_DISH,
    EDIT_DISH,
    EDIT_DRAFT,
    EDIT_UNPUBLISHED
}

@Parcelize
@JsonClass(generateAdapter = true)
data class DishIngredientRequest(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "ingredient_id") val ingredientId: Long?,
    @Json(name = "quantity") val quantity: Double?,
    @Json(name = "unit_id") val unitId: Long?,
    @Json(name = "is_adjustable") val isAdjustable: Boolean?,
    @Json(name = "_destroy") val _destroy: Boolean? = null
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class DishPricing(
    @Json(name = "ingredients_cost") val ingredientsCost: Price?,
    @Json(name = "suggested_retail_price") val suggestedRetailPrice: Price?,
    @Json(name = "min_retail_price") val minRetailPrice: Price?,
    @Json(name = "max_retail_price") val maxRetailPrice: Price?
) : Parcelable

@Parcelize
@Keep
enum class DishStatus: Parcelable {
    @Json(name = "draft")
    DRAFT,

    @Json(name = "hidden")
    HIDDEN,

    @Json(name = "active")
    ACTIVE,

    @Json(name = "removed")
    REMOVED,
}
@Keep
enum class DishUpdatedDialogStatus {
    DRAFT_PUBLISH,
    NEW_DISH_PUBLISH,
    UNPUBLISH,
    DUPLICATE,
    SAVE_TO_DRAFT,
    UPDATE_CHANGES,
    HIDE,

}

