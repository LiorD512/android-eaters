package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

data class OrderRequest(
    var cookId: Long? = null,
    @SerializedName("cooking_slot_id") var cookingSlotId: Long? = null,
    @SerializedName("deliver_at") var deliveryAt: String? = null,
    @SerializedName("delivery_address") var deliveryAddress: Address? = null,
    @SerializedName("delivery_address_id") var deliveryAddressId: Long? = null,
    @SerializedName("order_items") var orderItemRequests: ArrayList<OrderItemRequest>? = null,
    @SerializedName("tip_percentage") var tipPercentage: Float? = null,
    @SerializedName("tip") var tip: Int? = null,
    @SerializedName("disposable_tableware") var addUtensils: Boolean? = null,
    @SerializedName("shipping_service") var shippingService: String? = null,
//    @SerializedName("recurring_order") var recurringOrder: Boolean? = null,
    @SerializedName("tip_amount") var tipAmount: String? = null,
    @SerializedName("promo_code") var promoCode: String? = null
)

data class OrderItemRequest(
    @SerializedName("id") var id: Long? = null,
    @SerializedName("dish_id") val dishId: Long? = null,
    @SerializedName("quantity") var quantity: Int? = null,
    @SerializedName("removed_ingredient_ids") var removedIndredientsIds: ArrayList<Long>? = null,
    @SerializedName("notes") var notes: String? = null,
    @SerializedName("_destroy") var _destroy: Boolean? = false
)

@Parcelize
data class Order (
    @SerializedName("id") val id: Long,
    @SerializedName("order_number") val orderNumber: String,
    @SerializedName("deliver_at") val deliverAt: Date,
    @SerializedName("created_at") val created_at: Date,
    @SerializedName("delivery_address") val deliveryAddress: Address,
    @SerializedName("estimated_delivery_time") val estDeliveryTime: Date?,
    @SerializedName("eta_text") val estDeliveryTimeText: String?,
    @SerializedName("status") val status: String,
    @SerializedName("courier") val courier: Courier?,
    @SerializedName("promo_code") val promoCode: String = "",
    @SerializedName("status_updated_at") val statusUpdatedAt: Date,
    @SerializedName("delivery_status") val deliveryStatus: String,
    @SerializedName("preparation_status") val preparationStatus: String,
    @SerializedName("delivery_status_updated_at") val deliveryStatusUpdatedAt: Date,
    @SerializedName("tip_percentage") val tipPercentage: Int,
    @SerializedName("notes") val notes: String,
    @SerializedName("total") val total: Price,
    @SerializedName("total_before_tip") val totalBeforeTip: Price,
    @SerializedName("cook") val cook: Cook,
    @SerializedName("cooking_slot") val cookingSlot: CookingSlot,
    @SerializedName("order_items") val orderItems: ArrayList<OrderItem>,
    @SerializedName("subtotal") val subtotal: Price,
    @SerializedName("min_order_fee") val minPrice: Price?,
    @SerializedName("tax") val tax: Price,
    @SerializedName("service_fee") val serviceFee: Price,
    @SerializedName("delivery_fee") val deliveryFee: Price,
    @SerializedName("tip") val tip: Price,
    @SerializedName("discount") val discount: Price,
    @SerializedName("was_rated") val wasRated: Boolean,
    @SerializedName("nationwide_shipping") val isNationwide: Boolean
): Parcelable

@Parcelize
data class OrderItem(
    @SerializedName("id") val id: Long,
    @SerializedName("dish") val dish: Dish,
    @SerializedName("quantity") var quantity: Int,
    @SerializedName("matching_menu") var menuItem: MenuItem?,
    @SerializedName("removed_ingredients") var removedIndredients: ArrayList<Ingredient>,
    @SerializedName("price") val price: Price,
    @SerializedName("notes") var notes: String?
): Parcelable

@Parcelize
data class ShippingMethod(
    val code: String,
    val name: String,
    val fee: Price,
    val description: String
): Parcelable

@Parcelize
data class Courier(
    val id: Long,
    val first_name: String?,
    val last_name: String?,
    val thumbnail: String?,
    val phone_number: String?,
    val email: String?,
    val transport_type: String?,
    val lat: Double?,
    val lng: Double?
): Parcelable