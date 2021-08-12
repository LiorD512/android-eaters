package com.bupp.wood_spoon_eaters.managers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils

class CartManager(
    private val feedDataManager: FeedDataManager,
    private val deliveryTimeManager: DeliveryTimeManager,
    private val orderRepository: OrderRepository,
    private val eventsManager: EventsManager
) {

    // global params -
    private var currentOrderResponse: Order? = null

    var currentCookingSlot: CookingSlot? = null
    val deliverAt = deliveryTimeManager.getTempDeliveryTimeStamp()
    val deliveryAddressId = feedDataManager.getFinalAddressLiveDataParam().value?.id

    private val orderLiveData = MutableLiveData<Order>()
    fun getCurrentCartData() = orderLiveData

    private val wsErrorEvent = LiveEventData<String>()
    fun getWsErrorEvent() = wsErrorEvent


    fun getCurOrderId(): Long {
        currentOrderResponse?.id?.let {
            return it
        }
        return -1
    }

    private fun buildOrderRequest(cart: List<OrderItemRequest>? = null): OrderRequest {
        return OrderRequest(
            cookingSlotId = currentCookingSlot?.id,
            deliveryAt = deliverAt,
            deliveryAddressId = deliveryAddressId,
            orderItemRequests = cart
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

    private val clearCartUiEvent = MutableLiveData<ClearCartEvent>()
    fun getClearCartUiEvent() = clearCartUiEvent
    fun validateCartMatch(newRestaurant: Restaurant, newCookingSlot: CookingSlot): Boolean {
        currentOrderResponse?.let {
            if (it.restaurant!!.id != newRestaurant.id) {
                val curRestaurantName = it.restaurant.restaurantName ?: ""
                val newRestaurantName = newRestaurant.restaurantName ?: ""
                clearCartUiEvent.postValue(ClearCartEvent(ClearCartDialogType.CLEAR_CART_DIFFERENT_RESTAURANT, curRestaurantName, newRestaurantName))
                return false
            }
            if (it.cookingSlot!!.id != newCookingSlot.id) {
                val curCookingSlotStr = DateUtils.parseCookingSlotForNowOrDates(it.cookingSlot)
                val newCookingSlotStr = DateUtils.parseCookingSlotForNowOrDates(newCookingSlot)
                clearCartUiEvent.postValue(ClearCartEvent(ClearCartDialogType.CLEAR_CART_DIFFERENT_COOKING_SLOT, curCookingSlotStr, newCookingSlotStr))
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
    suspend fun addOrUpdateCart(quantity: Int, dishId: Long, note: String?) {
        if (currentOrderResponse == null) {
            //order response is null therefore post new order
            addToCart(quantity, dishId, note)
        } else {
            //order already have exist therefore update current order
            postUpdateOrder(quantity, dishId, note)
        }
    }

    /**
     * this function simply crates new order and adds a new instance of a dish to the cart
     */
    suspend fun addToCart(quantity: Int, dishId: Long, note: String?) {
        val orderRequest = buildOrderRequest(listOf(OrderItemRequest(dishId = dishId, quantity = quantity, notes = note)))
        val result = orderRepository.addNewDish(orderRequest)
        if (result.type == OrderRepository.OrderRepoStatus.ADD_NEW_DISH_SUCCESS) {
            result.data?.let {
                updateCartManagerParams(it.copy())
            }

            val currentAddedDish = result.data!!.orderItems?.find { it.dish.id == dishId }
            eventsManager.logEvent(Constants.EVENT_ADD_DISH, getAddDishData(result.data?.id, currentAddedDish))
        } else {
            //check for errors
            if (result.type == OrderRepository.OrderRepoStatus.WS_ERROR) {
                handleWsError(result.wsError)
            }
        }
    }

    suspend fun postUpdateOrder(quantity: Int, dishId: Long, note: String?) {
        val orderRequest = buildOrderRequest(listOf(OrderItemRequest(dishId = dishId, quantity = quantity, notes = note)))
        currentOrderResponse?.let {
            val result = orderRepository.updateOrder(it.id!!, orderRequest)
            result.data?.let {
                updateCartManagerParams(it.copy())
            }
        }
    }

    /**
     * this functions is called whenever a user swiped out (right) a dish.
     * it updates the order with a "destroyed" orderItems list.
     */
    suspend fun removeOrderItems(dishId: Long) {
        val orderRequest = buildOrderRequest(getDestroyedOrderItemsRequestByDishId(dishId))
        val result = orderRepository.updateOrder(getCurOrderId(), orderRequest)
        if (result.type == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS) {
            result.data?.let {
                updateCartManagerParams(it.copy())
            }
//            val currentAddedDish = result.data!!.orderItems?.find { it.dish.id == dishId }?
//            eventsManager.logEvent(Constants.EVENT_ADD_DISH, getAddDishData(result.data?.id, currentAddedDish))
        } else {
            //check for errors
        }
    }

    /**
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

    //update dish

    /**
     * Global methods
     */

    private fun updateCartManagerParams(order: Order) {
        Log.d(TAG, "updateCartParams")
        this.currentOrderResponse = order
        orderLiveData.postValue(order)
//        refreshCartWithUpdatedOrder(order)

//        Log.d(OldCartManager.TAG, "updated Cart: $cart")
    }

    private fun handleWsError(wsError: List<WSError>?) {
        var errorList = ""
        wsError?.forEach {
            errorList += "${it.msg} \n"
        }
        wsErrorEvent.postRawValue(errorList)
    }


    /**
     * Analytics data calculations
     */
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

    fun hasOpenCartInRestaurant(restaurantId: Long): Boolean {
        return currentOrderResponse?.restaurant?.id == restaurantId
    }

    fun getCurrentOrderOrderItems(): List<OrderItem>? {
        currentOrderResponse.let {
            return it?.orderItems
        }
    }

    private fun getCurrentOrderDishNames(): List<String> {
        val dishNames = mutableSetOf<String>()
        val chefsName = currentOrderResponse?.restaurant?.firstName ?: ""
        currentOrderResponse?.orderItems?.forEach {
            dishNames.add("${chefsName}_${it.dish.name}")
        }
        return dishNames.toList()
    }

    private fun getAddDishData(orderId: Long? = null, orderItem: OrderItem? = null): Map<String, String> {
        val currentDishName = getCurrentDishName(orderItem?.dish)
        val chefsName = getCurrentOrderChefName()
        val chefsId = getCurrentOrderChefId()
        val cuisine = getCurrentOrderChefCuisine()
        val dishId = getCurrentDishId(orderItem?.dish)
        val dishPrice = orderItem?.price?.value
        val data = mutableMapOf<String, String>("cook_name" to chefsName)

        data["cook_id"] = chefsId
        data["dish_id"] = "$dishId"
        data["dish_price"] = dishPrice.toString()
        data["dish_name"] = currentDishName
        if (cuisine.isNotEmpty()) {
            data["cuisine"] = cuisine[0]
        }
        orderId?.let {
            data["order_id"] = orderId.toString()
        }

        return data
    }

    private fun getCurrentDishName(dish: Dish? = null): String {
        dish?.name?.let {
            return it
        }
        return ""
    }

    fun sendFBAdditionalDishEvent(orderItem: OrderItem?) {
        eventsManager.logEvent(Constants.EVENT_ADD_ADDITIONAL_DISH, getAddDishData(orderItem = orderItem))
    }

    private fun getCurrentOrderChefId(): String {
        currentOrderResponse.let {
            return it?.restaurant?.id.toString()
        }
    }

    private fun getCurrentDishPrice(dish: Dish? = null): Double? {
        dish?.let {
            return it.price?.value
        }
        return null
    }

    private fun getCurrentDishId(dish: Dish? = null): Long {
        dish?.let {
            return it.id
        }
        return 0
    }

    fun calcTotalDishesPrice(): Double {
        var total = 0.0
        currentOrderResponse?.orderItems?.let {
            it.forEach {
                total += (it.price.value?.times(it.quantity)!!)
            }
        }
        return total
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

    fun updateCurCookingSlot(currentCookingSlot: CookingSlot) {
        this.currentCookingSlot = currentCookingSlot
    }


    /**
     *  General
     */
    companion object {
        const val TAG = "wowCartManager"
    }
}