package com.bupp.wood_spoon_eaters.features.upsale.upsale

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.domain.FeatureFlagFreeDeliveryUseCase
import com.bupp.wood_spoon_eaters.domain.comon.execute
import com.bupp.wood_spoon_eaters.features.free_delivery.FreeDeliveryState
import com.bupp.wood_spoon_eaters.features.free_delivery.mapOrderToFreeDeliveryState
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.*
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository
import com.bupp.wood_spoon_eaters.repositories.getCurrentFreeDeliveryThreshold
import com.eatwoodspoon.analytics.events.FreeDeliveryEvent
import com.eatwoodspoon.analytics.events.UpsaleEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UpSaleState(
    val upSaleItems: List<UpsaleAdapterItem>? = emptyList(),
    @StringRes val buttonStringRes: Int = R.string.upsale_button_no_items_added_title
)

class UpSaleViewModel(
    val cartManager: CartManager,
    private val flowEventsManager: FlowEventsManager,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker,
) : ViewModel() {
    private val currentCookingSlot = cartManager.getCurrentCookingSlot()

    private val _state = MutableStateFlow(UpSaleState())
    val state: StateFlow<UpSaleState> = _state

    val currentOrder = cartManager.getCurrentOrderData()

    init {
        logPageEvent()
        fetchUpSaleItems()
    }

    private fun fetchUpSaleItems() {
        val upSaleItems = cartManager.getUpSaleItems()?.items?.map { UpsaleAdapterItem(menuItem = it) }
        _state.update {
            it.copy(upSaleItems = upSaleItems)
        }
    }


    fun addDishToCart(menuItem: MenuItem) {
        val dishId = menuItem.dishId ?: -1
        currentCookingSlot?.let { currentCookingSlot ->
            viewModelScope.launch {
                cartManager.updateCurCookingSlotId(currentCookingSlot.id)
                cartManager.addOrUpdateCart(1, dishId, null)
            }
        }
        logSwipeAddDishEvent(menuItem)
        updateButtonTitle(R.string.upsale_button_items_added_title)
    }

    fun removeOrderItemsByDishId(menuItem: MenuItem) {
        val dishId = menuItem.dishId ?: -1
        viewModelScope.launch {
            cartManager.removeOrderItems(dishId)
        }
        logSwipeRemoveDishEvent(menuItem)
        if (_state.value.upSaleItems?.any { it.cartQuantity > 0 } == false) {
            updateButtonTitle(R.string.upsale_button_no_items_added_title)
        }
    }

    private fun updateButtonTitle(resId: Int) {
        _state.update {
            it.copy(buttonStringRes = resId)
        }
    }

    fun logPageEvent() {
        flowEventsManager.trackPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_UPSALE)
    }

    private fun logSwipeAddDishEvent(item: MenuItem) {
        eatersAnalyticsTracker.reportEvent(
            UpsaleEvent.SwipeAddDishEvent(
                item.dish?.id?.toInt() ?: -1,
                item.dish?.name ?: "",
                item.dish?.price?.value?.toFloat(),
                false
            )
        )
    }

    private fun logSwipeRemoveDishEvent(item: MenuItem) {
        eatersAnalyticsTracker.reportEvent(
            UpsaleEvent.SwipeRemoveDishEvent(
                item.dish?.id?.toInt() ?: -1,
                item.dish?.name ?: "",
                item.dish?.price?.value?.toFloat(),
                false
            )
        )
    }

    fun isUpSaleItemsSelected(): Boolean {
        if (_state.value.upSaleItems?.any { it.cartQuantity > 0 } == true){
            return true
        }
        return false
    }

    fun updateQuantity(order: Order?){
        val orderItems = order?.orderItems
        val upsaleItem = _state.value.upSaleItems?.find { upsaleItem -> orderItems?.any { it.dish.id == upsaleItem.menuItem?.dishId } == true }
        upsaleItem?.cartQuantity = orderItems?.filter { it.dish.id == upsaleItem?.menuItem?.dishId }?.size ?: 0
    }


}