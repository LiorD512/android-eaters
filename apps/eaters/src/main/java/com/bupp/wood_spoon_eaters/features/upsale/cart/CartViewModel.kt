package com.bupp.wood_spoon_eaters.features.upsale.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.domain.FeatureFlagFreeDeliveryUseCase
import com.bupp.wood_spoon_eaters.domain.comon.execute
import com.bupp.wood_spoon_eaters.features.free_delivery.mapOrderToFreeDeliveryState
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.*
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository
import com.bupp.wood_spoon_eaters.repositories.EatersFeatureFlags
import com.bupp.wood_spoon_eaters.repositories.featureFlag
import com.bupp.wood_spoon_eaters.repositories.getCurrentFreeDeliveryThreshold
import com.eatwoodspoon.analytics.events.FreeDeliveryEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class CartData(
    val restaurantName: String? = null,
    val items: List<CartBaseAdapterItem>?
)

data class CartState(
    val cartData: CartData? = null
)

class CartViewModel(
    val cartManager: CartManager,
    val eaterDataManager: EaterDataManager,
    private val flowEventsManager: FlowEventsManager,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker,
    appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: SharedFlow<CartState> = _state

    val currentOrderData = cartManager.getCurrentOrderData()
    private val currentCookingSlot = cartManager.getCurrentCookingSlot()

    private val shouldShowSubtotal =
        appSettingsRepository.featureFlag(EatersFeatureFlags.ShowCartSubtotal) ?: false

    init {
        logPageEvent()
    }

     fun fetchCartData() {
        val list = mutableListOf<CartBaseAdapterItem>()
        val orderItems = currentOrderData.value?.orderItems
        orderItems?.forEach {
            val customCartItem = CustomOrderItem(
                orderItem = it,
                cookingSlot = currentCookingSlot
            )
            list.add(CartAdapterItem(customOrderItem = customCartItem))
        }

        if (shouldShowSubtotal) {
            val subTotal = currentOrderData.value?.subtotal?.formatedValue
            subTotal?.let {
                list.add(CartAdapterSubTotalItem(it))
            }
        }

        val restaurantName = currentOrderData.value?.restaurant?.restaurantName

        _state.update {
            it.copy(cartData = CartData(restaurantName, list))
        }
    }

    fun updateDishInCart(customOrderItem: CustomOrderItem) {
        val dishId = customOrderItem.orderItem.dish.id
        val note = customOrderItem.orderItem.notes
        val orderId = customOrderItem.orderItem.id
        val currentQuantity = customOrderItem.orderItem.quantity
        viewModelScope.launch {
            cartManager.updateDishInExistingCart(currentQuantity.plus(1), note, dishId, orderId)
        }
        logSwipeDishInCart(Constants.EVENT_SWIPE_ADD_DISH_IN_CART, customOrderItem)
    }

    fun logPageEvent() {
        flowEventsManager.trackPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_CART)
    }

    fun logSwipeDishInCart(eventName: String, item: CustomOrderItem) {
        eatersAnalyticsTracker.logEvent(eventName, getSwipeDishData(item))
    }

    private fun getSwipeDishData(item: CustomOrderItem): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["dish_name"] = item.orderItem.dish.name
        data["dish_id"] = item.orderItem.dish.id.toString()
        data["dish_price"] = item.orderItem.price.formatedValue.toString()
        data["dish_quantity"] = item.orderItem.quantity.toString()
        return data
    }

    fun removeSingleOrderItemId(customOrderItem: CustomOrderItem) {
        val orderItemId = customOrderItem.orderItem.id
        viewModelScope.launch {
            cartManager.removeOrderItems(orderItemId, true)
        }
        logSwipeDishInCart(
            Constants.EVENT_SWIPE_REMOVE_DISH_IN_CART,
            customOrderItem
        )
    }
}