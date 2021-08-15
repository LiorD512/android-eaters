package com.bupp.wood_spoon_eaters.managers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.managers.location.LocationManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils
import java.util.*

class OldCartManager(
    private val orderRepository: OrderRepository,
    private val feedDataManager: FeedDataManager,
    private val deliveryTimeManager: DeliveryTimeManager,
    private val locationManager: LocationManager,
    private val eventsManager: EventsManager
) {

    private var isInCheckout = false
    private var shippingService: String? = null
    fun getCurrentOrderData() = currentOrderLiveData
    private val currentOrderLiveData = MutableLiveData<Order>()

    //    private var currentOrderRequest: OrderRequest? = null
    var currentShowingDish: FullDish? = null
    private var currentOrderResponse: Order? = null

    private var currentOrderItemRequest: OrderItemRequest? = null

    //    private var currentOrderDeliveryTime: Date? = null
    private val cart: MutableList<OrderItemRequest> = mutableListOf()

//    val globalDeliveryTimeLiveData = deliveryTimeManager.getDeliveryTimeLiveData()
//    var currentCartDeliveryTimestamp: String? =

    data class GetFullDishResult(
        val fullDish: FullDish,
        val isAvailable: Boolean,
        val startingTime: Date?,
        val isSoldOut: Boolean
    )

    private fun buildDishRequest(): FeedRequest {
        val feedRequest = FeedRequest()
        val lastAddress = locationManager.getFinalAddressLiveDataParam().value
        lastAddress?.let {
            //address
            if (lastAddress.id != null) {
                feedRequest.addressId = lastAddress.id
            } else {
                feedRequest.lat = lastAddress.lat
                feedRequest.lng = lastAddress.lng
            }
        }

        //time
        feedRequest.timestamp = deliveryTimeManager.getTempDeliveryTimeStamp()

        return feedRequest
    }

    suspend fun getFullDish(menuItemId: Long): GetFullDishResult? {
//        val feedRequest = feedDataManager.getLastFeedRequest()
        val result = orderRepository.getFullDish(menuItemId)
        result.data?.let {
            //build new currentItemRequest
            initNewOrderItemRequest(it)
            this.currentShowingDish = it
            checkIfFutureDish()
            return GetFullDishResult(
                it,
                isAvailable = checkCookingSlotAvailability(),
                startingTime = getStartingDate(),
                isSoldOut = checkDishSellout()
            )
        }
        //inspect result log if reach here.
        return null
    }

    private fun checkIfFutureDish() {
        Log.d(TAG, "checkIfFutureDish")
//            if(currentShowingDish?.menuItem?.orderAt == null){
//                //Dish is offered today.
//                return globalDeliveryTimeLiveData.value?.deliveryTimestamp
//            }else{
        currentShowingDish?.menuItem?.orderAt?.let {
            //Dish is offered in the future.
            val userSelectedDate = DateUtils.parseFromUnixTimestamp(deliveryTimeManager.getTempDeliveryTimeStamp())
            if (userSelectedDate.before(it)) {
                //order stating in the future. needs to update order delivery time to "orderAt" time
                deliveryTimeManager.setTemporaryDeliveryTimeDate(it)
                Log.d(TAG, "checkIfFutureDish - changing delivery time to future order")
            } else {
                //do nothing. stay with user selected date,

            }
        }

    }


//    fun refreshDeliveryTimeByUserTimeSelection() {
//        currentCartDeliveryTimestamp = globalDeliveryTimeLiveData.value?.deliveryTimestamp
//    }

    private fun checkCookingSlotAvailability(): Boolean {
        currentShowingDish?.let {
            val orderFrom: Date? = it.menuItem?.cookingSlot?.orderFrom
            val start: Date? = it.menuItem?.cookingSlot?.orderFrom
            val end: Date? = it.menuItem?.cookingSlot?.endsAt
            var userSelection: Date? = deliveryTimeManager.getDeliveryTimeDate()

            if (start == null || end == null) {
                return false
            }
            if (userSelection == null) {
                //in this case order is ASAP - then check from starting time and not orderingFrom time
                userSelection = Date()
                return (userSelection.equals(start) || userSelection.equals(end)) || (userSelection.after(start) && userSelection.before(end))
            }
            return (userSelection.equals(orderFrom) || userSelection.equals(end)) || (userSelection.after(orderFrom) && userSelection.before(end))
        }
        return true
    }

    private fun getStartingDate(): Date {
        var newDate = Date()
        currentShowingDish?.menuItem?.cookingSlot?.orderFrom?.let {
            if (it.after(newDate)) {
                newDate = it
            }
        }
        return newDate
    }

    private fun checkDishSellout(): Boolean {
        currentShowingDish?.let {
            val quantity = it.menuItem?.quantity
            val unitsSold = it.menuItem?.unitsSold
            quantity?.let {
                unitsSold?.let {
                    return ((quantity - unitsSold <= 0))
                }
            }
        }
        return false
    }

    data class CartStatus(
        val type: CartStatusEventType,
        val inCartTotalPrice: Double? = null,
        val currentOrderItemCounter: Int? = null,
        val currentOrderItemPrice: Double? = null
    )

    enum class CartStatusEventType {
        CART_IS_EMPTY,
        SHOWING_CURRENT_CART,
//        DIFFERENT_COOKING_SLOT
    }

    data class NewCartData(
        val inCartCookName: String? = null,
        val currentShowingCookName: String? = null
    )

    //check if needs to force clear cart - when user tries to add item to cart while has different cooking slot in his cart
    fun shouldForceClearCart(): NewCartData? {
        currentOrderResponse?.let { orderResponse ->
            val currentCookingSlotId = currentShowingDish?.menuItem?.cookingSlot?.id
            val inCartCookingSlot = orderResponse.cookingSlot
            inCartCookingSlot?.let { cookingSlot ->
                if (cookingSlot.id != currentCookingSlotId) {
                    //if the showing dish's (cook) is the same as the in-cart order's cook
                    val currentShowingCookName = currentShowingDish?.restaurant?.getFullName()
                    val inCartCookName = orderResponse.restaurant?.getFullName()
                    return NewCartData(
                        inCartCookName = inCartCookName,
                        currentShowingCookName = currentShowingCookName
                    )
                }
            }
        }
        return null
    }

    //check order status - is there any open order?
    fun getCartStatus(): CartStatus {
        currentOrderResponse?.let { orderResponse ->
            val inCartCookingSlot = orderResponse.cookingSlot
            inCartCookingSlot.let { inCartCookingSlot ->
                //SHOWING_CURRENT_CART
                val inCartTotalPrice = currentOrderResponse?.total?.value
                val currentOrderItemCounter = currentOrderItemRequest?.quantity
                val currentOrderItemPrice = currentShowingDish?.price?.value
                return CartStatus(
                    CartStatusEventType.SHOWING_CURRENT_CART,
                    inCartTotalPrice = inCartTotalPrice,
                    currentOrderItemCounter = currentOrderItemCounter,
                    currentOrderItemPrice = currentOrderItemPrice
                )
            }
        }
        return CartStatus(CartStatusEventType.CART_IS_EMPTY, currentOrderItemPrice = currentShowingDish?.getPriceObj()?.value, currentOrderItemCounter = 1)
    }

    //cart methods
    private fun initNewOrderItemRequest(fullDish: FullDish) {
        Log.d(TAG, "initNewOrderIteRequest")
        val newOrderItemRequest = OrderItemRequest(dishId = fullDish.id, quantity = 1, id = null, removedIngredientsIds = null, notes = null, _destroy = null)
        updateCurrentOrderItemRequest(newOrderItemRequest)
    }

    fun updateCurrentOrderItemRequest(orderItemRequest: OrderItemRequest) {
        Log.d(TAG, "updateCurrentOrderItemRequest")
        currentOrderItemRequest = OrderItemRequest(
            dishId = orderItemRequest.dishId ?: currentOrderItemRequest?.dishId,
            notes = orderItemRequest.notes ?: currentOrderItemRequest?.notes,
            quantity = orderItemRequest.quantity ?: currentOrderItemRequest?.quantity,
            removedIngredientsIds = orderItemRequest.removedIngredientsIds ?: currentOrderItemRequest?.removedIngredientsIds
        )
        Log.d(TAG, "updateCurrentOrderItem: $currentOrderItemRequest")
    }

    fun addCurrentOrderItemToCart() {
        Log.d(TAG, "addCurrentOrderItemToCart")
        currentOrderItemRequest?.let {
            cart.add(0, it)
            Log.d(TAG, "addNewItemToCart: $currentOrderItemRequest")
        }
    }

    fun removeLastOrderItem() {
            cart.removeAt(0)
            Log.d(TAG, "addNewItemToCart: $cart")
    }

    suspend fun updateInCartOrderItem(updatedOrderItem: OrderItem): OrderRepository.OrderRepoResult<Order>? {
        Log.d(TAG, "updateInCartOrderItem")
        //this method used to update orderItems that changed in AdditionalDishesDialog and checkout.
        //get specific orderItem from response and update cart with new orderItemRequest
        for (item in currentOrderResponse?.orderItems ?: arrayListOf()) {
            if (item.id == updatedOrderItem.id) {
                cart.find { it.dishId == item.dish.id }?.apply {
                    id = updatedOrderItem.id
                    quantity = updatedOrderItem.quantity
                    notes = updatedOrderItem.notes
                    _destroy = updatedOrderItem._destroy
                }
            }
        }

        return postNewOrUpdateCart()
    }

    suspend fun postNewOrUpdateCart(): OrderRepository.OrderRepoResult<Order>? {
        Log.d(TAG, "postNewOrUpdateCart..")
        val orderRequest = buildOrderRequest()
        var result: OrderRepository.OrderRepoResult<Order>? = null
        if (currentOrderResponse == null) {
            //this is first itemRequest therefore post new order
            Log.d(TAG, "postNewOrUpdateCart.. posting new order")
            result = orderRepository.postNewOrder(orderRequest)

//            eventsManager.sendAddToCart(result.data?.id)

            eventsManager.logEvent(Constants.EVENT_ADD_DISH, getAddDishData(result.data?.id))
        } else {
            //order already have items therefore update order
            Log.d(TAG, "postNewOrUpdateCart.. updating current order")
            result = postUpdateOrder(orderRequest)

            eventsManager.logEvent(Constants.EVENT_ADD_ADDITIONAL_DISH, getAddDishData(result?.data?.id))
        }
        result?.data?.let {
            updateCartManagerParams(it.copy())
        }
        return result
    }


    suspend fun addNewOrderItemToCart(orderItemRequest: OrderItemRequest): OrderRepository.OrderRepoResult<Order>? {
        Log.d(TAG, "addNewOrderItemToCart")
        val tempCart = mutableListOf<OrderItemRequest>()
        tempCart.add(orderItemRequest)
        cart.add(0, orderItemRequest)
        val orderRequest = buildOrderRequest(tempCart)
        return postUpdateOrder(orderRequest)
    }

    private fun buildOrderRequest(tempCart: List<OrderItemRequest>? = null): OrderRequest {
        Log.d(TAG, "buildOrderRequest withTempCart: ${!tempCart.isNullOrEmpty()}")
        val cookingSlotId = currentShowingDish?.menuItem?.cookingSlot?.id
        val deliverAt = deliveryTimeManager.getTempDeliveryTimeStamp()
        val deliveryAddressId = feedDataManager.getFinalAddressLiveDataParam().value?.id

        return OrderRequest(
            cookingSlotId = cookingSlotId,
            deliveryAt = deliverAt,
            deliveryAddressId = deliveryAddressId,
            orderItemRequests = tempCart ?: cart
        )
    }

    suspend fun postUpdateOrder(orderRequest: OrderRequest, eventType: String? = null): OrderRepository.OrderRepoResult<Order>? {
        Log.d(TAG, "postUpdateOrder")
        currentOrderResponse?.let {
            val result = orderRepository.updateOrder(it.id!!, orderRequest)
            handleEvent(eventType)
            result.data?.let {
                updateCartManagerParams(it.copy())
                return result
            }
            return result
        }
        return null
    }

    private fun handleEvent(eventType: String?) {
        eventType?.let {
            when (it) {
                Constants.EVENT_TIP -> {
                    eventsManager.logEvent(eventType, getTipData())
                }
            }
        }
    }

    private fun getTipData(): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["order_total_before_tip"] = currentOrderResponse?.totalBeforeTip?.formatedValue ?: "0"
        data["order_total_including_tip"] = currentOrderResponse?.total?.formatedValue ?: "0"
        data["tip_quantity"] = currentOrderResponse?.tip?.formatedValue ?: "0"
        return data
    }


    private fun updateCartManagerParams(order: Order) {
        Log.d(TAG, "updateCartParams")
        this.currentOrderResponse = order
        refreshCartWithUpdatedOrder(order)

        currentOrderLiveData.postValue(order)
        Log.d(TAG, "updated Cart: $cart")
    }

    private fun refreshCartWithUpdatedOrder(order: Order) {
        val updatedItems = mutableListOf<OrderItemRequest>()
        order.orderItems?.forEach { orderItem ->
            val item = orderItem.toOrderItemRequest()
            updatedItems.add(item)
        }
        cart.clear()
        cart.addAll(updatedItems)
    }

    fun clearCart() {
        Log.d(TAG, "clearCart")
        cart.clear()
        currentShowingDish = null
        currentOrderResponse = null
        isInCheckout = false
        deliveryTimeManager.clearDeliveryTime()
    }

    fun refreshOrderUi() {
        this.currentOrderResponse?.let {
            currentOrderLiveData.postValue(it)
        }
    }


    //Additional dishes bottom sheet
    fun shouldShowAdditionalDishesDialog(): Boolean {
        return cart.size == 1
    }

    fun getCurrentOrderItems(): List<OrderItem>? {
        currentOrderResponse?.let {
            return it.orderItems
        }
        return null
    }

    fun getAdditionalDishes(): List<Dish>? {
        currentShowingDish?.let {
            val allAdditionalDishes = it.getAdditionalDishes(it.menuItem?.cookingSlot?.id)
            currentOrderResponse?.orderItems?.let {
                val filteredAdditionals = allAdditionalDishes.f(it)
                return filteredAdditionals
            }
        }
        return null
    }

    fun List<Dish>.f(orderItems: List<OrderItem>) = filter { m -> orderItems.all { !it.dish.name.equals(m.name) } }

//    fun calcTotalDishesPrice(): Double {
//        var total = 0.0
//        currentOrderResponse?.orderItems?.let {
//            it.forEach {
//                total += (it.price.value * it.quantity)
//            }
//        }
//        return total
//    }

    fun calcTotalDishesPrice(): Double {
        var total = 0.0
        currentOrderResponse?.total?.value?.let {
            total = it
        }
        return total
    }

    fun getTotalPriceForDishQuantity(counter: Int): Double {
        currentShowingDish?.price?.value?.let {
            return it * counter
        }
        return 0.0
    }

    fun onNewOrderFinish() {
//        deliveryTimeManager.rollBackToPreviousDeliveryTime()
    }

    fun isEmpty(): Boolean {
        return cart.isEmpty()
    }

    suspend fun refreshOrderParams(): OrderRepository.OrderRepoResult<Order>? {
        val orderRequest = buildOrderRequest()
        return postUpdateOrder(orderRequest)
    }

    suspend fun updateOrderDeliveryParam(): OrderRepository.OrderRepoResult<Order>? {
        val orderRequest = buildOrderRequest(emptyList())
        return postUpdateOrder(orderRequest)
    }

    fun onLocationInvalid() {
        //roll back to previous locaiton.. current location doesnt valid for order
        locationManager.rollBackToPreviousAddress()
    }

    fun onDeliveryTimeInvalid() {
        //roll back to previous delivery time.. current time isnt valid for order
//        deliveryTimeManager.rollBackToPreviousDeliveryTime()
    }

    suspend fun finalizeOrder(paymentMethodId: String?): OrderRepository.OrderRepoResult<Any>? {
        this.currentOrderResponse?.id?.let { it ->
            val result = orderRepository.finalizeOrder(it, paymentMethodId)
            val isSuccess = result.type == OrderRepository.OrderRepoStatus.FINALIZE_ORDER_SUCCESS
            eventsManager.sendPurchaseEvent(it, calcTotalDishesPrice())
            eventsManager.logEvent(Constants.EVENT_ORDER_PLACED, getOrderValue(isSuccess, result.wsError))
            return result
        }
        return null
    }


    suspend fun getUpsShippingRates(): OrderRepository.OrderRepoResult<List<ShippingMethod>>? {
        this.currentOrderResponse?.id?.let { it ->
            return orderRepository.getUpsShippingRates(it)
        }
        return null
    }

    suspend fun updateShippingService(shippingService: String) {
        this.shippingService = shippingService
        val deliveryTime = currentShowingDish?.menuItem?.cookingSlot?.orderFrom
        val deliveryAtParam = DateUtils.parseUnixTimestamp(deliveryTime)
        val orderRequest = OrderRequest(shippingService = shippingService, deliveryAt = deliveryAtParam)
        postUpdateOrder(orderRequest)
    }

    fun checkShippingMethodValidation(): Boolean {
        return currentOrderResponse?.isNationwide == true && shippingService == null
    }

    //Events param -

    private fun getOrderValue(isSuccess: Boolean, wsError: List<WSError>? = null): Map<String, Any> {
        val chefsName = getCurrentOrderChefName()
        val chefsId = getCurrentOrderChefId()
        val totalCostStr = calcTotalDishesPrice()
        val dishesName = getCurrentOrderDishNames()
        val cuisine = getCurrentOrderChefCuisine()
        val data =
            mutableMapOf<String, Any>("revenue" to totalCostStr.toString(), "currency" to "USD", "cook_name" to chefsName, "success" to isSuccess.toString())
        data["cook_id"] = chefsId
        dishesName.let {
            data["dishes"] = it
        }
        if (cuisine.isNotEmpty()) {
            data["cuisine"] = cuisine
        }

        val isAsap = currentOrderResponse?.deliverAt == null
        data["ASAP"] = isAsap

        if (isSuccess) {
            currentOrderResponse?.tip?.value?.let {
                data["tip_amount"] = it
            }
            currentOrderResponse?.tax?.value?.let {
                data["tax"] = it
            }
            currentOrderResponse?.deliveryFee?.value?.let {
                data["delivery_fee"] = it
            }
            currentOrderResponse?.cooksServiceFee?.value?.let {
                data["cook_service_fee"] = it
            }
            currentOrderResponse?.serviceFee?.value?.let {
                data["woodspoon_service_fee"] = it
            }
            currentOrderResponse?.minOrderFee?.value?.let {
                data["min_order_fee"] = it
            }
            currentOrderResponse?.orderItems?.let {
                data["dish_count"] = it.size
            }
            currentOrderResponse?.deliveryAddress?.let {
                data["city"] = it.city?.name ?: "na"
                data["country"] = it.country?.name ?: "na"
                data["postalCode"] = it.zipCode ?: "na"
                data["state"] = it.state?.name ?: "na"
                data["street"] = it.streetLine1 ?: "na"
            }
        } else {
            wsError?.let {
                if (it.isNotEmpty()) {
                    data["error_message"] = it[0].msg ?: "no_error"
                }
            }
        }


        return data
    }

    private fun getCurrentOrderDishNames(): List<String> {
        val dishNames = mutableSetOf<String>()
        val chefsName = currentOrderResponse?.restaurant?.firstName ?: ""
        currentOrderResponse?.orderItems?.forEach {
            dishNames.add("${chefsName}_${it.dish.name}")
        }
        return dishNames.toList()
    }

    fun getAddDishData(id: Long? = null, dish: Dish? = null): Map<String, String> {
        val currentDishName = getCurrentDishName(dish)
        val chefsName = getCurrentOrderChefName()
        val chefsId = getCurrentOrderChefId()
        val cuisine = getCurrentOrderChefCuisine()
        val dishId = getCurrentDishId(dish)
        val dishPrice = getCurrentDishPrice(dish)
        val data = mutableMapOf<String, String>("cook_name" to chefsName)

        data["cook_id"] = chefsId
        data["dish_id"] = "$dishId"
        data["dish_price"] = dishPrice.toString()
        data["dish_name"] = currentDishName
        if (cuisine.isNotEmpty()) {
            data["cuisine"] = cuisine[0]
        }
        id?.let {
            data["order_id"] = id.toString()
        }

        return data
    }

    private fun getCurrentDishName(dish: Dish? = null): String {
        dish?.name?.let{
            return it
        }
        currentShowingDish?.name?.let {
            return it
        }
        return ""
    }

    fun sendFBAdditioanlDishEvent(dish: Dish) {
        eventsManager.logEvent(Constants.EVENT_ADD_ADDITIONAL_DISH, getAddDishData(dish = dish))
    }

    private fun getCurrentOrderChefId(): String {
        currentShowingDish.let {
            return it?.restaurant?.id.toString()
        }
    }

    private fun getCurrentDishPrice(dish: Dish? = null): Double? {
        dish?.let{
            return it.price?.value
        }
        currentShowingDish?.let {
            return it.price.value
        }
        return null
    }

    private fun getCurrentDishId(dish: Dish? = null): Long {
        dish?.let{
            return it.id
        }
        currentShowingDish?.let {
            return it.id
        }
        return 0
    }

    private fun getCurrentOrderChefCuisine(): List<String> {
        val cuisine = mutableSetOf<String>()
        currentOrderResponse?.orderItems?.forEach {
            if(!it.dish.cuisines.isNullOrEmpty()){
                it.dish.cuisines[0]?.let {
                    cuisine.add(it.name)
                }
            }
        }
        return cuisine.toList()
    }

    private fun getCurrentOrderChefName(): String {
        currentShowingDish.let {
            return it?.restaurant?.getFullName() ?: "no_name"
        }
    }

    fun isInCheckout(): Boolean {
        return this.isInCheckout
    }

    fun setIsInCheckout(isInCheckout: Boolean) {
        this.isInCheckout = isInCheckout
    }

    fun onDishChangeEvent() = deliveryTimeManager.getTempDeliveryTimeStamp()

    fun sendClickOnDishEvent() {
        val dishId = currentShowingDish?.id
        val param = getAddDishData(dishId)
        eventsManager.logOnDishClickEvent(param)
    }


//    fun checkoutOrder(orderId: Long) {
//        progressData.startProgress()
//        apiService.checkoutOrder(orderId, eaterDataManager.getCustomerCardId())
//            .enqueue(object : BaseCallback<ServerResponse<Void>>() {
//                override fun onSuccess(result: ServerResponse<Void>) {
//                    progressData.endProgress()
//                    checkoutOrderEvent.postValue(CheckoutEvent(true))
//                    //todo - check this ! that it send purchase cost
//                    val totalCost = orderManager.getTotalCostValue()
//                    eventsManager.sendPurchaseEvent(orderId, totalCost.toDouble())
//                    eventsManager.logUxCamEvent(Constants.UXCAM_EVENT_ORDER_PLACED, getOrderValue(true))
//                    orderManager.clearCurrentOrder()
//
//                }
//
//                override fun onError(errors: List<WSError>) {
//                    progressData.endProgress()
//                    eventsManager.logUxCamEvent(Constants.UXCAM_EVENT_ORDER_PLACED, getOrderValue(false))
//                    errorEvent.postValue(errors)
//                }
//            })
//    }


    companion object {
        const val TAG = "wowNCartManager"
    }


    // 1. init order - set basic param - cookingSlotId, deliveryTime etc,
    // 2. update current orderItemRequest - dishId, quantity
    // 2.1 update current orderItemRequest - note, ingredients if necessary
    // 3. create order. (POST) -> order Response.
    // 4. update existing item (orderItem id, orderItem)
    // 5. add new item (dishId, quantity) {orderItemRequest}


}