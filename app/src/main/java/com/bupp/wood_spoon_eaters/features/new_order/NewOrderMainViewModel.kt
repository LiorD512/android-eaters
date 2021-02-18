package com.bupp.wood_spoon_eaters.features.new_order

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
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class NewOrderMainViewModel(
    val feedDataManager: FeedDataManager,
    val cartManager: CartManager,
    val metaDataRepository: MetaDataRepository,
    val orderManager: OrderManager,
    val eaterDataManager: EaterDataManager,
    val deliveryTimeManager: DeliveryTimeManager,
    private val newOrderRepository: OrderRepository,
    val paymentManager: PaymentManager
) : ViewModel(),
    EphemeralKeyProvider.EphemeralKeyProviderListener {



    var menuItemId: Long? = null
    val progressData = ProgressData()
    val wsErrorEvent = MutableLiveData<List<WSError>>()

    val dishInfoEvent = MutableLiveData<FullDish>()
    val dishCookEvent = MutableLiveData<Cook>()
    val cartStatusEvent = MutableLiveData<CartManager.CartStatus>()

    val deliveryTimeLiveData = cartManager.deliveryTimeLiveData

    val getReviewsEvent: SingleLiveEvent<Review?> = SingleLiveEvent()

    val additionalDishesEvent = MutableLiveData<AdditionalDishesEvent>()
    data class AdditionalDishesEvent(val orderItems: List<OrderItem>?, val additionalDishes: List<Dish>?)

    val cartBottomBarEvent = MutableLiveData<CartBottomBarEvent>()
    data class CartBottomBarEvent(val type: Int, val price: Double, val itemCount: Int? = null)

    val mainActionEvent = MutableLiveData<NewOrderActionEvent>()
    enum class NewOrderActionEvent{
        ADD_CURRENT_DISH_TO_CART,
        SHOW_ADDITIONAL_DISH_DIALOG,

    }


    val navigationEvent = MutableLiveData<NewOrderNavigationEvent>()
    enum class NewOrderNavigationEvent{
        REDIRECT_TO_CHECKOUT,
        REDIRECT_TO_COOK_PROFILE,
        SHOW_ADDRESS_MISSING_DIALOG,
        REDIRECT_TO_SELECT_PROMO_CODE,
        START_LOCATION_AND_ADDRESS_ACTIVITY
    }

    fun initNewOrderActivity(intent: Intent?) {
        //get full dish and check order status prior to dish
        intent?.let{
            menuItemId = it.getLongExtra(Constants.NEW_ORDER_MENU_ITEM_ID, Constants.NOTHING.toLong())
        }
        menuItemId?.let{
            viewModelScope.launch {
                progressData.startProgress()
                val getFullDishResult = cartManager.getFullDish(it)
                getFullDishResult?.let{
                    dishInfoEvent.postValue(it.fullDish)
                    dishCookEvent.postValue(it.fullDish.cook)
                }
            }
        }
    }

    fun checkCartStatusAndUpdateUi(){
        cartStatusEvent.postValue(cartManager.getOrderStatus())
    }

    fun updateCartBottomBar(type: Int, price: Double, itemCount: Int? = null) {
        cartBottomBarEvent.postValue(CartBottomBarEvent(type, price, itemCount))
    }

    fun onCartBottomBarClick(type: Int) {
        when(type){
            Constants.CART_BOTTOM_BAR_TYPE_CART -> {
                handleAddToCartClick()
            }
            Constants.CART_BOTTOM_BAR_TYPE_CHECKOUT -> {

            }
            Constants.CART_BOTTOM_BAR_TYPE_FINALIZE -> {

            }
        }
    }

    private fun handleAddToCartClick() {
        //check if user set an address and add to cart
        if(eaterDataManager.hasUserSetAnAddress()){
            addToCart()
        }else{
            navigationEvent.postValue(NewOrderNavigationEvent.SHOW_ADDRESS_MISSING_DIALOG)
        }

    }

    private fun addToCart(){
        viewModelScope.launch {
            val result = cartManager.addCurrentOrderItemToCart()

            when(result.type){
                OrderRepository.OrderRepoStatus.POST_ORDER_SUCCESS -> {
                    if(cartManager.shouldShowAdditionalDishesDialog()){
                        mainActionEvent.postValue(NewOrderActionEvent.SHOW_ADDITIONAL_DISH_DIALOG)
                    }else{
                        navigationEvent.postValue(NewOrderNavigationEvent.REDIRECT_TO_COOK_PROFILE)
                        showProceedToCartBottomBar()
                    }

                }
                OrderRepository.OrderRepoStatus.POST_ORDER_FAILED -> {
                    result.data
                }
                OrderRepository.OrderRepoStatus.WS_ERROR -> {
                    result.wsError?.let{
                        wsErrorEvent.postValue(it)
                    }
                }
                else -> {}
            }
        }
    }

    fun updateOrderItem(orderItem: OrderItem) {
        viewModelScope.launch {
            val result = cartManager.updateInCartOrderItem(orderItem)
            result?.let{
                when(result.type){
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {
                        initAdditionalDishes()
                    }
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {
                    }
                    OrderRepository.OrderRepoStatus.WS_ERROR -> {
                        result.wsError?.let{
                            wsErrorEvent.postValue(it)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun initAdditionalDishes() {
        val orderItems = cartManager.getCurrentOrderItems()
        val additionalDishes = cartManager.getAdditionalDishes()
        additionalDishesEvent.postValue(AdditionalDishesEvent(orderItems, additionalDishes))
    }

    fun addNewDishToCart(dishId: Long, quantity: Int) {
        val newOrderItem = OrderItemRequest(dishId = dishId, quantity = quantity)
        Log.d(TAG, "addNewDishToCart: $newOrderItem")
        viewModelScope.launch {
            val result = cartManager.addNewOrderItemToCart(newOrderItem)
            result?.let{
                when(result.type){
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {
                        initAdditionalDishes()
                    }
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {
                    }
                    OrderRepository.OrderRepoStatus.WS_ERROR -> {
                        result.wsError?.let{
                            wsErrorEvent.postValue(it)
                        }
                    }
                    else -> {}
                }
            }
        }
    }


    fun getCooksReview() {
//        progressData.startProgress()
//        val currentCookId = cartManager.currentShowingDish?.cook?.id
//        currentCookId?.let{
//            viewModelScope.launch {
//                val result = newOrderRepository.getDishReview(it)
//                result?.let{
//                    getReviewsEvent.postValue(it)
//                }
//            }
//        }
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

    data class EmptyCartEvent(val shouldShow: Boolean = false)

    val emptyCartEvent = SingleLiveEvent<EmptyCartEvent>()


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
    val orderData = MutableLiveData<Order>() //current order result
    val orderRequestData = MutableLiveData<OrderRequest>() // current order request
    val additionalDishes = MutableLiveData<ArrayList<Dish>>() // current order other available dishes

    fun getLastOrderDetails() {
        orderManager.curOrderResponse?.let {
            orderData.postValue(orderManager.curOrderResponse)
        }
    }

    fun showProceedToCartBottomBar() {
        updateCartBottomBar(type = Constants.CART_BOTTOM_BAR_TYPE_CHECKOUT, price = cartManager.calcTotalDishesPrice())
    }


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
//        tipPercentage?.let {
//            this.tipPercentage.postValue(tipPercentage)
//        }
//        tipInCents?.let {
//            this.tipInDollars.postValue(tipInCents)
//        }
//        postUpdateOrder(OrderRequest(tip = tipInCents?.times(100), tipPercentage = tipPercentage?.toFloat()))
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
////    val getStripeCustomerCards: SingleLiveEvent<StripeCustomerCardsEvent> = SingleLiveEvent()
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

    companion object{
        const val TAG = "wowNewOrderVM"
    }


}
