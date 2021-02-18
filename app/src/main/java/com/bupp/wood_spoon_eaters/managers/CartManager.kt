package com.bupp.wood_spoon_eaters.managers

import android.util.Log
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.BaseCallback
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils
import java.util.*

class CartManager(
    private val orderRepository: OrderRepository,
    private val feedDataManager: FeedDataManager,
    private val deliveryTimeManager: DeliveryTimeManager
) {

//    private var currentOrderRequest: OrderRequest? = null
    var currentShowingDish: FullDish? = null
    private var currentOrderResponse: Order? = null

    private var currentOrderItem = OrderItemRequest()
    private var currentOrderDeliveryTime: Date? = null
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
//            currentOrderDeliveryTime = deliveryTimeManager.getDeliveryTimeDate()
            updateCurrentOrderItem(OrderItemRequest(dishId = it.id, quantity = 1))
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

    data class CartStatus(val type: CartStatusEventType, val inCartCookName: String? = null, val currentShowingCookName: String? = null)
    enum class CartStatusEventType {
        NEW_ORDER,
        ADD_TO_ORDER_FOR_CURRENT_COOKING_SLOT,
        DIFFERENT_COOKING_SLOT
    }

    //check order status - is there any open order?
    fun getOrderStatus(): CartStatus {
        currentOrderResponse?.let { orderResponse ->
            val currentCookingSlotId = currentShowingDish?.menuItem?.cookingSlot?.id
            val currentShowingCookName = currentShowingDish?.cook?.getFullName()
            val inCartCookingSlot = orderResponse.cookingSlot
            val inCartCookName = orderResponse.cook.getFullName()
            inCartCookingSlot.let { inCartCookingSlot ->
                if (inCartCookingSlot.id != currentCookingSlotId) {
                    //if the showing dish's (cook) is the same as the in-cart order's cook
                    return CartStatus(CartStatusEventType.DIFFERENT_COOKING_SLOT, inCartCookName, currentShowingCookName)
                } else {
                    return CartStatus(CartStatusEventType.ADD_TO_ORDER_FOR_CURRENT_COOKING_SLOT)
                }
            }
        }
        return CartStatus(CartStatusEventType.NEW_ORDER)
    }


    suspend fun addCurrentOrderItemToCart(): OrderRepository.OrderRepoResult<Order> {
        cart.add(0, currentOrderItem)
        Log.d(TAG, "addNewItemToCart: $currentOrderItem")

        val orderRequest = buildOrderRequest()

        val result = orderRepository.postNewOrder(orderRequest)
        this.currentOrderResponse = result.data
        return result
    }

    private fun buildOrderRequest(tempCart: List<OrderItemRequest>? = null): OrderRequest {
        val cookingSlotId = currentShowingDish?.menuItem?.cookingSlot?.id
        val deliverAt = DateUtils.parseUnixTimestamp(currentOrderDeliveryTime)
        val deliveryAddressId = feedDataManager.getFinalAddressLiveDataParam().value?.id

        return OrderRequest(
            cookingSlotId = cookingSlotId,
            deliveryAt = deliverAt,
            deliveryAddressId = deliveryAddressId,
            orderItemRequests = tempCart ?: cart
        )
    }


    fun updateCurrentOrderItem(orderItemRequest: OrderItemRequest) {
        currentOrderItem = OrderItemRequest(
            dishId = orderItemRequest.dishId ?: currentOrderItem.dishId,
            notes = orderItemRequest.notes ?: currentOrderItem.notes,
            quantity = orderItemRequest.quantity ?: currentOrderItem.quantity
        )
        Log.d(TAG, "updateCurrentOrderItem: $currentOrderItem")
    }

    suspend fun addNewOrderItemToCart(orderItemRequest: OrderItemRequest): OrderRepository.OrderRepoResult<Order>? {
        val tempCart = mutableListOf<OrderItemRequest>()
            tempCart.add(0, orderItemRequest)
        val orderRequest = buildOrderRequest(tempCart)
        return postUpdateOrder(orderRequest)
    }


    suspend fun updateInCartOrderItem(updatedOrderItem: OrderItem): OrderRepository.OrderRepoResult<Order>? {
        //this method used to update orderItems in AdditionalDishesDialog and checkout.

//        val updatedOrderItemId = currentOrderResponse?.orderItems?.find { it.id == updatedOrderItem.id }?.id
        val orderRequest = buildOrderRequest()
        for(item in cart){
            if(item.dishId == updatedOrderItem.dish.id){
                item.apply {
                    id = updatedOrderItem.id
                    quantity = updatedOrderItem.quantity
                    notes = updatedOrderItem.notes
                }
            }
        }

        orderRequest.orderItemRequests = cart
        Log.d(TAG, "updateInCartOrderItem: ${orderRequest.orderItemRequests}")

//        val orderItems = currentOrderRequest?.orderItemRequests?.toMutableList()
        Log.d(TAG, "orderRequest: $orderRequest")

        return postUpdateOrder(orderRequest)
        //check emptyCart State - one item in cart and current updated item quantity is 0
//        if (currentOrderResponse?.orderItems?.size == 1 && updatedOrderItem.quantity == 0) {
//            //show empty cart dialog
////            emptyCartEvent.postValue(NewOrderMainViewModel.EmptyCartEvent(shouldShow = true))
//        } else {
//        val tempOrderItems: MutableList<OrderItemRequest> = mutableListOf()
//        for (item in currentOrderResponse?.orderItems!!) {
//            if (item.id == updatedOrderItem.id) {
//                item.quantity = updatedOrderItem.quantity
//            }
//            val removedIngredientIds: ArrayList<Long> = arrayListOf()
//            for (ingItem in item.removedIndredients) {
//                ingItem.id?.let { removedIngredientIds.add(it) }
//            }
//            val updatedOrderItemRequest = OrderItemRequest(item.id, item.dish.id, item.quantity, removedIngredientIds, item.notes)
//            if (item.quantity <= 0) { //this line make sure dishes with 0 quantity will be left outside of the order
//                updatedOrderItemRequest._destroy = true
//            }
//            tempOrderItems.add(updatedOrderItemRequest)
////            }
//
//            orderItems?.clear()
//            orderItems?.addAll(tempOrderItems)
//            return postUpdateOrder(currentOrderRequest!!)
//        }
//        return null
    }

//        progressData.startProgress()
    private suspend fun postUpdateOrder(orderRequest: OrderRequest): OrderRepository.OrderRepoResult<Order>? {
        val result = orderRepository.updateOrder(currentOrderResponse!!.id, orderRequest)
        result.data?.let {
            this.currentOrderResponse = it
            return result
        }
        return null
    }

    fun clearCart() {
        cart.clear()
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
        currentShowingDish?.let{
            return it.price.value * counter
        }
        return 0.0
    }


//    //check if there is an order in cart from diffrent cook
//    fun checkForDifferentOpenOrder(): Boolean {
//        currentOrderResponse?.let{
//            val currentCookingSlotId = currentShowingDish?.menuItem?.cookingSlot?.id
//            val inCartCookingSlot = it.cookingSlot
//            inCartCookingSlot.let { it ->
//                if (it.id != currentCookingSlotId) {
//                    return true
//                }
//            }
//        }
//        return false
//    }

    companion object {
        const val TAG = "wowCartManager"
    }

}