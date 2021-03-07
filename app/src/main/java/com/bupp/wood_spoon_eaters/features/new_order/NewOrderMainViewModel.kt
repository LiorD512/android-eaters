package com.bupp.wood_spoon_eaters.features.new_order

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.new_order.service.EphemeralKeyProvider
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.features.main.feed.FeedViewModel
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.repositories.FeedRepository
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.views.CartBottomBar
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class NewOrderMainViewModel(
    private val feedRepository: FeedRepository,
    private val cartManager: CartManager,
    private val orderManager: OrderManager,
    private val eaterDataManager: EaterDataManager,
    private val deliveryTimeManager: DeliveryTimeManager,
    private val paymentManager: PaymentManager,
    private val eventsManager: EventsManager
) : ViewModel(),
    EphemeralKeyProvider.EphemeralKeyProviderListener {


    var menuItemId: Long? = null
    val progressData = ProgressData()
    val wsErrorEvent = MutableLiveData<List<WSError>>()

    val dishInfoEvent = MutableLiveData<FullDish>()
    val dishCookEvent = MutableLiveData<Cook>()
    val clearCartEvent = SingleLiveEvent<Boolean>()
    val validationError = SingleLiveEvent<OrderValidationErrorType>()

    val deliveryTimeLiveData = cartManager.onDishChangeEvent()

    val getReviewsEvent: SingleLiveEvent<Review?> = SingleLiveEvent()

    val additionalDishesEvent = MutableLiveData<AdditionalDishesEvent>()

    data class AdditionalDishesEvent(val orderItems: List<OrderItem>?, val additionalDishes: List<Dish>?)

    val cartBottomBarTypeEvent = MutableLiveData<CartBottomBarTypeEvent>()

    data class CartBottomBarTypeEvent(val type: CartBottomBar.BottomBarTypes, val price: Double?, val itemCount: Int? = null, val totalPrice: Double? = null)

    val startNewCartEvent = SingleLiveEvent<NewCartDialog>()

    data class NewCartDialog(
        val inCartCookName: String,
        val currentShowingCookName: String
    )

    val mainActionEvent = MutableLiveData<NewOrderActionEvent>()

    enum class NewOrderActionEvent {
        ADD_CURRENT_DISH_TO_CART,
        SHOW_ADDITIONAL_DISH_DIALOG,

    }


    fun initNewOrderActivity(intent: Intent?) {
        //get full dish and check order status prior to dish
        intent?.let {
            menuItemId = it.getLongExtra(Constants.NEW_ORDER_MENU_ITEM_ID, Constants.NOTHING.toLong())
            if (menuItemId != Constants.NOTHING.toLong()) {
                getFullDish()
            }

            isCheckout = it.getBooleanExtra(Constants.NEW_ORDER_IS_CHECKOUT, false)
            if (isCheckout) {
                handleNavigation(NewOrderScreen.CHECKOUT)
            }
        }
    }

    private fun getFullDish() {
        menuItemId?.let {
            viewModelScope.launch {
                progressData.startProgress()
                val getFullDishResult = cartManager.getFullDish(it)
                getFullDishResult?.let {
                    dishInfoEvent.postValue(it.fullDish)
                    dishCookEvent.postValue(it.fullDish.cook)

                    if(!cartManager.isInCheckout()){
                        checkCartStatusAndUpdateUi()
                    }
                }
                progressData.endProgress()
            }
        }
    }

    fun refreshFullDish(menuItemId: Long? = null) {
        menuItemId?.let{
            this.menuItemId = menuItemId
        }
        getFullDish()
    }

    fun checkCartStatusAndUpdateUi() {
        val cartStatus = cartManager.getCartStatus()
        when (cartStatus.type) {
            CartManager.CartStatusEventType.CART_IS_EMPTY -> {
//                singleDishStatusBar.handleBottomBar(showCheckout = false)
                updateCartBottomBarByType(
                    type = CartBottomBar.BottomBarTypes.ADD_TO_CART,
                    price = cartStatus.currentOrderItemPrice,
                    itemCount = cartStatus.currentOrderItemCounter,
                )
            }
            CartManager.CartStatusEventType.SHOWING_CURRENT_CART -> {
                updateCartBottomBarByType(
                    type = CartBottomBar.BottomBarTypes.ADD_TO_CART_OR_CHECKOUT,
                    price = cartStatus.currentOrderItemPrice,
                    itemCount = cartStatus.currentOrderItemCounter,
                    totalPrice = cartStatus.inCartTotalPrice
                )
            }
        }
    }


    fun updateCartBottomBarByType(type: CartBottomBar.BottomBarTypes, price: Double?, itemCount: Int? = null, totalPrice: Double? = null) {
        cartBottomBarTypeEvent.postValue(CartBottomBarTypeEvent(type, price, itemCount, totalPrice))
    }

    fun onCartBottomBarClick(type: CartBottomBar.BottomBarTypes) {
        when (type) {
            CartBottomBar.BottomBarTypes.UPDATE_COUNTER, CartBottomBar.BottomBarTypes.ADD_TO_CART, CartBottomBar.BottomBarTypes.ADD_TO_CART_OR_CHECKOUT -> {
                handleAddToCartClick()
            }
            CartBottomBar.BottomBarTypes.PROCEED_TO_CHECKOUT -> {

            }
            CartBottomBar.BottomBarTypes.PLACE_AN_ORDER -> {
                if (validateOrder()) {
                    finalizeOrder()
                }
            }
        }
    }

    private fun finalizeOrder() {
        viewModelScope.launch {
            val paymentMethodId = paymentManager.getStripeCurrentPaymentMethod()?.id
            progressData.startProgress()
            val result = cartManager.finalizeOrder(paymentMethodId)
            when (result?.type) {
                OrderRepository.OrderRepoStatus.FINALIZE_ORDER_SUCCESS -> {
                    Log.d(TAG, "finalizeOrder - success")
                    cartManager.clearCart()
                    handleNavigation(NewOrderScreen.FINISH_ACTIVITY_AFTER_PURCHASE)
                }
                OrderRepository.OrderRepoStatus.FINALIZE_ORDER_FAILED -> {
                    Log.d(TAG, "finalizeOrder - failed")
                }
                OrderRepository.OrderRepoStatus.WS_ERROR -> {
                    Log.d(TAG, "finalizeOrder - ws error")

                }
            }
            progressData.endProgress()
        }
    }

    private fun handleAddToCartClick() {
        //check if user set an address and add to cart
        val newCartData = cartManager.shouldForceClearCart()
        if (newCartData != null) {
            startNewCartEvent.postValue(newCartData.inCartCookName?.let { newCartData.currentShowingCookName?.let { it1 -> NewCartDialog(it, it1) } })
        } else if (eaterDataManager.hasUserSetAnAddress()) {
            addToCart()
        } else {
            navigationEvent.postValue(NewOrderNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY)
        }
    }

    private fun addToCart() {
        Log.d(TAG, "addToCart")
        progressData.startProgress()
        cartManager.addCurrentOrderItemToCart()

        viewModelScope.launch {
            val result = cartManager.postNewOrUpdateCart()
            result?.let {
                when (it.type) {
                    OrderRepository.OrderRepoStatus.POST_ORDER_SUCCESS -> {
                        if (cartManager.shouldShowAdditionalDishesDialog()) {
                            mainActionEvent.postValue(NewOrderActionEvent.SHOW_ADDITIONAL_DISH_DIALOG)
                        } else {
                            handleNavigation(NewOrderScreen.LOCK_SINGLE_DISH_COOK)
                        }
//                        getFullDish()
//                        handleAndShowBottomBar()
                        showProceedToCartBottomBar()
                    }
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {
                        handleNavigation(NewOrderScreen.SINGLE_DISH_COOK)
                        getFullDish()
                    }
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {
                    }
                    OrderRepository.OrderRepoStatus.POST_ORDER_FAILED -> {
                    }
                    OrderRepository.OrderRepoStatus.WS_ERROR -> {
                        result.wsError?.let {
                            wsErrorEvent.postValue(it)
                        }
                    }
                    else -> {
                    }
                }
                progressData.endProgress()
            }
        }
    }

    fun updateOrderItem(orderItem: OrderItem) {
        Log.d(TAG, "updateOrderItem -> orderItem: $orderItem")
        viewModelScope.launch {
            progressData.startProgress()
            val result = cartManager.updateInCartOrderItem(orderItem)
            result?.let {
                when (result.type) {
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {
                        initAdditionalDishes()
                    }
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {
                    }
                    OrderRepository.OrderRepoStatus.WS_ERROR -> {
                        result.wsError?.let {
                            wsErrorEvent.postValue(it)
                        }
                    }
                    else -> {
                    }
                }
            }
            progressData.endProgress()
        }
    }

    fun initAdditionalDishes() {
        Log.d(TAG, "initAdditionalDishes")
        val orderItems = cartManager.getCurrentOrderItems()
        val additionalDishes = cartManager.getAdditionalDishes()
        additionalDishesEvent.postValue(AdditionalDishesEvent(orderItems, additionalDishes))
    }

    fun addNewDishToCart(dishId: Long, quantity: Int) {
        val newOrderItem = OrderItemRequest(dishId = dishId, quantity = quantity)
        Log.d(TAG, "addNewDishToCart: $newOrderItem")
        viewModelScope.launch {
            val result = cartManager.addNewOrderItemToCart(newOrderItem)
            progressData.startProgress()
            result?.let {
                when (result.type) {
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {
                        initAdditionalDishes()
                    }
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {
                    }
                    OrderRepository.OrderRepoStatus.WS_ERROR -> {
                        result.wsError?.let {
                            wsErrorEvent.postValue(it)
                        }
                    }
                    else -> {
                    }
                }
            }
            progressData.endProgress()
        }
    }


    fun getDishReview(cookId: Long?) {
        val cookId = cookId ?: cartManager.currentShowingDish?.cook?.id
        cookId?.let{
            progressData.startProgress()
            viewModelScope.launch {
                val result = feedRepository.getCookReview(it)
    //                progressData.endProgress()
                when (result.type) {
                    FeedRepository.FeedRepoStatus.SERVER_ERROR -> {
                        Log.d(FeedViewModel.TAG, "NetworkError")
    //                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                    FeedRepository.FeedRepoStatus.SOMETHING_WENT_WRONG -> {
                        Log.d(FeedViewModel.TAG, "GenericError")
    //                        errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
                    }
                    FeedRepository.FeedRepoStatus.SUCCESS -> {
                        Log.d(FeedViewModel.TAG, "Success")
                        getReviewsEvent.postValue(result.review!!)
                    }
                    else -> {
                        Log.d(FeedViewModel.TAG, "NetworkError")
    //                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                }
            }
            progressData.endProgress()
        }
    }


    enum class OrderValidationErrorType {
        SHIPPING_METHOD_MISSING,
        PAYMENT_METHOD_MISSING
    }

    private fun validateOrder(): Boolean {
        if (cartManager.checkShippingMethodValidation()) {
            validationError.postValue(OrderValidationErrorType.SHIPPING_METHOD_MISSING)
            return false
        }
        val paymentMethod = paymentManager.getStripeCurrentPaymentMethod()?.id
        if (paymentMethod == null) {
            handleNavigation(NewOrderScreen.START_PAYMENT_METHOD_ACTIVITY)
//            validationError.postValue(OrderValidationErrorType.PAYMENT_METHOD_MISSING)
            return false
        }
        return true
    }


    //Navigation
    private var currentShowingScreen: NewOrderScreen = NewOrderScreen.SINGLE_DISH_INFO

    enum class NewOrderScreen {
        ADDITIONAL_DISHES,
        SINGLE_DISH_INFO,
        SINGLE_DISH_COOK,
        SINGLE_DISH_INGR,
        FINISH_ACTIVITY,
        FINISH_ACTIVITY_AFTER_PURCHASE,
        START_PAYMENT_METHOD_ACTIVITY,
        LOCATION_AND_ADDRESS_ACTIVITY,
        LOCK_SINGLE_DISH_COOK,
        ADDITIONAL_DISHES_DISMISS,
        BACK_PRESS,
        PROMO_CODE,
        CHECKOUT,
    }

    val navigationEvent = MutableLiveData<NewOrderNavigationEvent>()

    enum class NewOrderNavigationEvent {
        MAIN_TO_CHECKOUT,
        BACK_TO_PREVIOUS,
        BACK_FROM_CHECKOUT,
        CHECKOUT_TO_PROMO_CODE,
        SHOW_ADDRESS_MISSING_DIALOG,
        REDIRECT_TO_COOK_PROFILE,
        REDIRECT_TO_DISH_INFO,
        LOCK_SINGLE_DISH_COOK,
        FINISH_ACTIVITY,
        FINISH_ACTIVITY_AFTER_PURCHASE,
        START_PAYMENT_METHOD_ACTIVITY,
        REDIRECT_TO_SELECT_PROMO_CODE,
        START_LOCATION_AND_ADDRESS_ACTIVITY
    }

    fun updateCurrentScreen(screen: NewOrderScreen) {
        this.currentShowingScreen = screen
    }

    fun handleNavigation(nextShowingScreen: NewOrderScreen) {
        when (nextShowingScreen) {
            NewOrderScreen.SINGLE_DISH_INFO -> {
                navigationEvent.postValue(NewOrderNavigationEvent.REDIRECT_TO_DISH_INFO)
            }
            NewOrderScreen.SINGLE_DISH_COOK -> {
                navigationEvent.postValue(NewOrderNavigationEvent.REDIRECT_TO_COOK_PROFILE)
            }
            NewOrderScreen.CHECKOUT -> {
                Log.d(TAG, "handleNavigation: MAIN_TO_CHECKOUT")
                navigationEvent.postValue(NewOrderNavigationEvent.MAIN_TO_CHECKOUT)
                cartManager.setIsInCheckout(true)
            }
            NewOrderScreen.PROMO_CODE -> {
                Log.d(TAG, "handleNavigation: PROMO_CODE")
                navigationEvent.postValue(NewOrderNavigationEvent.REDIRECT_TO_SELECT_PROMO_CODE)
            }
            NewOrderScreen.LOCK_SINGLE_DISH_COOK -> {
                Log.d(TAG, "handleNavigation: LOCK_SINGLE_DISH_COOK")
                navigationEvent.postValue(NewOrderNavigationEvent.LOCK_SINGLE_DISH_COOK)
            }
            NewOrderScreen.LOCATION_AND_ADDRESS_ACTIVITY -> {
                Log.d(TAG, "handleNavigation: LOCATION_AND_ADDRESS_ACTIVITY")
                navigationEvent.postValue(NewOrderNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY)
            }
            NewOrderScreen.START_PAYMENT_METHOD_ACTIVITY -> {
                Log.d(TAG, "handleNavigation: START_PAYMENT_METHOD_ACTIVITY")
                navigationEvent.postValue(NewOrderNavigationEvent.START_PAYMENT_METHOD_ACTIVITY)
            }
            NewOrderScreen.FINISH_ACTIVITY -> {
                Log.d(TAG, "handleNavigation: FINISH_ACTIVITY")
                navigationEvent.postValue(NewOrderNavigationEvent.FINISH_ACTIVITY)
            }
            NewOrderScreen.FINISH_ACTIVITY_AFTER_PURCHASE -> {
                Log.d(TAG, "handleNavigation: FINISH_ACTIVITY_AFTER_PURCHASE")
                navigationEvent.postValue(NewOrderNavigationEvent.FINISH_ACTIVITY_AFTER_PURCHASE)
            }
        }
        currentShowingScreen = nextShowingScreen
    }


    // Checkout section
    val orderData = cartManager.getCurrentOrderData()

    fun onLocationChanged() {
        viewModelScope.launch {
            val result = cartManager.refreshOrderParams()
            result?.let {
                when (result.type) {
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {
                        initAdditionalDishes()
                    }
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {
                    }
                    OrderRepository.OrderRepoStatus.WS_ERROR -> {
                        cartManager.onLocationInvalid()
                        result.wsError?.let {
                            wsErrorEvent.postValue(it)
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    fun onDeliveryTimeChange() {
        viewModelScope.launch {
            val result = cartManager.refreshOrderParams()
            result?.let {
                when (result.type) {
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {
//                        do nothing.. live data updates it self.
                    }
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {
                    }
                    OrderRepository.OrderRepoStatus.WS_ERROR -> {
                        cartManager.onDeliveryTimeInvalid()
                        result.wsError?.let {
                            wsErrorEvent.postValue(it)
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }


    var isCheckout: Boolean = false

    fun updateDeliveryTime(time: Date) {
//        progressData.startProgress()
        deliveryTimeManager.setNewDeliveryTime(time)
//        orderManager.updateOrderRequest(deliveryAt = eaterDataManager.getLastOrderTimeParam())
        orderData.postValue(orderData.value)
        val orderRequest = OrderRequest()
        orderRequest.deliveryAt = deliveryTimeManager.getDeliveryTimestamp()
//        postUpdateOrder(orderRequest)
    }


    val ephemeralKeyProvider: SingleLiveEvent<EphemeralKeyProviderEvent> = SingleLiveEvent()

    data class EphemeralKeyProviderEvent(val isSuccess: Boolean = false)

    override fun onEphemeralKeyProviderError() {
        ephemeralKeyProvider.postValue(EphemeralKeyProviderEvent(false))
    }

    fun getLastOrderDetails() {
        orderManager.curOrderResponse?.let {
            orderData.postValue(orderManager.curOrderResponse)
        }
    }

    private fun showProceedToCartBottomBar() {
        updateCartBottomBarByType(type = CartBottomBar.BottomBarTypes.PROCEED_TO_CHECKOUT, price = cartManager.calcTotalDishesPrice())
    }

    fun onNewOrderFinish() {
        cartManager.onNewOrderFinish()
    }

    fun clearCart() {
        cartManager.clearCart()
        getFullDish()
    }


    fun showClearCartDialog() {
        clearCartEvent.postValue(true)
    }


    fun onCooksProfileDishClick(menuItemId: Long?) {
        menuItemId?.let {
            refreshFullDish(menuItemId)
            navigationEvent.postValue(NewOrderNavigationEvent.REDIRECT_TO_DISH_INFO)
        }
    }

    fun checkIfFutureOrder(fullDish: FullDish) {
        if(fullDish.menuItem?.orderAt == null){
            //Dish is offered today.
        }else{
            fullDish.menuItem?.orderAt?.let{
                //Dish is offered in the future.
                if(Date().after(it)){

                }else{
                    //order stating in the future. needs to update order delivery time to "orderAt"
                    deliveryTimeManager.setTemporaryDeliveryTimeDate(it)
                }
            }

        }
    }



    companion object {
        const val TAG = "wowNewOrderVM"
    }


}
