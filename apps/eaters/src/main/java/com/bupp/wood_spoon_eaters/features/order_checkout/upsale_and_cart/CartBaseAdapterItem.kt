package com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.model.CookingSlot
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapterItem
import kotlinx.parcelize.Parcelize


sealed class CartBaseAdapterItem(
    var type: CartAdapterViewType?
) : SwipeableAdapterItem()

enum class CartAdapterViewType {
    CART_DISH,
    SUB_TOTAL
}

data class UpsaleAdapterItem(
    override var cartQuantity: Int = 0,
    override val menuItem: MenuItem? = null,
    override val isSwipeable: Boolean = true
): SwipeableAdapterItem()

data class CartAdapterItem(
    val customOrderItem: CustomOrderItem,
    override var cartQuantity: Int = 0,
    override val menuItem: MenuItem? = null,
    override val isSwipeable: Boolean = true
): CartBaseAdapterItem(CartAdapterViewType.CART_DISH)

data class CartAdapterSubTotalItem(
    val subTotal: String,
    override val menuItem: MenuItem? = null,
    override var cartQuantity: Int = 0,
    override val isSwipeable: Boolean = false
): CartBaseAdapterItem(CartAdapterViewType.SUB_TOTAL)

@Parcelize
data class CustomOrderItem(
    val orderItem: OrderItem,
    val cookingSlot: CookingSlot?
): Parcelable