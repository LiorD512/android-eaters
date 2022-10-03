package com.bupp.wood_spoon_eaters.features.upsale

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.domain.FeatureFlagFreeDeliveryUseCase
import com.bupp.wood_spoon_eaters.domain.comon.execute
import com.bupp.wood_spoon_eaters.features.free_delivery.mapOrderToFreeDeliveryState
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.*
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.model.MenuItem
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
    val buttonTitle: String = "No thanks"
)

class UpSaleViewModel(
    val cartManager: CartManager,
    private val flowEventsManager: FlowEventsManager,
    private val featureFlagFreeDeliveryUseCase: FeatureFlagFreeDeliveryUseCase,
    private val appSettingsRepository: AppSettingsRepository,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker,
) : ViewModel() {
    private val currentCookingSlot = cartManager.getCurrentCookingSlot()
    val freeDeliveryData =
        cartManager.getCurrentOrderData().map {
            it.mapOrderToFreeDeliveryState(
                appSettingsRepository.getCurrentFreeDeliveryThreshold(),
                featureFlagFreeDeliveryUseCase.execute(),
                showAddMoreItemsBtn = false
            )
        }

    private val _state = MutableStateFlow(UpSaleState())
    val state: StateFlow<UpSaleState> = _state

    init {
        fetchUpSaleItems()
    }

    private fun fetchUpSaleItems() {
        val list = mutableListOf<UpsaleAdapterItem>()
        val upSaleItems = cartManager.getUpSaleItems()
        upSaleItems?.items?.forEach {
            list.add(UpsaleAdapterItem(menuItem = it))
        }
        _state.update {
            it.copy(upSaleItems = list)
        }

    }

    fun addDishToCart(quantity: Int, dishId: Long, note: String? = null) {
        currentCookingSlot?.let { currentCookingSlot ->
            viewModelScope.launch {
                cartManager.updateCurCookingSlotId(currentCookingSlot.id)
                cartManager.addOrUpdateCart(quantity, dishId, note)
            }
        }
        updateButtonTitle("Continue")
    }

    fun removeOrderItemsByDishId(dishId: Long?) {
        dishId?.let {
            viewModelScope.launch {
                cartManager.removeOrderItems(it)
            }
        }
        if (_state.value.upSaleItems?.any { it.cartQuantity > 0  } == false){
            updateButtonTitle("No thanks")
        }
    }

    private fun updateButtonTitle(title: String){
        _state.update {
            it.copy(buttonTitle = title)
        }
    }

    fun logPageEvent(eventType: FlowEventsManager.FlowEvents) {
        flowEventsManager.trackPageEvent(eventType)
    }

    fun reportThresholdAchievedEvent(){
        reportEvent { orderId, screen ->
            FreeDeliveryEvent.ThresholdAchievedEvent(orderId, screen)
        }
    }

    fun reportViewClickedEvent(){
        reportEvent { orderId, screen ->
            FreeDeliveryEvent.ViewClickedEvent(orderId, screen)
        }
    }

    private fun reportEvent(factory: (orderId: Int, screen: String) -> FreeDeliveryEvent) {
        val orderId = cartManager.getCurrentOrderData().value?.id?.toInt() ?: -1
        val screen = "upsale_page"
        eatersAnalyticsTracker.reportEvent(factory.invoke(orderId, screen))
    }

    fun logSwipeAddDishEvent(item: MenuItem) {
        eatersAnalyticsTracker.reportEvent(UpsaleEvent.SwipeAddDishEvent(
            item.dish?.id?.toInt() ?: -1,
            item.dish?.name ?: "",
            item.dish?.price?.value?.toFloat(),
            false))
    }

    fun logSwipeRemoveDishEvent(item: MenuItem) {
        eatersAnalyticsTracker.reportEvent(UpsaleEvent.SwipeRemoveDishEvent(
            item.dish?.id?.toInt() ?: -1,
            item.dish?.name ?: "",
            item.dish?.price?.value?.toFloat(),
            false))
    }

    fun logUpsaleButtonClickedEvents(){
        if (_state.value.upSaleItems?.any { it.cartQuantity > 0  } == false){
            eatersAnalyticsTracker.reportEvent(UpsaleEvent.ClickedNoThanksEvent())
        }else{
            eatersAnalyticsTracker.reportEvent(UpsaleEvent.ClickProceedToCartEvent())
        }
    }
}