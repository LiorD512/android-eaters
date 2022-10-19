package com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart

import androidx.lifecycle.*
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.domain.FeatureFlagFreeDeliveryUseCase
import com.bupp.wood_spoon_eaters.domain.comon.execute
import com.bupp.wood_spoon_eaters.features.free_delivery.mapOrderToFreeDeliveryState
import com.bupp.wood_spoon_eaters.features.upsale.data_source.repository.UpSaleRepository
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.repositories.*
import com.eatwoodspoon.analytics.events.FreeDeliveryEvent
import com.eatwoodspoon.analytics.events.UpsaleEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

sealed class UpSaleAndCartEvents {
    object OpenUpSaleDialog : UpSaleAndCartEvents()
    object GoToCheckout : UpSaleAndCartEvents()
    object GoToSelectAddress : UpSaleAndCartEvents()
}

enum class UpSaleAndCartScreenName{
    UPSALE_SCREEN,
    CART_SCREEN
}

data class UpSaleAndCartState(
    val upSaleAndCartScreenName: UpSaleAndCartScreenName = UpSaleAndCartScreenName.CART_SCREEN,
    val isUpSaleItemsSelected: Boolean = false
)

class UpSaleNCartViewModel(
    private val featureFlagFreeDeliveryUseCase: FeatureFlagFreeDeliveryUseCase,
    val cartManager: CartManager,
    private val upSaleRepository: UpSaleRepository,
    val appSettingsRepository: AppSettingsRepository,
    val eaterDataManager: EaterDataManager,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker
) : ViewModel() {
    val currentOrderData = cartManager.getCurrentOrderData()
    val freeDeliveryData =
        cartManager.getCurrentOrderData().map {
            it.mapOrderToFreeDeliveryState(
                appSettingsRepository.getCurrentFreeDeliveryThreshold(),
                featureFlagFreeDeliveryUseCase.execute(),
                showAddMoreItemsBtn = false
            )
        }


    private val _events = MutableSharedFlow<UpSaleAndCartEvents>()
    val events: SharedFlow<UpSaleAndCartEvents> = _events

    private val _state = MutableStateFlow(UpSaleAndCartState())
    val state: StateFlow<UpSaleAndCartState> = _state

    fun onCheckoutClick() {
        viewModelScope.launch {
            val upSaleItems = cartManager.getUpSaleItems()
            if (!upSaleItems?.items.isNullOrEmpty() && !upSaleRepository.getOrderIdsOfShownUpSaleScreen().contains(cartManager.getCurOrderId())) {
                _events.emit(UpSaleAndCartEvents.OpenUpSaleDialog)
                upSaleRepository.setUpSaleItemShowedForThisOrder(cartManager.getCurOrderId())
            } else {
                if (eaterDataManager.hasUserSetAnAddress()) {
                    _events.emit(UpSaleAndCartEvents.GoToCheckout)
                } else {
                    _events.emit(UpSaleAndCartEvents.GoToSelectAddress)
                }
            }
        }
    }

    fun updateAddressAndProceedToCheckout() {
        viewModelScope.launch {
            val result = cartManager.updateOrderDeliveryAddressParam()
            if (result?.type == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS) {
                viewModelScope.launch {
                    _events.emit(UpSaleAndCartEvents.GoToCheckout)
                }
            }
        }
    }

    fun getRestaurantName(): String?{
        return currentOrderData.value?.restaurant?.restaurantName
    }

    fun setCurrentScreen(upSaleAndCartScreenName: UpSaleAndCartScreenName){
        _state.update {
            it.copy(upSaleAndCartScreenName = upSaleAndCartScreenName)
        }
    }

    fun getCurrentScreenName(): UpSaleAndCartScreenName{
        return _state.value.upSaleAndCartScreenName
    }

    fun reportThresholdAchievedEvent() {
        reportEvent { orderId, screen ->
            FreeDeliveryEvent.ThresholdAchievedEvent(orderId, screen)
        }
    }

    fun reportViewClickedEvent() {
        reportEvent { orderId, screen ->
            FreeDeliveryEvent.ViewClickedEvent(orderId, screen)
        }
    }


    private fun reportEvent(factory: (orderId: Int, screen: String) -> FreeDeliveryEvent) {
        val orderId = cartManager.getCurrentOrderData().value?.id?.toInt() ?: -1
        val screen = when(_state.value.upSaleAndCartScreenName){
            UpSaleAndCartScreenName.UPSALE_SCREEN -> "upsale_page"
            UpSaleAndCartScreenName.CART_SCREEN -> "cart_page"
        }
        eatersAnalyticsTracker.reportEvent(factory.invoke(orderId, screen))
    }

    fun logUpSaleButtonClickedEvents() {
        if (!_state.value.isUpSaleItemsSelected) {
            eatersAnalyticsTracker.reportEvent(UpsaleEvent.ClickedNoThanksEvent())
        } else {
            eatersAnalyticsTracker.reportEvent(UpsaleEvent.ClickProceedToCartEvent())
        }
    }

    fun updateIsUpSaleItemsSelected(isSelected: Boolean){
        _state.update {
            it.copy(isUpSaleItemsSelected = isSelected)
        }
    }
}