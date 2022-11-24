package com.bupp.wood_spoon_eaters.managers

import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.features.upsale.data_source.repository.UpSaleData
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.features.upsale.data_source.repository.UpSaleRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.stripe.android.model.PaymentMethod
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

data class ClearCartEvent(
    val dialogType: ClearCartDialogType,
    val curData: String,
    val newData: String
)

data class FloatingCartEvent(
    val restaurantId: Long,
    val restaurantName: String,
    val allOrderItemsQuantity: Int
)

enum class ClearCartDialogType {
    CLEAR_CART_DIFFERENT_RESTAURANT,
    CLEAR_CART_DIFFERENT_COOKING_SLOT
}

class CartManager(
    private val eaterDataManager: EaterDataManager,
    private val orderRepository: OrderRepository,
    private val upSaleRepository: UpSaleRepository,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker
) {
    private var currentOrderResponse: Order? = null
    private var currentOrderDeliveryDates: List<DeliveryDates?>? = null
    var currentCookingSlotId: Long? = null
    var shippingService: String? = null

    private var tempCookingSlotId = LiveEventData<Long>()
    private val clearCartUiEvent = LiveEventData<ClearCartEvent>()
    private val orderLiveData = MutableLiveData<Order?>()
    private val wsErrorEvent = LiveEventData<String>()
    private val floatingCartBtnEvent = MutableLiveData<FloatingCartEvent>()
    private val deliveryDateUi = MutableLiveData<String>()

    private fun getAddressId() = eaterDataManager.getCartAddressId()
    private fun getTipPercentage() = currentOrderResponse?.tipPercentage

    fun getCurrentOrderData() = orderLiveData
    fun getWsErrorEvent() = wsErrorEvent
    fun getFloatingCartBtnEvent() = floatingCartBtnEvent
    fun getOnCookingSlotIdChange() = tempCookingSlotId
    fun getClearCartUiEvent() = clearCartUiEvent
    fun getDeliveryDatesUi() = deliveryDateUi

    init {
        observeOrderLiveDataAndUpdateUpSaleItems()
    }

    private fun observeOrderLiveDataAndUpdateUpSaleItems(){
        orderLiveData.observeForever(){
            if (it != null){
                GlobalScope.launch {
                    upSaleRepository.fetchUpSaleItemsRemote(it.id)
                }
            }
        }
    }

    fun getUpSaleItems(): UpSaleData?{
        return upSaleRepository.getUpSaleItemsLocally(getCurOrderId())
    }

    fun getCurOrderId(): Long {
        currentOrderResponse?.id?.let {
            return it
        }
        return -1
    }

    private fun buildOrderRequest(cart: List<OrderItemRequest>? = null, selectedAddress: Address? = null): OrderRequest {
        return OrderRequest(
            cookingSlotId = currentCookingSlotId,
            deliveryAddressId = selectedAddress?.id ?: getAddressId(),
            orderItemRequests = cart,
            tipPercentage = getTipPercentage()?.toFloat()
        )
    }

    fun validateCartMatch(
        newRestaurant: Restaurant,
        newCookingSlotId: Long,
        newStartAtDate: Date,
        newEndsAtDate: Date
    ): Boolean {
        currentOrderResponse?.let {
            if (it.restaurant!!.id != newRestaurant.id) {
                val curRestaurantName = it.restaurant.restaurantName ?: ""
                val newRestaurantName = newRestaurant.restaurantName ?: ""
                clearCartUiEvent.postRawValue(
                    ClearCartEvent(
                        ClearCartDialogType.CLEAR_CART_DIFFERENT_RESTAURANT,
                        curRestaurantName,
                        newRestaurantName
                    )
                )
                return false
            }
            if (currentOrderResponse!!.cookingSlot!!.id != newCookingSlotId) {
                val curStartsAtDate = currentOrderResponse!!.cookingSlot!!.orderFrom
                val curEndsAtDate = currentOrderResponse!!.cookingSlot!!.endsAt
                val curCookingSlotStr =
                    DateUtils.parseDatesToNowOrDates(curStartsAtDate, curEndsAtDate)
                val newCookingSlotStr =
                    DateUtils.parseDatesToNowOrDates(newStartAtDate, newEndsAtDate)
                clearCartUiEvent.postRawValue(
                    ClearCartEvent(
                        ClearCartDialogType.CLEAR_CART_DIFFERENT_COOKING_SLOT,
                        curCookingSlotStr,
                        newCookingSlotStr
                    )
                )
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
    suspend fun addOrUpdateCart(
        quantity: Int,
        dishId: Long,
        note: String?
    ): OrderRepository.OrderRepoStatus {
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
    private suspend fun addDishToNewCart(
        quantity: Int,
        dishId: Long,
        note: String?
    ): OrderRepository.OrderRepoStatus {
        val orderRequest = buildOrderRequest(
            listOf(
                OrderItemRequest(
                    dishId = dishId,
                    quantity = quantity,
                    notes = note
                )
            )
        )
        val result = orderRepository.addNewDish(orderRequest)
        if (result.type == OrderRepository.OrderRepoStatus.ADD_NEW_DISH_SUCCESS) {
            result.data?.let {
                updateCartManagerParams(it.copy())
            }
        } else {
            if (result.type == OrderRepository.OrderRepoStatus.WS_ERROR) {
                handleWsError(result.wsError)
            }
        }
        return result.type
    }

    private suspend fun addDishToExistingCart(
        quantity: Int,
        dishId: Long,
        note: String?
    ): OrderRepository.OrderRepoStatus {
        val orderRequest = buildOrderRequest(
            listOf(
                OrderItemRequest(
                    dishId = dishId,
                    quantity = quantity,
                    notes = note
                )
            )
        )
        currentOrderResponse?.let {
            val result = orderRepository.updateOrder(it.id!!, orderRequest)
            if (result.type == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS) {
                result.data?.let {
                    updateCartManagerParams(it.copy())
                }
            } else {
                if (result.type == OrderRepository.OrderRepoStatus.WS_ERROR) {
                    handleWsError(result.wsError)
                }
            }
            return result.type
        }
        return OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED
    }

    suspend fun updateDishInExistingCart(
        quantity: Int,
        note: String?,
        dishId: Long,
        orderItemId: Long
    ): OrderRepository.OrderRepoStatus {
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
            } else {
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
    suspend fun updateOrderParams(
        orderRequest: OrderRequest,
        eventType: String? = null
    ): OrderRepository.OrderRepoResult<Order>? {
        currentOrderResponse?.let {
            if (orderRequest.tipPercentage == -1f) {
                orderRequest.tipPercentage = null
            } else {
                orderRequest.tipPercentage =
                    orderRequest.tipPercentage ?: getTipPercentage()?.toFloat()
            }
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
     * this function is used to update order gifting params
     * @param orderGiftRequest OrderGiftRequest
     * @param eventType String?
     * @return OrderRepository.OrderRepoResult<Order>?
     */
    suspend fun updateOrderGiftParams(
        orderGiftRequest: OrderGiftRequest,
        eventType: String? = null
    ): OrderRepository.OrderRepoResult<Order>? {
        currentOrderResponse?.id?.let { orderId ->
            val result = orderRepository.updateOrderGift(orderId, orderGiftRequest)
            result.data?.let { resultOrder ->
                updateCartManagerParams(resultOrder.copy())
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
    suspend fun removeOrderItems(
        dishId: Long,
        removeSingle: Boolean = false
    ): OrderRepository.OrderRepoStatus {
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
                } else {
                    updateCartManagerParams(it.copy())
                }
            }
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
    suspend fun getFullDishByMenuItem(menuItemId: Long): OrderRepository.OrderRepoResult<FullDish>? {
        val result = orderRepository.getFullDishByMenuItem(menuItemId)
        if (result.type == OrderRepository.OrderRepoStatus.FULL_DISH_SUCCESS) {
            return result
        }

        return null
    }

    suspend fun getFullDishByDish(dishId: Long): OrderRepository.OrderRepoResult<FullDish>? {
        val result = orderRepository.getFullDishByDish(dishId)
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

    fun calcCurrentOrderDeliveryTime() {
        currentOrderDeliveryDates?.let { deliveryDates ->
            currentOrderResponse?.let { order ->
                val firstDeliveryDate = deliveryDates[0]!!
                if (order.deliverAt == null) {
                    /**
                     * when deliver_at is null - it means no change of delivery time made by user.
                     */
                    if (DateUtils.isNowInRange(
                            order.cookingSlot?.startsAt,
                            order.cookingSlot?.endsAt
                        ) && DateUtils.isToday(firstDeliveryDate.from)
                    ) {
                        deliveryDateUi.postValue(
                            "ASAP (${
                                DateUtils.parseDateToDayDateAndTime(
                                    firstDeliveryDate.from
                                )
                            })"
                        )
                    } else {
                        deliveryDateUi.postValue(
                            DateUtils.parseDateToDayDateAndTime(
                                firstDeliveryDate.from
                            )
                        )
                    }
                } else {
                    val matchedDate = deliveryDates.find {
                        DateUtils.isDateInRange(order.deliverAt, it?.from, it?.to)
                    }
                    if (matchedDate == null) {

                        deliveryDateUi.postValue(
                            DateUtils.parseDateToDayDateAndTime(
                                firstDeliveryDate.from
                            )
                        )
                    } else {
                        val isFirst = DateUtils.isSameTime(deliveryDates[0]?.from, matchedDate.from)
                        if (DateUtils.isNowInRange(matchedDate.from, matchedDate.to) && isFirst) {
                            deliveryDateUi.postValue(
                                "ASAP (${
                                    DateUtils.parseDateToDayDateAndTime(
                                        order.deliverAt
                                    )
                                })"
                            )
                        } else {
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
    suspend fun updateOrderDeliveryAddressParam(selectedAddress: Address? = null): OrderRepository.OrderRepoResult<Order>? {
        val orderRequest = buildOrderRequest(emptyList(), selectedAddress)
        return updateOrderParams(orderRequest)
    }

    fun onLocationInvalid() {
        //roll back to previous locaiton.. current location doesnt valid for order
        eaterDataManager.rollBackToPreviousAddress()
    }

    suspend fun finalizeOrder(paymentMethod: PaymentMethod?): OrderRepository.OrderRepoResult<Any>? {
        this.currentOrderResponse?.id?.let { it ->
            val result = orderRepository.finalizeOrder(it, paymentMethod?.id)
            val isSuccess = result.type == OrderRepository.OrderRepoStatus.FINALIZE_ORDER_SUCCESS
            eatersAnalyticsTracker.sendPurchaseEvent(it, calcTotalDishesPrice())
            if (eaterDataManager.currentEater?.ordersCount == 0){
                eatersAnalyticsTracker.sendFirstPurchaseEvent(it, calcTotalDishesPrice())
            }
            eatersAnalyticsTracker.logEvent(Constants.EVENT_ORDER_PLACED, getOrderValue(paymentMethod, isSuccess, result.wsError))
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
        tempCookingSlotId.postRawValue(currentCookingSlotId)
    }


    private fun updateFloatingCartBtn(order: Order?) {
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
            orderLiveData.postValue(it)
        }
    }

    /**
     * this function is being called when user decided to clear the cart via ClearCart dialog.
     */
    fun onCartCleared() {
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
            updateCurCookingSlotId(it.cookingSlotId)
            forceCookingSlotChange(it.cookingSlotId)
            val result = addOrUpdateCart(it.quantity, it.dishId, it.note)
            pendingRequestParam = null
            return result
        }
        return null
    }

    private fun updateCartManagerParams(order: Order?) {
        this.currentOrderResponse = order
        updateOrderAtParams(order?.deliverAt)
        orderLiveData.postValue(order)
        updateFloatingCartBtn(order)
    }

    private var currentDeliveryAt: Date? = null
    fun updateCurrentDeliveryAt(deliverAt: Date?) {
        currentDeliveryAt = deliverAt
    }

    private val deliveryAtChangeEvent = LiveEventData<String>()
    fun getDeliveryAtChangeEvent() = deliveryAtChangeEvent
    private fun updateOrderAtParams(deliverAt: Date?) {
        deliverAt?.let { newDeliveryAt ->
            currentDeliveryAt?.let { currentDeliveryAt ->
                if (currentDeliveryAt != newDeliveryAt) {
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
        val orderRequest =
            OrderRequest(shippingService = shippingService, deliveryAt = deliveryAtParam)
        updateOrderParams(orderRequest)
    }

    fun checkShippingMethodValidation(): Boolean {
        return currentOrderResponse?.isNationwide == true && shippingService == null
    }

    private fun handleEvent(eventType: String?) {
        eventType?.let {
            when (it) {
                Constants.EVENT_CLICK_TIP -> {
                    eatersAnalyticsTracker.logEvent(eventType, getTipData())
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

    private fun getOrderValue(
        paymentMethod: PaymentMethod?,
        isSuccess: Boolean,
        wsError: List<WSError>? = null
    ): Map<String, Any> {
        val chefsName = getCurrentOrderChefName()
        val chefsId = getCurrentOrderChefId()
        val totalCostStr = calcTotalDishesPrice()
        val dishesName = getCurrentOrderDishNames()
        val cuisine = getCurrentOrderChefCuisine()

        val data = mutableMapOf<String, Any>(
            "currency" to "USD",
            "home_chef_name" to chefsName,
            "success" to isSuccess.toString(),
        )

        paymentMethod?.let {
            if (it.type?.name == "Card") {
                data["payment_method"] = "CC"
            }
        }

        data["home_chef_id"] = chefsId
        data["dishes"] = dishesName
        data["cooking_slot_id"] = currentCookingSlotId ?: -1

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

    fun getCurrentOrder(): Order? {
        return currentOrderResponse
    }
}