package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import androidx.annotation.Nullable
import com.bupp.wood_spoon_eaters.di.abs.SerializeNulls
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.util.*

@JsonClass(generateAdapter = true)
data class OrderRequest(
    var cookId: Long? = null,
    @Json(name = "cooking_slot_id") var cookingSlotId: Long? = null,
    @Json(name = "deliver_at") var deliveryAt: String? = null,
    @Json(name = "delivery_address") var deliveryAddress: Address? = null,
    @Json(name = "delivery_address_id") var deliveryAddressId: Long? = null,
    @Json(name = "order_items") var orderItemRequests: List<OrderItemRequest>? = null,
    @Json(name = "tip_percentage") @SerializeNulls var tipPercentage: Float? = null,
    @Json(name = "tip") var tip: Int? = null,
    @Json(name = "disposable_tableware") var addUtensils: Boolean? = null,
    @Json(name = "shipping_service") var shippingService: String? = null,
    @Json(name = "promo_code") var promoCode: String? = null
)

@JsonClass(generateAdapter = true)
data class OrderItemRequest(
    @Json(name = "id") var id: Long? = null,
    @Json(name = "dish_id") val dishId: Long? = null,
    @Json(name = "quantity") var quantity: Int? = null,
    @Json(name = "removed_ingredient_ids") var removedIngredientsIds: List<Long>? = null,
    @Json(name = "notes") var notes: String? = null,
    @Json(name = "_destroy") var _destroy: Boolean? = false
)

@Parcelize
@JsonClass(generateAdapter = true)
data class Order (
    @Json(name = "id") val id: Long?,
    @Json(name = "order_number") val orderNumber: String?,
    @Json(name = "deliver_at") val deliverAt: Date?,
    @Json(name = "created_at") val created_at: Date?,
    @Json(name = "delivery_address") val deliveryAddress: Address?,
    @Json(name = "estimated_delivery_time") val estDeliveryTime: Date?,
    @Json(name = "eta_text") val estDeliveryTimeText: String?,
    @Json(name = "status") val status: String?,
    @Json(name = "courier") val courier: Courier?,
    @Json(name = "promo_code") val promoCode: String?,
    @Json(name = "status_updated_at") val statusUpdatedAt: Date?,
    @Json(name = "delivery_status") val deliveryStatus: String?,
    @Json(name = "preparation_status") val preparationStatus: String?,
    @Json(name = "delivery_status_updated_at") val deliveryStatusUpdatedAt: Date?,
    @Json(name = "tip_percentage") val tipPercentage: Int?,
    @Json(name = "notes") val notes: String?,
    @Json(name = "total") val total: Price?,
    @Json(name = "total_before_tip") val totalBeforeTip: Price?,
    @Json(name = "cook") val cook: Cook?,
    @Json(name = "cooking_slot") val cookingSlot: CookingSlot?,
    @Json(name = "order_items") val orderItems: List<OrderItem>?,
    @Json(name = "subtotal") val subtotal: Price?,
    @Json(name = "min_order_fee") val minPrice: Price?,
    @Json(name = "tax") val tax: Price?,
    @Json(name = "minOrderFee") val minOrderFee: Price?,
    @Json(name = "service_fee") val serviceFee: Price?,
    @Json(name = "delivery_fee") val deliveryFee: Price?,
    @Json(name = "cook_service_fee") val cooksServiceFee: Price?,
    @Json(name = "tip") val tip: Price?,
    @Json(name = "discount") val discount: Price?,
    @Json(name = "was_rated") val wasRated: Boolean?,
    @Json(name = "nationwide_shipping") val isNationwide: Boolean?
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class OrderItem(
    @Json(name = "id") val id: Long,
    @Json(name = "dish") val dish: Dish,
    @Json(name = "quantity") var quantity: Int,
    @Json(name = "matching_menu") var menuItem: MenuItem?,
    @Json(name = "removed_ingredients") var removedIngredients: List<Ingredient>,
    @Json(name = "price") val price: Price,
    @Json(name = "notes") var notes: String?,
    @Json(name = "_destroy") var _destroy: Boolean? = null
): Parcelable {
     fun getRemovedIngredientsIds(): List<Long>{
         return removedIngredients.mapNotNull { it.id }
     }
    fun toOrderItemRequest(): OrderItemRequest{
        return OrderItemRequest(
            id = id,
            notes = notes,
            dishId = dish.id,
            quantity = quantity,
            removedIngredientsIds = getRemovedIngredientsIds(),
            _destroy = _destroy
        )
    }

}

@Parcelize
@JsonClass(generateAdapter = true)
data class ShippingMethod(
    val code: String,
    val name: String,
    val fee: Price,
    val description: String
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Courier(
    val id: Long?,
    val first_name: String?,
    val last_name: String?,
    val thumbnail: String?,
    val phone_number: String?,
    val email: String?,
    val transport_type: String?,
    val lat: Double?,
    val lng: Double?
): Parcelable