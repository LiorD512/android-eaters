package com.bupp.wood_spoon_eaters.managers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils
import java.util.*

class CartManager(
    private val eaterDataManager: EaterDataManager,
    private val orderRepository: OrderRepository,
    private val eventsManager: EventsManager
) {

    // global params -
    private var currentOrderResponse: Order? = null
    private var currentOrderDeliveryDates: List<DeliveryDates?>? = null

    var currentCookingSlotId: Long? = null
    private fun getAddressId() = eaterDataManager.getCartAddressId()
    private fun getTipPercentage() = currentOrderResponse?.tipPercentage

    private var tempCookingSlotId = LiveEventData<Long>()
    fun getOnCookingSlotIdChange() = tempCookingSlotId

    private val orderLiveData = MutableLiveData<Order?>()
    fun getCurrentOrderData() = orderLiveData

    private val wsErrorEvent = LiveEventData<String>()
    fun getWsErrorEvent() = wsErrorEvent

    data class FloatingCartEvent(val restaurantId: Long, val restaurantName: String, val allOrderItemsQuantity: Int)

    private val floatingCartBtnEvent = MutableLiveData<FloatingCartEvent>()
    fun getFloatingCartBtnEvent() = floatingCartBtnEvent

    fun getCurOrderId(): Long {
        currentOrderResponse?.id?.let {
            return it
        }
        return -1
    }

    private fun buildOrderRequest(cart: List<OrderItemRequest>? = null): OrderRequest {
        return OrderRequest(
            cookingSlotId = currentCookingSlotId,
            deliveryAddressId = currentOrderResponse?.deliveryAddress?.id ?: getAddressId(),
            orderItemRequests = cart,
            tipPercentage = getTipPercentage()?.toFloat()
        )
    }


//    fun addItemRequest(dishId: Long, quantity: Int, note: String? = null) {
//        val orderItemRequest = OrderItemRequest(dishId, quantity = quantity, notes = note)
//        val orderRequest = buildOrderRequest(listOf(orderItemRequest))
//        postOrUpdateOrder(orderRequest)
//    }
//
//
//    suspend fun postNewOrder(orderRequest: OrderRequest) {
//        Log.d(OldCartManager.TAG, "postNewOrUpdateCart.. posting new order")
//        val result = orderRepository.postNewOrder(orderRequest)
//        if (result.type == OrderRepository.OrderRepoStatus.POST_ORDER_SUCCESS) {
//            result.data?.let {
//                orderLiveData.postValue(it)
//            }
//        }
////        eventsManager.logEvent(Constants.EVENT_ADD_DISH, getAddDishData(result.data?.id))
//    }
//
//    fun updateItemRequest(item: DishSectionSingleDish) {
//
//    }


    /**
     * this method is being called when user already have an "active" order.
     * this method checks that the current added dish is part of the same restaurant and
     * that is part of the current cooking slot. if so, returns true, else
     * informs the user with the ui of ClearCart dialog and returns false.
     */
    enum class ClearCartDialogType {
        CLEAR_CART_DIFFERENT_RESTAURANT,
        CLEAR_CART_DIFFERENT_COOKING_SLOT
    }

    data class ClearCartEvent(val dialogType: ClearCartDialogType, val curData: String, val newData: String)

    private val clearCartUiEvent = LiveEventData<ClearCartEvent>()
    fun getClearCartUiEvent() = clearCartUiEvent
    fun validateCartMatch(newRestaurant: Restaurant, newCookingSlotId: Long, newStartAtDate: Date, newEndsAtDate: Date): Boolean {
        currentOrderResponse?.let {
            if (it.restaurant!!.id != newRestaurant.id) {
                val curRestaurantName = it.restaurant.restaurantName ?: ""
                val newRestaurantName = newRestaurant.restaurantName ?: ""
                clearCartUiEvent.postRawValue(ClearCartEvent(ClearCartDialogType.CLEAR_CART_DIFFERENT_RESTAURANT, curRestaurantName, newRestaurantName))
                return false
            }
            if (currentOrderResponse!!.cookingSlot!!.id != newCookingSlotId) {
                val curStartsAtDate = currentOrderResponse!!.cookingSlot!!.orderFrom
                val curEndsAtDate = currentOrderResponse!!.cookingSlot!!.endsAt
                val curCookingSlotStr = DateUtils.parseDatesToNowOrDates(curStartsAtDate, curEndsAtDate)
                val newCookingSlotStr = DateUtils.parseDatesToNowOrDates(newStartAtDate, newEndsAtDate)
                clearCartUiEvent.postRawValue(ClearCartEvent(ClearCartDialogType.CLEAR_CART_DIFFERENT_COOKING_SLOT, curCookingSlotStr, newCookingSlotStr))
                return false
            }
            return true
        }
        return true
    }


    /**
     * this function is being called whenever a user is adding a new dish to the cart.
     * this function checks if this is the first item is the cart - and post a new cart
     * or cart is allready existed and it just updates the cart with the new dish
     */
    suspend fun addOrUpdateCart(quantity: Int, dishId: Long, note: String?): OrderRepository.OrderRepoStatus {
        return if (currentOrderResponse == null) {
            //order response is null therefore post new order
            addDishToNewCart(quantity, dishId, note)
        } else {
            //order already have exist therefore update current order
            addDishToExistingCart(quantity, dishId, note)
        }
    }

    /**
     * this function simply crates new order and adds a new instance of a dish to the cart
     */
    private suspend fun addDishToNewCart(quantity: Int, dishId: Long, note: String?): OrderRepository.OrderRepoStatus {
        val orderRequest = buildOrderRequest(listOf(OrderItemRequest(dishId = dishId, quantity = quantity, notes = note)))
        val result = orderRepository.addNewDish(orderRequest)
        if (result.type == OrderRepository.OrderRepoStatus.ADD_NEW_DISH_SUCCESS) {
            result.data?.let {
                updateCartManagerParams(it.copy())
            }
//            val currentAddedDish = result.data!!.orderItems?.find { it.dish.id == dishId }

        } else {
            //check for errors
            if (result.type == OrderRepository.OrderRepoStatus.WS_ERROR) {
                handleWsError(result.wsError)
            }
        }
        return result.type
    }

    private suspend fun addDishToExistingCart(quantity: Int, dishId: Long, note: String?): OrderRepository.OrderRepoStatus {
        Log.d("orderFlow - cartManager", "addDishToExistingCart")
        val orderRequest = buildOrderRequest(listOf(OrderItemRequest(dishId = dishId, quantity = quantity, notes = note)))
        currentOrderResponse?.let {
            val result = orderRepository.updateOrder(it.id!!, orderRequest)
            if (result.type == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS) {
                result.data?.let {
                    updateCartManagerParams(it.copy())
                }

                //todo - check analytics for updated order.....
//                val currentAddedDish = result.data!!.orderItems?.find { it.dish.id == dishId }
//                eventsManager.logEvent(Constants.EVENT_ADD_DISH, getAddDishData(result.data.id, currentAddedDish))
            } else {
                //check for errors
                if (result.type == OrderRepository.OrderRepoStatus.WS_ERROR) {
                    handleWsError(result.wsError)
                }
            }
            return result.type
        }
        return OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED
    }

    suspend fun updateDishInExistingCart(quantity: Int, note: String?, dishId: Long, orderItemId: Long): OrderRepository.OrderRepoStatus {
        Log.d("orderFlow - cartManager", "updateDishInExistingCart")
//        Log.d(TAG, "updateDishInExistingCart")
        val updatedOrderItem = OrderItemRequest(
            id = orderItemId, dishId = dishId,
            quantity = quantity, notes = note
        )
        val orderRequest = buildOrderRequest(listOf(updatedOrderItem))
        currentOrderResponse?.let {
            val result = orderRepository.updateOrder(it.id!!, orderRequest)
            if (result.type == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS) {
                result.data?.let {
                    updateCartManagerParams(it.copy())
                }
//                val currentAddedDish = result.data!!.orderItems?.find { it.dish.id == dishId }
//                eventsManager.logEvent(Constants.EVENT_UPDATE_DISH, getAddDishData(result.data.id, currentAddedDish))
            } else {
//                check for errors
                if (result.type == OrderRepository.OrderRepoStatus.WS_ERROR) {
                    handleWsError(result.wsError)
                }
            }
            return result.type
        }
        return OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED
    }

    /**
     * this function is used to update order params - ex. deliveryTime, AddressId, Ups, etc.
     * @param orderRequest OrderRequest
     * @param eventType String?
     * @return OrderRepository.OrderRepoResult<Order>?
     */
    suspend fun updateOrderParams(orderRequest: OrderRequest, eventType: String? = null): OrderRepository.OrderRepoResult<Order>? {
        Log.d("orderFlow - cartManager", "updateOrderParams")
        Log.d(TAG, "updateOrderParams")
        currentOrderResponse?.let {
            if(orderRequest.tipPercentage == -1f){
                orderRequest.tipPercentage = null
            }else{
                orderRequest.tipPercentage = orderRequest.tipPercentage ?: getTipPercentage()?.toFloat()
            }
//            orderRequest.tipPercentage = it.tipPercentage?.toFloat()
            val result = orderRepository.updateOrder(it.id!!, orderRequest)
            result.data?.let {
                updateCartManagerParams(it.copy())
                handleEvent(eventType)
                return result
            }
            return result
        }
        return null
    }


    /**
     * this functions is called whenever a user swiped out (right) a dish.
     * it updates the order with a "destroyed" orderItems list.
     * @param dishId = could be dish id or orderItem id
     */
    suspend fun removeOrderItems(dishId: Long, removeSingle: Boolean = false): OrderRepository.OrderRepoStatus {
        Log.d("orderFlow - cartManager", "removeOrderItems")
        var orderRequest: OrderRequest?
        if (removeSingle) {
            orderRequest = buildOrderRequest(getDestroyedOrderItemRequestByOrderIdItem(dishId))
        } else {
            orderRequest = buildOrderRequest(getDestroyedOrderItemsRequestByDishId(dishId))
        }
        val result = orderRepository.updateOrder(getCurOrderId(), orderRequest)
        if (result.type == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS) {
            result.data?.let {
                if (it.orderItems.isNullOrEmpty()) {
                    /**
                     * if cart returns empty (orderItem.size == 0) - clear cart and update floating btn
                     */
                    onCartCleared()
//                    updateCartManagerParams(it.copy())
//                    updateCartManagerParams(it.copy())
//                    updateFloatingCartBtn(it.copy())
                } else {
                    updateCartManagerParams(it.copy())
                }
            }
//            val currentAddedDish = result.data!!.orderItems?.find { it.dish.id == dishId }?
//            eventsManager.logEvent(Constants.EVENT_ADD_DISH, getAddDishData(result.data?.id, currentAddedDish))
        } else {
            //check for errors
        }
        return result.type
    }

    /**
     * called when user swipe out a dish from his cart
     * this function is being used to retrieve list of orderItems based on dish Id for deletion.
     * this function gets dishId and returns a list of OrderItems with _destroy = true
     */
    fun getDestroyedOrderItemsRequestByDishId(dishId: Long): List<OrderItemRequest> {
        currentOrderResponse?.let {
            val destroyedOrderItemRequest = mutableListOf<OrderItemRequest>()
            it.orderItems?.forEach {
                if (it.dish.id == dishId) {
                    val parsed = it.toOrderItemRequest()
                    parsed._destroy = true
                    destroyedOrderItemRequest.add(parsed)
                }
            }
            return destroyedOrderItemRequest.toList()
        }
        return listOf()
    }

    private fun getDestroyedOrderItemRequestByOrderIdItem(orderItemId: Long): List<OrderItemRequest> {
        currentOrderResponse?.let {
            val destroyedOrderItemRequest = mutableListOf<OrderItemRequest>()
            val updatedOrderItem = it.orderItems?.firstOrNull {
                it.id == orderItemId
            }
            updatedOrderItem?.let {
                val parsed = it.toOrderItemRequest()
                parsed._destroy = true
                destroyedOrderItemRequest.add(parsed)
            }
            return destroyedOrderItemRequest.toList()
        }
        return listOf()
    }

    /**
     * Dish Page related functions
     */
    suspend fun getFullDish(menuItemId: Long): OrderRepository.OrderRepoResult<FullDish>? {
        Log.d("orderFlow - cartManager", "getFullDish")
        val result = orderRepository.getFullDish(menuItemId)
        if (result.type == OrderRepository.OrderRepoStatus.FULL_DISH_SUCCESS) {
            return result
        }
        //inspect result log if reach here.
        return null
    }


    fun getQuantityInCart(dishId: Long?): Int {
        var sumQuantity = 0
        currentOrderResponse?.orderItems?.forEach {
            if (it.dish.id == dishId) {
                sumQuantity += it.quantity
            }
        }
        return sumQuantity
    }

    fun getMenuItemsCount(): Int {
        return currentOrderResponse?.orderItems?.size ?: 0
    }

    /** Checkout page related functions
     */

    /**
     * this function returns current order other available cooking slots
     */
    suspend fun fetchOrderDeliveryTimes(orderId: Long?): List<DeliveryDates>? {
        orderId?.let {
            val result = orderRepository.getOrderDeliveryTimes(it)
            if (result.type == OrderRepository.OrderRepoStatus.GET_DELIVERY_DATES_SUCCESS) {
                this.currentOrderDeliveryDates = result.data
                return result.data
            }
        }
        return null
    }

    /**
     * This function is called everytime we call getDeliveryDates (in checkout)
     * this function calculates the current order delivery time based on user selection and server response
     */
    private val deliveryDateUi = MutableLiveData<String>()
    fun getDeliveryDatesUi() = deliveryDateUi
    fun calcCurrentOrderDeliveryTime() {
        Log.d("orderFlowTime", "calcCurrentOrderDeliveryTime")
        currentOrderDeliveryDates?.let { deliveryDates ->
            currentOrderResponse?.let { order ->
                val firstDeliveryDate = deliveryDates[0]!!
                if (order.deliverAt == null) {
                    Log.d("orderFlowTime", "deliverAt == null")
                    /**
                     * when deliver_at is null - it means no change of delivery time made by user.
                     */
                    if (DateUtils.isNowInRange(order.cookingSlot?.startsAt, order.cookingSlot?.endsAt) && DateUtils.isToday(firstDeliveryDate.from)) {
                        Log.d("orderFlowTime", "is now")
                        deliveryDateUi.postValue("ASAP (${DateUtils.parseDateToDayDateAndTime(firstDeliveryDate.from)})")
                    } else {
                        Log.d("orderFlowTime", "first delivery time")
                        deliveryDateUi.postValue(DateUtils.parseDateToDayDateAndTime(firstDeliveryDate.from))
                    }
                } else {
                    val matchedDate = deliveryDates.find {
                        DateUtils.isDateInRange(order.deliverAt, it?.from, it?.to)
                    }
                    if (matchedDate == null) {
                        Log.d("orderFlowTime", "future order but not in a valid delivery time")

                        deliveryDateUi.postValue(DateUtils.parseDateToDayDateAndTime(firstDeliveryDate.from))
                    } else {
                        Log.d("orderFlowTime", "future order ")
                        val isFirst = DateUtils.isSameTime(deliveryDates[0]?.from, matchedDate.from)
                        if (DateUtils.isNowInRange(matchedDate.from, matchedDate.to) && isFirst) {
                            Log.d("orderFlowTime", "is now")
                            deliveryDateUi.postValue("ASAP (${DateUtils.parseDateToDayDateAndTime(order.deliverAt)})")
                        } else {
                            Log.d("orderFlowTime", "first delivery time")
                            deliveryDateUi.postValue(DateUtils.parseDateToDayDateAndTime(order.deliverAt))
                        }
                    }
                }
            }
        }
    }

    /**
     * this function is called when user change address or delivery time
     */
    suspend fun updateOrderDeliveryAddressParam(): OrderRepository.OrderRepoResult<Order>? {
        val orderRequest = buildOrderRequest(emptyList())
        return updateOrderParams(orderRequest)
    }

    fun onLocationInvalid() {
        //roll back to previous locaiton.. current location doesnt valid for order
        eaterDataManager.rollBackToPreviousAddress()
    }

    suspend fun finalizeOrder(paymentMethodId: String?): OrderRepository.OrderRepoResult<Any>? {
        Log.d("orderFlow", "finalizeOrder")
        this.currentOrderResponse?.id?.let { it ->
            val result = orderRepository.finalizeOrder(it, paymentMethodId)
            val isSuccess = result.type == OrderRepository.OrderRepoStatus.FINALIZE_ORDER_SUCCESS
            eventsManager.sendPurchaseEvent(it, calcTotalDishesPrice())
            eventsManager.logEvent(Constants.EVENT_ORDER_PLACED, getOrderValue(isSuccess, result.wsError))
            return result
        }
        return null
    }

    /**
     * Global methods
     */

    private var pendingRequestParam: PendingRequestParam? = null

    data class PendingRequestParam(
        val cookingSlotId: Long,
        val quantity: Int,
        val dishId: Long,
        val note: String?
    )

    fun setPendingRequestParams(cookingSlotId: Long, quantity: Int, dishId: Long, note: String?) {
        pendingRequestParam = PendingRequestParam(cookingSlotId, quantity, dishId, note)
    }

    fun getCurrentCookingSlot(): CookingSlot? {
        currentOrderResponse?.let {
            return it.cookingSlot
        }
        return null
    }

    fun getCurrentDeliveryAt(): Date? {
        return currentOrderResponse?.deliverAt
    }

    fun updateCurCookingSlotId(currentCookingSlotId: Long) {
        //updating the current cooking slot everytime before starting a new cart
        this.currentCookingSlotId = currentCookingSlotId
    }

    /** this functions is called when user starts a new cart from dishPage,
     * forcing cooking slot change will update restaurant page on user return.
     * @param currentCookingSlotId Long
     */
    fun forceCookingSlotChange(currentCookingSlotId: Long) {
        Log.d("orderFlow - cartManager", "forceCookingSlotChange")
        tempCookingSlotId.postRawValue(currentCookingSlotId)
    }


    private fun updateFloatingCartBtn(order: Order?) {
        Log.d("orderFlow - cartManager", "updateFloatingCartBtn: ${order?.getAllOrderItemsQuantity()}")
        floatingCartBtnEvent.postValue(
            FloatingCartEvent(
                order?.restaurant?.id ?: -1,
                order?.restaurant?.restaurantName ?: "",
                order?.getAllOrderItemsQuantity() ?: 0
            )
        )
    }

    fun refreshOrderLiveData() {
        currentOrderResponse?.let {
            Log.d("orderFlow - cartManager", "refreshOrderLiveData")
            orderLiveData.postValue(it)
        }
    }

    /**
     * this function is being called when user decided to clear the cart via ClearCart dialog.
     */
    fun onCartCleared() {
        Log.d("orderFlow - cartManager", "onCartCleared")
        currentDeliveryAt = null
        currentOrderResponse = null
        currentOrderDeliveryDates = null
        orderLiveData.postValue(null)
        updateFloatingCartBtn(null)
//        refreshFloatingCartBtn()
    }

    /**
     * this function is called to add a dish to cart after user accepted clear cart dialog
     * the function checks is there is a pending "add to cart" action,
     * and clearing action after execute.
     * @return OrderRepository.OrderRepoStatus? - status is returned to DishPage to inform page
     * to dismiss it self in case of success.
     */
    suspend fun checkForPendingActions(): OrderRepository.OrderRepoStatus? {
        pendingRequestParam?.let {
            Log.d("orderFlow - cartManager", "checkForPendingActions")
            updateCurCookingSlotId(it.cookingSlotId)
            forceCookingSlotChange(it.cookingSlotId)
            val result = addOrUpdateCart(it.quantity, it.dishId, it.note)
            pendingRequestParam = null
            return result
        }
        return null
    }

    private fun updateCartManagerParams(order: Order?) {
        Log.d(TAG, "updateCartParams")
        this.currentOrderResponse = order
        updateOrderAtParams(order?.deliverAt)
        orderLiveData.postValue(order)
        updateFloatingCartBtn(order)
    }

    private var currentDeliveryAt: Date? = null
    fun updateCurrentDeliveryAt(deliverAt: Date?){
        currentDeliveryAt = deliverAt
    }
    
    private val deliveryAtChangeEvent = LiveEventData<String>()
    fun getDeliveryAtChangeEvent() = deliveryAtChangeEvent
    private fun updateOrderAtParams(deliverAt: Date?) {
        deliverAt?.let{ newDeliveryAt ->
            currentDeliveryAt?.let{ currentDeliveryAt->
                if(currentDeliveryAt != newDeliveryAt){
                    val currentTime = DateUtils.parseDateToUsTime(currentDeliveryAt)
                    val newTime = DateUtils.parseDateToUsTime(newDeliveryAt)
                    deliveryAtChangeEvent.postRawValue("Your delivery time was set to $currentTime, and was changed to $newTime due to a change in the preparation time")
                    this.currentDeliveryAt = newDeliveryAt
                }
            }
        }
    }

    fun handleWsError(wsError: List<WSError>?) {
        var errorList = ""
        wsError?.forEach {
            errorList += "${it.msg} \n"
        }
        wsErrorEvent.postRawValue(errorList)
    }

    /**
     * Ups Data Functions
     */
    var shippingService: String? = null
    suspend fun getUpsShippingRates(): OrderRepository.OrderRepoResult<List<ShippingMethod>>? {
        this.currentOrderResponse?.id?.let { it ->
            return orderRepository.getUpsShippingRates(it)
        }
        return null
    }

    suspend fun updateShippingService(shippingService: String) {
        this.shippingService = shippingService
        val deliveryTime = getCurrentCookingSlot()?.orderFrom
        val deliveryAtParam = DateUtils.parseUnixTimestamp(deliveryTime)
        val orderRequest = OrderRequest(shippingService = shippingService, deliveryAt = deliveryAtParam)
        updateOrderParams(orderRequest)
    }

    fun checkShippingMethodValidation(): Boolean {
        return currentOrderResponse?.isNationwide == true && shippingService == null
    }


    /**
     * Analytics data calculations
     */

    private fun handleEvent(eventType: String?) {
        eventType?.let {
            when (it) {
                Constants.EVENT_CLICK_TIP -> {
                    eventsManager.logEvent(eventType, getTipData())
                }
            }
        }
    }

    private fun getTipData(): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["precentage"] = currentOrderResponse?.tipPercentage.toString()
        data["value"] = currentOrderResponse?.tip?.formatedValue ?: "0"
        data["subtotal_price"] = currentOrderResponse?.subtotal?.formatedValue ?: "0"
        return data
    }

    private fun getOrderValue(isSuccess: Boolean, wsError: List<WSError>? = null): Map<String, Any> {
        val chefsName = getCurrentOrderChefName()
        val chefsId = getCurrentOrderChefId()
        val totalCostStr = calcTotalDishesPrice()
        val dishesName = getCurrentOrderDishNames()
        val cuisine = getCurrentOrderChefCuisine()
        val data =
            mutableMapOf<String, Any>("currency" to "USD", "home_chef_name" to chefsName, "success" to isSuccess.toString())
        data["home_chef_id"] = chefsId
        dishesName.let {
            data["dishes"] = it
        }
        if (cuisine.isNotEmpty()) {
            data["cuisine"] = cuisine
        }

        val isAsap = currentOrderResponse?.deliverAt == null
        data["ASAP"] = isAsap

        if (isSuccess) {
            data["revenue"] = totalCostStr

            currentOrderResponse?.id?.let {
                data["order_id"] = it
            }
            currentOrderResponse?.subtotal?.value?.let {
                data["subtotal_price"] = it
            }
            currentOrderResponse?.discount?.value?.let {
                data["promo_discount"] = it
            }
            currentOrderResponse?.tip?.value?.let {
                data["tip"] = it
            }
            currentOrderResponse?.tax?.value?.let {
                data["tax"] = it
            }
            currentOrderResponse?.deliveryFee?.value?.let {
                data["delivery_fee"] = it
            }
            currentOrderResponse?.cooksServiceFee?.value?.let {
                data["home_chef_service_fee"] = it
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

    fun hasOpenCartInRestaurant(restaurantId: Long): Boolean {
        return currentOrderResponse?.restaurant?.id == restaurantId
    }

    fun getCurrentOrderItems(): List<OrderItem>? {
        currentOrderResponse.let {
            return it?.orderItems
        }
    }

    private fun getCurrentOrderDishNames(): List<String> {
        val dishNames = mutableListOf<String>()
        val chefsName = currentOrderResponse?.restaurant?.firstName ?: ""
        currentOrderResponse?.orderItems?.forEach {
            dishNames.add("${chefsName}_${it.dish.name}")
        }
        return dishNames.toList()
    }


    private fun getCurrentOrderChefId(): String {
        currentOrderResponse.let {
            return it?.restaurant?.id.toString()
        }
    }

    private fun calcTotalDishesPrice(): Double {
        currentOrderResponse?.total?.value?.let {
            return it
        }
        return 0.0
    }

    private fun getCurrentOrderChefCuisine(): List<String> {
        val cuisine = mutableSetOf<String>()
        currentOrderResponse?.orderItems?.forEach {
            if (!it.dish.cuisines.isNullOrEmpty()) {
                it.dish.cuisines[0].let {
                    cuisine.add(it.name)
                }
            }
        }
        return cuisine.toList()
    }

    private fun getCurrentOrderChefName(): String {
        currentOrderResponse.let {
            return it?.restaurant?.getFullName() ?: "no_name"
        }
    }

    fun isCartEmpty(): Boolean {
        return getCurrentOrderItems().isNullOrEmpty()
    }


    /**
     *  General
     */
    companion object {
        const val TAG = "wowCartManager"
    }
}