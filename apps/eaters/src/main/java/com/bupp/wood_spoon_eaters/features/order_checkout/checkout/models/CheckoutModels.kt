package com.bupp.wood_spoon_eaters.features.order_checkout.checkout.models

import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.CustomOrderItem
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapterItem

data class CheckoutAdapterItem(
    val customOrderItem: CustomOrderItem,
    override var cartQuantity: Int = 0,
    override val menuItem: MenuItem? = null,
    override val isSwipeable: Boolean = true
): SwipeableAdapterItem()