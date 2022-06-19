package com.bupp.wood_spoon_eaters.features.order_checkout.gift

import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderGiftRequest

class GiftStateMapper {

    fun mapStateToOrderGiftRequest(state: GiftViewModelState) = with(state) {
        OrderGiftRequest(
            isGift = true,
            recipientFirstName = recipientFirstName,
            recipientLastName = recipientLastName,
            recipientPhoneNumber = recipientPhoneNumber,
            notifyRecipient = notifyRecipient,
            recipientEmail = if (notifyRecipient) recipientEmail else null,
        )
    }

    fun mapOrderToState(order: Order) = with(order) {
        if (isGift == true) GiftViewModelState(
            recipientFirstName = recipientFirstName,
            recipientLastName = recipientLastName,
            recipientPhoneNumber = recipientPhoneNumber,
            notifyRecipient = !this.recipientEmail.isNullOrBlank(),
            recipientEmail = this.recipientEmail
        ) else GiftViewModelState()
    }
}
