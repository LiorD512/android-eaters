package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

data class OrderRequest(
    @SerializedName("cooking_slot_id") var cookingSlotId: Long? = null,
    @SerializedName("deliver_at") var deliveryAt: String? = null,
    var deliveryAddress: Address? = null,
    @SerializedName("delivery_address_id") var deliveryAddressId: Long? = null,
    @SerializedName("order_items") var orderItemRequests: ArrayList<OrderItemRequest>? = null,
    @SerializedName("tip_percentage") var tipPercentage: Float? = null,
    @SerializedName("tip") var tip: Int? = null,
    @SerializedName("tip_amount") var tipAmount: String? = null,
    @SerializedName("promo_code") var promoCode: String? = null
)

data class OrderItemRequest(
    @SerializedName("id") var id: Long? = null,
    @SerializedName("dish_id") val dishId: Long? = null,
    @SerializedName("quantity") var quantity: Int? = null,
    @SerializedName("removed_ingredient_ids") var removedIndredientsIds: ArrayList<Long>? = null,
    @SerializedName("notes") var notes: String? = null
)

@Parcelize
data class Order (
    @SerializedName("id") val id: Long,
    @SerializedName("order_number") val orderNumber: String,
    @SerializedName("deliver_at") val deliverAt: Date,
    @SerializedName("delivery_address") val deliveryAddress: Address,
    @SerializedName("estimated_delivery_time") val estDeliveryTime: Date,
    @SerializedName("status") val status: String,
    @SerializedName("status_updated_at") val statusUpdatedAt: Date,
    @SerializedName("delivery_status") val deliveryStatus: String,
    @SerializedName("preparation_status") val preparationStatus: String,
    @SerializedName("delivery_status_updated_at") val deliveryStatusUpdatedAt: Date,
    @SerializedName("tip_percentage") val tipPercentage: Int,
    @SerializedName("notes") val notes: String,
    @SerializedName("total") val total: Price,
    @SerializedName("cook") val cook: Cook,
    @SerializedName("cooking_slot") val cookingSlot: CookingSlot,
    @SerializedName("order_items") val orderItems: ArrayList<OrderItem>,
    @SerializedName("subtotal") val subtotal: Price,
    @SerializedName("tax") val tax: Price,
    @SerializedName("service_fee") val serviceFee: Price,
    @SerializedName("delivery_fee") val deliveryFee: Price,
    @SerializedName("tip") val tip: Price,
    @SerializedName("discount") val discount: Price,
    @SerializedName("was_rated") val wasRated: Boolean
): Parcelable

@Parcelize
data class OrderItem(
    @SerializedName("id") val id: Long,
    @SerializedName("dish") val dish: Dish,
    @SerializedName("quantity") var quantity: Int,
    @SerializedName("removed_ingredients") var removedIndredients: ArrayList<Ingredient>,
    @SerializedName("price") val price: Price,
    @SerializedName("notes") var notes: String?
): Parcelable