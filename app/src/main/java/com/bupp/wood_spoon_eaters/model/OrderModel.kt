package com.bupp.wood_spoon_eaters.model

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("cooking_slot_id") var cookingSlotId: Long? = null,
    @SerializedName("deliver_at") var deliveryAt: String? = null,
    @SerializedName("delivery_address_id") var deliveryAddressId: Long? = null,
    @SerializedName("order_items") var orderItems: ArrayList<OrderItem>? = null,
    @SerializedName("tip_percentage") var tipPercentage: Float? = null,
    @SerializedName("tip") var tip: Int? = null,
    @SerializedName("tip_amount") var tipAmount: String? = null,
    @SerializedName("promo_code_id") var promoCodeId: Long? = null
)

data class OrderItem(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("dish_id") val dishId: Long? = null,
    @SerializedName("quantity") val quantity: Int? = null,
    @SerializedName("removed_ingredient_ids") var removedIndredientsIds: ArrayList<Long>? = null,
    @SerializedName("notes") var notes: String? = null
)