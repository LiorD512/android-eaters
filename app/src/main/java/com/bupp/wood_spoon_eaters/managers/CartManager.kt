package com.bupp.wood_spoon_eaters.managers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.managers.location.LocationManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils
import java.util.*

class CartManager(
    private val orderRepository: OrderRepository,
    private val feedDataManager: FeedDataManager,
    private val deliveryTimeManager: DeliveryTimeManager,
    private val locationManager: LocationManager
) {

    private var shippingService: String? = null
    fun getCurrentOrderData() = currentOrderLiveData
    private val currentOrderLiveData = MutableLiveData<Order>()

    //    private var currentOrderRequest: OrderRequest? = null
    var currentShowingDish: FullDish? = null
    private var currentOrderResponse: Order? = null

    private var currentOrderItemRequest: OrderItemRequest? = null

    //    private var currentOrderDeliveryTime: Date? = null
    private val cart: MutableList<OrderItemRequest> = mutableListOf()

    val deliveryTimeLiveData = deliveryTimeManager.getDeliveryTimeLiveData()

    data class GetFullDishResult(
        val fullDish: FullDish,
        val isAvailable: Boolean,
        val startingTime: Date?,
        val isSoldOut: Boolean
    )

    suspend fun getFullDish(menuItemId: Long): GetFullDishResult? {
        val feedRequest = feedDataManager.getLastFeedRequest()
        val result = orderRepository.getFullDish(menuItemId, feedRequest)
        result.data?.let {
            //build new currentItemRequest
            initNewOrderItemRequest(it)
            this.currentShowingDish = it
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

    private fun checkCookingSlotAvailability(): Boolean {
        currentShowingDish?.let {
            val orderFrom: Date? = it.menuItem?.cookingSlot?.orderFrom
            val start: Date? = it.menuItem?.cookingSlot?.startsAt
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

    private fun getStartingDate(): Date? {
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
        val inCartCookName: String? = null,
        val currentShowingCookName: String? = null,
        val inCartTotalPrice: Double? = null,
        val currentOrderItemCounter: Int? = null,
        val currentOrderItemPrice: Double? = null

    )

    enum class CartStatusEventType {
        NEW_ORDER,
        ADD_TO_ORDER_FOR_CURRENT_COOKING_SLOT,
        DIFFERENT_COOKING_SLOT
    }

    //check order status - is there any open order?
    fun getOrderStatus(): CartStatus {
        currentOrderResponse?.let { orderResponse ->
            val currentCookingSlotId = currentShowingDish?.menuItem?.cookingSlot?.id
            val inCartCookingSlot = orderResponse.cookingSlot
            inCartCookingSlot.let { inCartCookingSlot ->
                if (inCartCookingSlot?.id != currentCookingSlotId) {
                    //if the showing dish's (cook) is the same as the in-cart order's cook
                    val currentShowingCookName = currentShowingDish?.cook?.getFullName()
                    val inCartCookName = orderResponse.cook?.getFullName()
                    return CartStatus(
                        CartStatusEventType.DIFFERENT_COOKING_SLOT,
                        inCartCookName = inCartCookName,
                        currentShowingCookName = currentShowingCookName
                    )
                } else {
                    val inCartTotalPrice = currentOrderResponse?.total?.value
                    val currentOrderItemCounter = currentOrderItemRequest?.quantity
                    val currentOrderItemPrice = currentShowingDish?.price?.value
                    return CartStatus(
                        CartStatusEventType.ADD_TO_ORDER_FOR_CURRENT_COOKING_SLOT,
                        inCartTotalPrice = inCartTotalPrice,
                        currentOrderItemCounter = currentOrderItemCounter,
                        currentOrderItemPrice = currentOrderItemPrice
                    )
                }
            }
        }
        return CartStatus(CartStatusEventType.NEW_ORDER)
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

    suspend fun updateInCartOrderItem(updatedOrderItem: OrderItem): OrderRepository.OrderRepoResult<Order>? {
        Log.d(TAG, "updateInCartOrderItem")
        //this method used to update orderItems in AdditionalDishesDialog and checkout.
        // get specific orderItem from response and update cart with new orderItemRequest
//        val orderRequest = buildOrderRequest()
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

//        orderRequest.orderItemRequests = cart
//        Log.d(TAG, "updateInCartOrderItem: ${orderRequest.orderItemRequests}")

//        val orderItems = currentOrderRequest?.orderItemRequests?.toMutableList()
//        Log.d(TAG, "orderRequest: $orderRequest")

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
        } else {
            //order already have items therefore update order
            Log.d(TAG, "postNewOrUpdateCart.. updating current order")
            result = postUpdateOrder(orderRequest)
        }
        result?.data?.let {
            updateCartManagerParams(it.copy())
        }
        return result
    }


//    suspend fun addCurrentOrderItemToCart(): OrderRepository.OrderRepoResult<Order> {
//        Log.d(TAG, "addCurrentOrderItemToCart")
//        currentOrderItemRequest?.let {
//            cart.add(0, it)
//            Log.d(TAG, "addNewItemToCart: $currentOrderItemRequest")
//        }
//        val orderRequest = buildOrderRequest()
//
//        val result = orderRepository.postNewOrder(orderRequest)
//        result.data?.let{
//            updateCartParams(result.data)
//        }
//        return result
//    }

//    private fun updateCart(order: Order?) {
//        order?.let {
//            updateCurrentOrderItemRequest(OrderItemRequest(id = it.orderItems[0].id))
//        }
//    }


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
        val deliverAt = DateUtils.parseUnixTimestamp(deliveryTimeLiveData.value?.deliveryDate)
        val deliveryAddressId = feedDataManager.getFinalAddressLiveDataParam().value?.id

        return OrderRequest(
            cookingSlotId = cookingSlotId,
            deliveryAt = deliverAt,
            deliveryAddressId = deliveryAddressId,
            orderItemRequests = tempCart ?: cart
        )
    }


    suspend fun postUpdateOrder(orderRequest: OrderRequest): OrderRepository.OrderRepoResult<Order>? {
        Log.d(TAG, "postUpdateOrder")
        val result = orderRepository.updateOrder(currentOrderResponse!!.id!!, orderRequest)
        result.data?.let {
            updateCartManagerParams(it.copy())
            return result
        }
        return result
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
//            val item = cart.find { it.dishId == orderItem.dish.id }?.apply {
//                id = orderItem.id
//                quantity = orderItem.quantity
//                notes = orderItem.notes
//                removedIngredientsIds = orderItem.getRemovedIngredientsIds()
//            }
            updatedItems.add(item)
        }
        cart.clear()
        cart.addAll(updatedItems)
    }

    fun clearCart() {
        Log.d(TAG, "clearCart")
        cart.clear()
        currentOrderResponse = null
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
            return it.getAdditionalDishes(currentShowingDish?.menuItem?.cookingSlot?.id)
        }
        return null
    }

    fun calcTotalDishesPrice(): Double {
        var total = 0.0
        currentOrderResponse?.orderItems?.let {
            it.forEach {
                total += (it.price.value * it.quantity)
            }
        }
        return total
    }

    fun getTotalPriceForDishQuantity(counter: Int): Double {
        currentShowingDish?.let {
            return it.price.value * counter
        }
        return 0.0
    }

    fun onNewOrderFinish() {
        deliveryTimeManager.rollBackToPreviousDeliveryTime()
    }

    fun isEmpty(): Boolean {
        return cart.isEmpty()
    }

    suspend fun refreshOrderParams(): OrderRepository.OrderRepoResult<Order>? {
        val orderRequest = buildOrderRequest()
        return postUpdateOrder(orderRequest)
    }

    fun onLocationInvalid() {
        //roll back to previous locaiton.. current location doesnt valid for order
        locationManager.rollBackToPreviousAddress()
    }

    fun onDeliveryTimeInvalid() {
        //roll back to previous delivery time.. current time isnt valid for order
        deliveryTimeManager.rollBackToPreviousDeliveryTime()
    }

    suspend fun finalizeOrder(paymentMethodId: String?): OrderRepository.OrderRepoResult<Any>? {
        this.currentOrderResponse?.id?.let { it ->
            return orderRepository.finalizeOrder(it, paymentMethodId)
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
        val deliveryTime = currentShowingDish?.menuItem?.cookingSlot?.startsAt
        val deliveryAtParam = DateUtils.parseUnixTimestamp(deliveryTime)
        val orderRequest = OrderRequest(shippingService = shippingService, deliveryAt = deliveryAtParam)
        postUpdateOrder(orderRequest)
    }

    fun checkShippingMethodValidation(): Boolean {
        return currentOrderResponse?.isNationwide == true && shippingService == null
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