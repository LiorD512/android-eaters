package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSections
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionsViewType
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapterItem
import kotlinx.parcelize.Parcelize

//data class UpSaleAdapterItem(
//    override var quantity: Int = 0,
//    override val dish: Dish,
//    override val isSwipeable: Boolean = true
//): SwipeableAdapterItem()


sealed class CartBaseAdapterItem(
    var type: CartAdapterViewType?
) : SwipeableAdapterItem()

enum class CartAdapterViewType {
    UPSALE_DISH,
    CART_DISH,
    SUB_TOTAL
}

data class UpsaleAdapterItem(
    override var cartQuantity: Int = 0,
    override val menuItem: MenuItem? = null,
    override val isSwipeable: Boolean = true
): CartBaseAdapterItem(CartAdapterViewType.UPSALE_DISH)

data class CartAdapterItem(
    val customCartItem: CustomCartItem,
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

data class CustomCartItem(
    val dishName: String,
    val quantity: Int,
    val price: String,
    val note: String?
)
