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
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
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

    val deliveryTimeLiveData = cartManager.deliveryTimeLiveData

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
                    progressData.endProgress()
                }
            }
        }
    }

    fun refreshFullDish(menuItemId: Long?) {
        this.menuItemId = menuItemId
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
                Log.d(TAG, "handleNavigation: MAIN_TO_CHECKOUT")
                navigationEvent.postValue(NewOrderNavigationEvent.REDIRECT_TO_SELECT_PROMO_CODE)
            }
            NewOrderScreen.LOCK_SINGLE_DISH_COOK -> {
                Log.d(TAG, "handleNavigation: MAIN_TO_CHECKOUT")
                navigationEvent.postValue(NewOrderNavigationEvent.LOCK_SINGLE_DISH_COOK)
            }
            NewOrderScreen.LOCATION_AND_ADDRESS_ACTIVITY -> {
                Log.d(TAG, "handleNavigation: MAIN_TO_CHECKOUT")
                navigationEvent.postValue(NewOrderNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY)
            }
            NewOrderScreen.START_PAYMENT_METHOD_ACTIVITY -> {
                Log.d(TAG, "handleNavigation: MAIN_TO_CHECKOUT")
                navigationEvent.postValue(NewOrderNavigationEvent.START_PAYMENT_METHOD_ACTIVITY)
            }
            NewOrderScreen.FINISH_ACTIVITY -> {
                Log.d(TAG, "handleNavigation: MAIN_TO_CHECKOUT")
                navigationEvent.postValue(NewOrderNavigationEvent.FINISH_ACTIVITY)
            }
            NewOrderScreen.FINISH_ACTIVITY_AFTER_PURCHASE -> {
                Log.d(TAG, "handleNavigation: MAIN_TO_CHECKOUT")
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


    private var curCookingSlotId: Long? = null
    var selectedShippingMethod: ShippingMethod? = null
    var isCheckout: Boolean = false
    var isEvent: Boolean = false

    val errorEvent: SingleLiveEvent<List<WSError>> = SingleLiveEvent()

    fun openAddressChooser() {
//        actionEvent.postValue(NewOrderActionEvent.OPEN_ADDRESS_CHOOSER)
    }


//    fun getListOfAddresses(): java.util.ArrayList<Address>? {
//        return eaterDataManager.currentEater!!.addresses
//    }
//
//    fun getChosenAddress(): Address? {
//        return eaterDataManager.getLastChosenAddress()
//    }

    fun updateDeliveryTime(time: Date) {
//        progressData.startProgress()
        deliveryTimeManager.setNewDeliveryTime(time)

//        orderManager.updateOrderRequest(deliveryAt = eaterDataManager.getLastOrderTimeParam())
        orderData.postValue(orderData.value)
        val orderRequest = OrderRequest()
        orderRequest.deliveryAt = deliveryTimeManager.getDeliveryTimestamp()
//        postUpdateOrder(orderRequest)
    }

    fun updateAddress() {
//        val deliveryAddress = eaterDataManager.getLastChosenAddress()//todo - nyc
//        deliveryAddress?.let {
//            val orderRequest = OrderRequest()
//            orderRequest.deliveryAddressId = it.id
////            postUpdateOrder(orderRequest)
//        }
    }

//    data class EmptyCartEvent(val shouldShow: Boolean = false)

//    val emptyCartEvent = SingleLiveEvent<EmptyCartEvent>()


//    fun setIntentParams(menuItemId: Long = -1, isCheckout: Boolean = false, isEvent: Boolean) {
//        this.menuItemId = menuItemId
//        this.isCheckout = isCheckout
//        this.isEvent = isEvent
//        navigationEvent.postValue(NavigationEvent(menuItemId, isCheckout))
//    }


    val ephemeralKeyProvider: SingleLiveEvent<EphemeralKeyProviderEvent> = SingleLiveEvent()

    data class EphemeralKeyProviderEvent(val isSuccess: Boolean = false)

    override fun onEphemeralKeyProviderError() {
        ephemeralKeyProvider.postValue(EphemeralKeyProviderEvent(false))
    }

    //    fun setChosenAddress(address: Address) {
//        eaterDataManager.setUserChooseSpecificAddress(true)
////        eaterDataManager.setLastChosenAddress(address)//todo - nyc
//    }
//
    fun loadPreviousDish() {
//        navigationEvent.postValue(NavigationEvent(menuItemId = menuItemId))
    }


    //ORDER DATA
    val orderRequestData = MutableLiveData<OrderRequest>() // current order request
    val additionalDishes = MutableLiveData<ArrayList<Dish>>() // current order other available dishes

    fun getLastOrderDetails() {
        orderManager.curOrderResponse?.let {
            orderData.postValue(orderManager.curOrderResponse)
        }
    }

    fun showProceedToCartBottomBar() {
        updateCartBottomBarByType(type = CartBottomBar.BottomBarTypes.PROCEED_TO_CHECKOUT, price = cartManager.calcTotalDishesPrice())
    }

    fun onNewOrderFinish() {
        cartManager.onNewOrderFinish()
    }

    fun clearCart() {
        cartManager.clearCart()
        getFullDish()
    }

    fun showStartNewCartDialog() {
        clearCartEvent.postValue(true)
    }

    fun showClearCartDialog() {
        clearCartEvent.postValue(true)
    }

    fun isCartEmpty(): Boolean {
        return cartManager.isEmpty()
    }

    fun onCooksProfileDishClick(menuItemId: Long?) {
        menuItemId?.let {
            refreshFullDish(menuItemId)
            navigationEvent.postValue(NewOrderNavigationEvent.REDIRECT_TO_DISH_INFO)
        }
    }

    fun refreshPaymentsMethod(context: Context) {
        paymentManager.getStripeCustomerCards(context, true)
    }


//    fun removeItemFromCart(orderItemId: OrderItem) {
//        cartManager.removeItemFromCart(orderItemId)
//    }


    var isFirstPurchase: Boolean = true
    val showDialogEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val procceedToCheckoutEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()

    data class PostOrderEvent(val isSuccess: Boolean = false, val order: Order?)

    val postOrderEvent: SingleLiveEvent<PostOrderEvent> = SingleLiveEvent()

//    //post current order - order details and order items - this method is used to add single dish to cart.
//    fun addToCart(
//        fullDish: FullDish? = null, quantity: Int = 1, removedIngredients: ArrayList<Long>? = null, note: String? = null, tipPercentage: Float? = null,
//        tipAmount: String? = null,
//        promoCode: String? = null
//    ) {
//        Log.d("wowNewOrderVM", "addToCart START")
//        val hasPendingOrder = orderManager.haveCurrentActiveOrder()
//        val hasPendingOrderFromDifferentCook = checkForDifferentOpenOrder(fullDish?.menuItem?.cookingSlot?.id)
//        if (hasPendingOrder) {
//            if (hasPendingOrderFromDifferentCook) {
//                fullDish?.let {
//                    checkForOpenOrderAndShowClearCartDialog(fullDish.menuItem?.cookingSlot?.id, fullDish.cook.getFullName())
//                }
//            }else{
//                addNewDishToCart(fullDish!!.id, quantity)
//                eventsManager.logUxCamEvent(Constants.UXCAM_EVENT_ADD_ADDITIONAL_DISH, getAddDishData())
//            }
//        } else {
//            var cookId: Long = -1
//            var dishId: Long = -1
//            var cookingSlotId: Long = -1
//
//            fullDish?.let {
//                cookId = it.cook.id
//                dishId = it.id
//                it.menuItem?.let {
//                    cookingSlotId = it.cookingSlot.id
//                }
//            }
//
//            val deliveryAddress = eaterDataManager.getLastChosenAddress()
//            val orderItem = OrderItemRequest(dishId = dishId, quantity = quantity, removedIndredientsIds = removedIngredients, notes = note)
//
//            orderManager.updateOrderRequest(
//                cookId = cookId,
//                cookingSlotId = cookingSlotId,
//                deliveryAddress = deliveryAddress,
//                tipPercentage = tipPercentage,
//                tipAmount = tipAmount,
//                promoCode = promoCode
//            )
//
//            orderManager.addOrderItem(orderItem)
//            progressData.startProgress()
//            apiService.postOrder(orderManager.getOrderRequest()).enqueue(object : Callback<ServerResponse<Order>> {
//                override fun onResponse(call: Call<ServerResponse<Order>>, response: Response<ServerResponse<Order>>) {
//                    progressData.endProgress()
//                    if (response.isSuccessful) {
//                        val order = response.body()?.data
//                        curCookingSlotId = order?.cookingSlot?.id
//                        initAdditionalDishDialog() //it will be more alegant if server will return the right list of additional dishes instead of client do that. todo - incase of major refactor
//                        orderManager.setOrderResponse(order)
//                        orderData.postValue(order)
//                        Log.d("wowNewOrderVM", "postOrder success: ${order.toString()}")
//                        showAdditionalDialogOrProcceedToCheckout()
//
//                        postOrderEvent.postValue(PostOrderEvent(true, order))
//
//                        eventsManager.sendAddToCart(order?.id)
//                        eventsManager.logUxCamEvent(Constants.UXCAM_EVENT_ADD_DISH, getAddDishData())
//
//                    } else {
//                        Log.d("wowNewOrderVM", "postOrder fail")
//                        postOrderEvent.postValue(PostOrderEvent(false, null))
//                    }
//                }
//
//                override fun onFailure(call: Call<ServerResponse<Order>>, t: Throwable) {
//                    Log.d("wowNewOrderVM", "postOrder big fail")
//                    progressData.endProgress()
//                    postOrderEvent.postValue(PostOrderEvent(false, null))
//                }
//            })
//            Log.d("wowNewOrderVM", "addToCart finish")
//        }
//
//    }
//
//    fun initAdditionalDishDialog() {
//        if(isFirstPurchase) {
//            curCookingSlotId?.let {
//                fullDishData?.let {
//                    setAdditionalDishes(it.getAdditionalDishes(curCookingSlotId))
//                }
//            }
//        }
//    }
//
//    fun showAdditionalDialogOrProcceedToCheckout() {
//        if (fullDishData?.cook?.dishes != null) {
//            if (fullDishData?.cook?.dishes!!.size > 1 && isFirstPurchase) {
//                showDialogEvent.postValue(isFirstPurchase)
//                isFirstPurchase = false
//            } else {
//                procceedToCheckoutEvent.postValue(true)
//            }
//        } else {
//            procceedToCheckoutEvent.postValue(true)
//        }
//    }
//
//    fun showAdditionalDialogIfFirst() {
//        if (fullDishData?.cook?.dishes != null) {
//            if (fullDishData?.cook?.dishes!!.size > 1 && isFirstPurchase) {
//                showDialogEvent.postValue(isFirstPurchase)
//                isFirstPurchase = false
//            }
//        }
//    }
//
//
//    fun addNewDishToCart(dishId: Long = -1, quantity: Int? = 1) {
//        Log.d("wowNewOrderVM", "addNewDishToCart START")
//        progressData.startProgress()
//
//        val newOrderItem = OrderItemRequest(dishId = dishId, quantity = quantity)
//
//        val orderRequest = orderManager.getOrderRequest()
//
//        orderRequest.orderItemRequests?.clear()
//        orderRequest.orderItemRequests?.add(newOrderItem)
//        val orderId = orderManager.curOrderResponse!!.id
//        apiService.updateOrder(orderId, orderRequest)
//            .enqueue(object : Callback<ServerResponse<Order>> {
//                override fun onResponse(call: Call<ServerResponse<Order>>, response: Response<ServerResponse<Order>>) {
//                    progressData.endProgress()
//                    if (response.isSuccessful) {
//                        val updatedOrder = response.body()?.data
//                        curCookingSlotId = updatedOrder?.cookingSlot?.id
//                        initAdditionalDishDialog()
//                        orderManager.setOrderResponse(updatedOrder)
//                        orderData.postValue(updatedOrder)
//                        showAdditionalDialogIfFirst()
//                        eventsManager.logUxCamEvent(Constants.UXCAM_EVENT_ADD_ADDITIONAL_DISH, getAddDishData())
//                    } else {
//                        Log.d("wowNewOrderVM", "updateOrder FAILED")
//                    }
//                }
//
//                override fun onFailure(call: Call<ServerResponse<Order>>, t: Throwable) {
//                    progressData.endProgress()
//                    Log.d("wowNewOrderVM", "updateOrder big FAILED")
//                }
//            })
//        Log.d("wowNewOrderVM", "addNewDishToCart finish")
//    }
//
//    //update order - items quantity
//    fun updateOrder(updatedOrderItem: OrderItem) {
//        val orderResponse = orderManager.curOrderResponse
//
//        val orderRequest = orderManager.getOrderRequest()
//        val orderItems = orderRequest.orderItemRequests
//
//        //check emptyCart State - one item in cart and current updated item quantity is 0
//        if (orderResponse?.orderItems?.size == 1 && updatedOrderItem.quantity == 0) {
//            //show empty cart dialog
//            emptyCartEvent.postValue(EmptyCartEvent(shouldShow = true))
//        } else {
//            val tempOrderItems: ArrayList<OrderItemRequest> = arrayListOf()
//            for (item in orderResponse?.orderItems!!) {
//                if (item.id == updatedOrderItem.id) {
//                    item.quantity = updatedOrderItem.quantity
//                }
//                val removedIngredientIds: ArrayList<Long> = arrayListOf()
//                for (ingItem in item.removedIndredients) {
//                    ingItem.id?.let { removedIngredientIds.add(it) }
//                }
//                val updatedOrderItemRequest = OrderItemRequest(item.id, item.dish.id, item.quantity, removedIngredientIds, item.notes)
//                if (item.quantity <= 0) { //this line make sure dishes with 0 quantitny will be left outside of the order
//                    updatedOrderItemRequest._destroy = true
//                }
//                tempOrderItems.add(updatedOrderItemRequest)
//            }
//
//            orderItems!!.clear()
//            orderItems.addAll(tempOrderItems)
//
//            postUpdateOrder(orderRequest)
//        }
//
//    }
//
//
//    val updateOrderError: SingleLiveEvent<Int> = SingleLiveEvent()
//    private fun postUpdateOrder(orderRequest: OrderRequest) {
//        progressData.startProgress()
//        apiService.updateOrder(orderManager.curOrderResponse!!.id, orderRequest)
//            .enqueue(object : BaseCallback<ServerResponse<Order>>() {
//                override fun onSuccess(result: ServerResponse<Order>) {
//                    progressData.endProgress()
//                    val updatedOrder = result.data
//                    orderManager.setOrderResponse(updatedOrder)
//                    orderData.postValue(updatedOrder)
//                }
//
//                override fun onError(error: List<WSError>) {
//                    progressData.endProgress()
//                    errorEvent.postValue(error)
//
////                    val errorCode = response.code()
////                    if (errorCode == 400) {
////                        updateOrderError.postValue(errorCode)
////                    }
//                }
//            })
//    }
//
//    fun calcTotalDishesPrice(): Double {
//        var total = 0.0
//        orderData.value?.orderItems?.let {
//            it.forEach {
//                total += (it.price.value * it.quantity)
//            }
//        }
//        return total
//    }
//
//    val hasOpenOrder: SingleLiveEvent<HasOpenOrder> = SingleLiveEvent()
//
//    data class HasOpenOrder(val hasOpenOrder: Boolean, val cookInCartName: String? = null, val currentShowingCookName: String? = null)
//
//    //check order status - is there any open order?
//    fun checkForOpenOrderAndShowClearCartDialog(currentCookingSlotid: Long?, currentShowingCookName: String) {
//        if (orderManager.haveCurrentActiveOrder()) {
//            val inCartOrder = orderManager.curOrderResponse
//            val inCartCookingSlot = inCartOrder?.cookingSlot
//            inCartCookingSlot?.let {
//                if (it.id != currentCookingSlotid) {
//                    val cookInCartName = inCartOrder.cook.getFullName()
//                    //if the showing dish's (cook) is the same as the in-cart order's cook
//                    hasOpenOrder.postValue(HasOpenOrder(true, cookInCartName, currentShowingCookName))
//                } else {
//                    hasOpenOrder.postValue(HasOpenOrder(false))
//                }
//            }
//        } else {
//            hasOpenOrder.postValue(HasOpenOrder(false))
//        }
//    }
//
//    //check if there is an order in cart from diffrent cook
//    fun checkForDifferentOpenOrder(currentCookingSlotId: Long?): Boolean {
//        if (orderManager.haveCurrentActiveOrder()) {
//            val inCartOrder = orderManager.curOrderResponse
//            val inCartCookingSlot = inCartOrder?.cookingSlot
//            inCartCookingSlot?.let {
//                if (it.id != currentCookingSlotId) {
//                    return true
//                }
//            }
//        }
//        return false
//    }
//
//
//    val checkCartStatus: MutableLiveData<CheckCartStatusEvent> = SingleLiveEvent()
//
//    data class CheckCartStatusEvent(val hasPendingOrder: Boolean)
//
//    fun checkCartStatus() {
//        val hasPendingOrder = orderManager.haveCurrentActiveOrder()
//        checkCartStatus.postValue(CheckCartStatusEvent(hasPendingOrder))
//    }
//
//
//    data class CheckoutEvent(val isSuccess: Boolean)
//
//    val checkoutOrderEvent: SingleLiveEvent<CheckoutEvent> = SingleLiveEvent()
//
//    //checkout order
//    fun checkoutOrder(orderId: Long) {
//        progressData.startProgress()
//        apiService.checkoutOrder(orderId, eaterDataManager.getCustomerCardId())
//            .enqueue(object : BaseCallback<ServerResponse<Void>>() {
//                override fun onSuccess(result: ServerResponse<Void>) {
//                    progressData.endProgress()
//                    checkoutOrderEvent.postValue(CheckoutEvent(true))
//                    //todo - check this ! that it send purchase cost
//                    val totalCost = orderManager.getTotalCost()
//                    eventsManager.sendPurchaseEvent(orderId, "")
//                    eventsManager.logUxCamEvent(Constants.UXCAM_EVENT_ORDER_PLACED, getOrderValue())
//                    orderManager.clearCurrentOrder()
//
//                }
//
//                override fun onError(errors: List<WSError>) {
//                    progressData.endProgress()
//                    errorEvent.postValue(errors)
//                }
//            })
//    }
//
//    private fun getOrderValue(): Map<String, String>? {
//        val totalCostStr = orderManager.getTotalCostValue()
//        val chefsName = getCurrentOrderChefName()
//        val dishesName = orderManager.getCurrentOrderDishNames()
//        val cuisine = getCurrentOrderChefCuisine()
//        val data = mutableMapOf<String, String>("revenue" to totalCostStr, "currency" to "USD", "Chefs name" to chefsName)
//        dishesName.forEachIndexed {index, it ->
//            data["Dish name_${index}"] = it
//        }
//        cuisine.forEachIndexed {index, it ->
//            data["Cuisine_${index}"] = it
//        }
//        return data
//    }
//
//    private fun getAddDishData(): Map<String, String>? {
//        val chefsName = getCurrentOrderChefName()
//        val dishesName = orderManager.getCurrentOrderDishNames()
//        val cuisine = getCurrentOrderChefCuisine()
//        val data = mutableMapOf<String, String>("cook_name" to chefsName)
//        dishesName.forEachIndexed {index, it ->
//            data["Dish name_${index}"] = it
//        }
//        if(cuisine.isNotEmpty()){
//            data["Cuisine"] = cuisine[0]
//        }
//        return data
//    }
//
//    private fun getCurrentOrderChefCuisine(): List<String> {
//        val cuisine = mutableListOf<String>()
//        fullDishData?.cuisines?.let{
//            it.forEach {
//                cuisine.add(it.name)
//            }
//        }
//        return cuisine
//    }
//
//    private fun getCurrentOrderChefName(): String {
//        fullDishData.let{
//            return it?.cook?.getFullName() ?: "no_name"
//        }
//    }
//
//
//    //clear order
//    fun clearCart() {
//        orderManager.clearCurrentOrder()
//    }
//
//
//    //Additional single params
//    fun resetTip() {
//        tipInDollars.postValue(0)
//        tipPercentage.postValue(0)
//        postUpdateOrder(OrderRequest(tipPercentage = null))
//        postUpdateOrder(OrderRequest(tip = null))
//    }
//
//    val tipInDollars = MutableLiveData<Int>()
//    val tipPercentage = MutableLiveData<Int>()
//
//    fun updateTip(tipPercentage: Int? = null, tipInCents: Int? = null) {
//        cartManager.updateOrderRequest(OrderRequest(tip = tipInCents?.times(100), tipPercentage = tipPercentage?.toFloat()))
////        tipPercentage?.let {
////            this.tipPercentage.postValue(tipPercentage)
////        }
////        tipInCents?.let {
////            this.tipInDollars.postValue(tipInCents)
////        }
//    }
//
//    fun updateAddUtensils(shouldAdd: Boolean) {
//        orderManager.updateOrderRequest(addUtensils = shouldAdd)
//        postUpdateOrder(OrderRequest(addUtensils = shouldAdd))
//    }
//
//    fun updateShppingMethod(shippingMethod: ShippingMethod){
//        selectedShippingMethod = shippingMethod
//        postUpdateOrder(OrderRequest(shippingService = shippingMethod.code))
//    }
//
//    fun setAdditionalDishes(dishes: ArrayList<Dish>) {
//        //get only available dishes
//        Log.d("wowAdditionalMainAdptr", "setAdditionalDishes $dishes")
//        additionalDishes.postValue(dishes)
//    }
//
//    data class EditDeliveryTime(val startAt: Date?, val endsAt: Date?)
//
//    val editDeliveryTime: SingleLiveEvent<EditDeliveryTime> = SingleLiveEvent()
//    fun editDeliveryTime() {
//        val now = Date()
////        var availableCookingSlotStartsAt = orderData.value?.cookingSlot?.startsAt
//        var availableCookingSlotStartsAt = orderData.value?.cookingSlot?.orderFrom
//        availableCookingSlotStartsAt?.let {
//            if (now.time > it.time) {
//                availableCookingSlotStartsAt = now
//            }
//        }
//        val availableCookingSlotEndsAt = orderData.value?.cookingSlot?.endsAt
//        editDeliveryTime.postValue(EditDeliveryTime(availableCookingSlotStartsAt, availableCookingSlotEndsAt))
//    }
//
//    fun rollBackToPreviousAddress() {
//        eaterDataManager.rollBackToPreviousAddress()
//    }
//
//
//    //Stripe stuff
//    val getStripeCustomerCards: SingleLiveEvent<StripeCustomerCardsEvent> = SingleLiveEvent()
//
//    data class StripeCustomerCardsEvent(val isSuccess: Boolean, val paymentMethods: List<PaymentMethod>? = null)
//
//    fun getStripeCustomerCards(context: Context): SingleLiveEvent<List<PaymentMethod>> {
//        return paymentManager.getStripeCustomerCards(context)
//    }
//
//    fun updateUserCustomerCard(paymentMethod: PaymentMethod) {
//        eaterDataManager.updateCustomerCard(paymentMethod)
//    }
//
//    val shippingMethodsEvent = SingleLiveEvent<ArrayList<ShippingMethod>>()
//    fun onNationwideShippingSelectClick() {
//        progressData.startProgress()
//        orderData.value?.id?.let {
//            apiService.getUpsShippingRates(it).enqueue(object : BaseCallback<ServerResponse<ArrayList<ShippingMethod>>>() {
//                override fun onSuccess(result: ServerResponse<ArrayList<ShippingMethod>>) {
//                    shippingMethodsEvent.postValue(result.data)
//                    progressData.endProgress()
//                }
//
//                override fun onError(errors: List<WSError>) {
//                    progressData.endProgress()
//                    errorEvent.postValue(errors)
//                }
//            })
//        }
//    }
//
//    fun setFullDishParam(fullDish: FullDish) {
//        this.fullDishData = fullDish
//    }
//
//    fun isNationwideOrder(): Boolean {
//        orderData.value?.let{
//            return it.cookingSlot.isNationwide
//        }
//        return false
//    }
//
//    fun pulItemBackToAdditionalList(curOrderItem: OrderItem) {
//        curCookingSlotId?.let {
//            fullDishData?.let {
//                setAdditionalDishes(it.getAdditionalDishes(curCookingSlotId))
//            }
//        }
//    }
//
//    private fun getAddDishData(): Map<String, String>? {
//        val chefsName = getCurrentOrderChefName()
//        val dishesName = orderManager.getCurrentOrderDishNames()
//        val cuisine = getCurrentOrderChefCuisine()
//        val data = mutableMapOf<String, String>("Chefs name" to chefsName)
//        dishesName.forEachIndexed {index, it ->
//            data["Dish name_${index}"] = it
//        }
//        cuisine.forEachIndexed {index, it ->
//            data["Cuisine_${index}"] = it
//        }
//        return data
//    }
//
//    private fun getCurrentOrderChefCuisine(): List<String> {
//        val cuisine = mutableListOf<String>()
//        fullDishData?.cook?.let{
//            it.cuisines.forEach {
//                cuisine.add(it.name)
//            }
//        }
//        return cuisine
//    }
//
//    private fun getCurrentOrderChefName(): String {
//        fullDishData.let{
//            return it?.cook?.getFullName() ?: "no_name"
//        }
//    }

    companion object {
        const val TAG = "wowNewOrderVM"
    }


}
