package com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart

import androidx.lifecycle.*
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.domain.FeatureFlagFreeDeliveryUseCase
import com.bupp.wood_spoon_eaters.domain.comon.execute
import com.bupp.wood_spoon_eaters.features.free_delivery.mapOrderToFreeDeliveryState
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.repositories.*
import com.eatwoodspoon.analytics.events.FreeDeliveryEvent
import kotlinx.coroutines.launch

class UpSaleNCartViewModel(
    private val featureFlagFreeDeliveryUseCase: FeatureFlagFreeDeliveryUseCase,
    val cartManager: CartManager,
    val eaterDataManager: EaterDataManager,
    private val flowEventsManager: FlowEventsManager,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker,
    appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    var currentPageState = PageState.CART
    val currentOrderData = cartManager.getCurrentOrderData()
    val currentCookingSlot = cartManager.getCurrentCookingSlot()
    val onDishCartClick = LiveEventData<CustomOrderItem>()
    val onCheckOutClicked = MutableLiveData<Boolean>()
    val freeDeliveryData =
        currentOrderData.map {
            it.mapOrderToFreeDeliveryState(
                appSettingsRepository.getCurrentFreeDeliveryThreshold(),
                featureFlagFreeDeliveryUseCase.execute(),
                showAddMoreItemsBtn = false
            )
        }

    private val shouldShowSubtotal = appSettingsRepository.featureFlag(EatersFeatureFlags.ShowCartSubtotal) ?: false


    enum class PageState {
        UPSALE,
        CART
    }

    enum class NavigationEvent {
        GO_TO_CHECKOUT,
        GO_TO_UP_SALE,
        GO_TO_SELECT_ADDRESS
    }

    val navigationEvent = MutableLiveData<NavigationEvent>()

    fun onCartBtnClick() {
        val shouldShowUpsaleDialog = false
        if (shouldShowUpsaleDialog && currentPageState == PageState.CART) {
            currentPageState = PageState.UPSALE
            navigationEvent.postValue(NavigationEvent.GO_TO_UP_SALE)
        } else {
            if(eaterDataManager.hasUserSetAnAddress()){
                currentPageState = PageState.CART
                navigationEvent.postValue(NavigationEvent.GO_TO_CHECKOUT)
            }else{
                navigationEvent.postValue(NavigationEvent.GO_TO_SELECT_ADDRESS)
            }
        }
    }

    fun onCheckoutClick(){
        onCheckOutClicked.postValue(true)
    }

    fun initData() {
        when (currentPageState) {
            PageState.CART -> {
                upsaleNCartLiveData.postValue(fetchCartData())
            }
            else -> {
                upsaleNCartLiveData.postValue(fetchUpSaleData())
            }
        }
    }


    val upsaleNCartLiveData = MutableLiveData<CartData?>()

    data class CartData(
        val restaurantName: String? = null,
        val items: List<CartBaseAdapterItem>?
    )


    private fun fetchCartData(): CartData? {
        val list = mutableListOf<CartBaseAdapterItem>()
        val orderItems = currentOrderData.value?.orderItems
        if(orderItems.isNullOrEmpty()){
            return null
        }
        orderItems.forEach {
            val customCartItem = CustomOrderItem(
                orderItem = it,
                cookingSlot = currentCookingSlot
            )
            list.add(CartAdapterItem(customOrderItem = customCartItem))
        }

        if(shouldShowSubtotal) {
            val subTotal = currentOrderData.value?.subtotal?.formatedValue
            subTotal?.let {
                list.add(CartAdapterSubTotalItem(it))
            }
        }

        val restaurantName = currentOrderData.value?.restaurant?.restaurantName

        return CartData(restaurantName, list)
    }


    private fun fetchUpSaleData(): CartData {
        val list = mutableListOf<CartBaseAdapterItem>()

        return CartData("Buy more", list)
    }


    fun updateDishInCart(quantity: Int, dishId: Long, note: String? = null, orderItemId: Long) {
        viewModelScope.launch {
            cartManager.updateDishInExistingCart(quantity, note, dishId, orderItemId)
        }
    }

    fun removeOrderItemsByDishId(dishId: Long?) {
        dishId?.let {
            viewModelScope.launch {
                cartManager.removeOrderItems(it)
            }
        }
    }

    fun removeSingleOrderItemId(orderItemId: Long) {
        viewModelScope.launch {
            cartManager.removeOrderItems(orderItemId, true)
        }
    }

    fun onCartItemClicked(customOrderItem: CustomOrderItem) {
        onDishCartClick.postRawValue(customOrderItem)
    }

    fun updateAddressAndProceedToCheckout() {
        viewModelScope.launch {
            val result = cartManager.updateOrderDeliveryAddressParam()
            if(result?.type == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS){
                navigationEvent.postValue(NavigationEvent.GO_TO_CHECKOUT)
            }
        }
    }

    fun logPageEvent(eventType: FlowEventsManager.FlowEvents) {
        flowEventsManager.trackPageEvent(eventType)
    }

    fun logSwipeDishInCart(eventName: String, item: CustomOrderItem) {
        eatersAnalyticsTracker.logEvent(eventName, getSwipeDishData(item))
    }

    fun logEvent(eventName: String) {
        eatersAnalyticsTracker.logEvent(eventName)
    }

    private fun getSwipeDishData(item: CustomOrderItem): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["dish_name"] = item.orderItem.dish.name
        data["dish_id"] = item.orderItem.dish.id.toString()
        data["dish_price"] = item.orderItem.price.formatedValue.toString()
        data["dish_quantity"] = item.orderItem.quantity.toString()
        return data
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
        val orderId = currentOrderData.value?.id?.toInt() ?: -1
        val screen = "cart_page"
        eatersAnalyticsTracker.reportEvent(factory.invoke(orderId, screen))
    }

//    /**
//     * this function is being called when user swiped out all of his
//     * items from the cart - restaurant page need to update item items counter
//     */
//    fun onCartCleared(){
//        viewModelScope.launch {
//            cartManager.currentCookingSlotId?.let{
//                cartManager.updateCurCookingSlot(it)
//            }
//        }
//    }

}