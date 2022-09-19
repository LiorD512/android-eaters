package com.bupp.wood_spoon_eaters.features.free_delivery

import com.bupp.wood_spoon_eaters.domain.comon.execute
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.Price


data class FreeDeliveryState(
    val subtotal: Price?,
    val untilFreeDelivery: Price?,
    val currentThreshold: Price?,
    val showAddMoreItemsBtn: Boolean
)

fun Order?.mapOrderToFreeDeliveryState(
    currentThreshold: Price?,
    showFreeDelivery: Boolean,
    showAddMoreItemsBtn: Boolean
): FreeDeliveryState? {
    return if (this == null) {
        null
    } else {
        if (showFreeDelivery && untilFreeDelivery != null && currentThreshold != null) {
            FreeDeliveryState(
                this.subtotal,
                this.untilFreeDelivery,
                currentThreshold,
                showAddMoreItemsBtn
            )
        } else {
            null
        }
    }
}
