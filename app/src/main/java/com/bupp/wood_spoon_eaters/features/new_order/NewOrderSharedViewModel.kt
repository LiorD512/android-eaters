package com.bupp.wood_spoon_eaters.features.new_order

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.new_order.service.EphemeralKeyProvider
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.SingleDishViewModel
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.stripe.android.CustomerSession
import com.stripe.android.PaymentConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class NewOrderSharedViewModel(val apiService: ApiService, val metaDataManager: MetaDataManager, val orderManager: OrderManager, val eaterDataManager: EaterDataManager) : ViewModel(),
    EphemeralKeyProvider.EphemeralKeyProviderListener {

    var menuItemId: Long = -1
    var isCheckout: Boolean = false
    var isEvent: Boolean = false

    val progressData = ProgressData()


    fun getListOfAddresses(): java.util.ArrayList<Address>? {
        return eaterDataManager.currentEater!!.addresses
    }

    fun getChosenAddress(): Address?{
        return eaterDataManager.getLastChosenAddress()
    }

    fun updateDeliveryTime(time: Date) {
        progressData.startProgress()
        eaterDataManager.orderTime = time
        val orderRequest = orderManager.getOrderRequest()
        orderRequest.deliveryAt = eaterDataManager.getLastOrderTimeParam()
        postUpdateOrder(orderRequest)
    }

    fun updateAddress(){
        val deliveryAddress = eaterDataManager.getLastChosenAddress()
        orderManager.updateOrderRequest(deliveryAddress = deliveryAddress)
        val orderRequest = orderManager.getOrderRequest()
        postUpdateOrder(orderRequest)
    }

    data class NavigationEvent(val menuItemId: Long = -1, val isCheckout: Boolean = false)
    val navigationEvent = MutableLiveData<NavigationEvent>()
    fun setIntentParams(menuItemId: Long = -1, isCheckout: Boolean = false, isEvent: Boolean) {
        this.menuItemId = menuItemId
        this.isCheckout = isCheckout
        this.isEvent = isEvent
        navigationEvent.postValue(NavigationEvent(menuItemId, isCheckout))
    }

    val orderStatusEvent: SingleLiveEvent<OrderStatusEvent> = SingleLiveEvent()
    data class OrderStatusEvent(val hasActiveOrder: Boolean = false)
    fun checkOrderStatus(){
        if(orderManager.haveCurrentActiveOrder()){
            orderStatusEvent.postValue(OrderStatusEvent(true))
        }else{
            orderStatusEvent.postValue(OrderStatusEvent(false))
        }
    }

    fun initStripe(activity: Activity) {
        PaymentConfiguration.init(metaDataManager.getStripePublishableKey())
        CustomerSession.initCustomerSession(activity, EphemeralKeyProvider(this), false)
    }


    val ephemeralKeyProvider: SingleLiveEvent<EphemeralKeyProviderEvent> = SingleLiveEvent()
    data class EphemeralKeyProviderEvent(val isSuccess: Boolean = false)
    override fun onEphemeralKeyProviderError() {
        ephemeralKeyProvider.postValue(EphemeralKeyProviderEvent(false))
    }


    fun setChosenAddress(address: Address){
        eaterDataManager.setUserChooseSpecificAddress(true)
        eaterDataManager.setLastChosenAddress(address)
    }

//    fun clearCart() {
//        orderManager.clearCurrentOrder()
//    }

    fun loadPreviousDish() {
        navigationEvent.postValue(NavigationEvent(menuItemId = menuItemId))
    }




    //ORDER DATA

    val orderData = MutableLiveData<Order>() //current order result
    val orderRequestData = MutableLiveData<OrderRequest>() // current order request
    val additionalDishes = MutableLiveData<ArrayList<Dish>>() // current order other available dishes

    fun getLastOrderDetails() {
        orderManager.curOrderResponse?.let{
            orderData.postValue(orderManager.curOrderResponse)
        }
    }

    //create new order
    fun initNewOrder(){
        orderManager.clearCurrentOrder()
        orderRequestData.postValue(orderManager.initNewOrder())
    }

//    //get current orser
//    data class OrderDetailsEvent(val order: Order?)
//    val getOrderDetailsEvent: SingleLiveEvent<OrderDetailsEvent> = SingleLiveEvent()
//    fun getOrderDetails() {
//        getOrderDetailsEvent.postValue(OrderDetailsEvent(orderManager.curOrderResponse))
//    }

    var isFirst: Boolean = true
    val showDialogEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()
    data class PostOrderEvent(val isSuccess: Boolean = false, val order: Order?)
    val postOrderEvent: SingleLiveEvent<PostOrderEvent> = SingleLiveEvent()
    //post current order - order details and order items - this method is used to add single dish to cart.
    fun addToCart(fullDish: FullDish? = null, quantity: Int = 1, removedIngredients: ArrayList<Long>? = null, note: String? = null, tipPercentage: Float? = null,
                  tipAmount: String? = null,
                  promoCode: String? = null) {


        val hasPendingOrder = orderManager.haveCurrentActiveOrder()
        if(hasPendingOrder){
            fullDish?.let{
                addNewDishToCart(it.id, quantity)
            }
        }else{
            var cookId: Long = -1
            var dishId: Long = -1
            var cookingSlotId: Long = -1

//            dish?.let {
//                cookId = it.cook.id
//                dishId = it.id
//                it.menuItem?.let {
//                    cookingSlotId = it.cookingSlot?.id
//                }
//            }
            fullDish?.let {
                cookId = it.cook.id
                dishId = it.id
                it.menuItem?.let {
                    cookingSlotId = it.cookingSlot?.id
                }
            }

            val deliveryAddress = eaterDataManager.getLastChosenAddress()
            val orderItem = OrderItemRequest(dishId = dishId, quantity = quantity, removedIndredientsIds = removedIngredients, notes = note)

            orderManager.updateOrderRequest(
                cookId = cookId,
                cookingSlotId = cookingSlotId,
                deliveryAddress = deliveryAddress,
                tipPercentage = tipPercentage,
                tipAmount = tipAmount,
                promoCode = promoCode
            )

            orderManager.addOrderItem(orderItem)
            progressData.startProgress()
            apiService.postOrder(orderManager.getOrderRequest()).enqueue(object : Callback<ServerResponse<Order>> {
                override fun onResponse(call: Call<ServerResponse<Order>>, response: Response<ServerResponse<Order>>) {
                    progressData.endProgress()
                    if (response.isSuccessful) {
                        val order = response.body()?.data
                        orderManager.setOrderResponse(order)
                        orderData.postValue(order)
                        Log.d("wowFeedVM", "postOrder success: ${order.toString()}")
                        postOrderEvent.postValue(PostOrderEvent(true, order))
                        showDialogEvent.postValue(isFirst)
                        isFirst = false
                    } else {
                        Log.d("wowFeedVM", "postOrder fail")
                        postOrderEvent.postValue(PostOrderEvent(false, null))
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Order>>, t: Throwable) {
                    Log.d("wowFeedVM", "postOrder big fail")
                    progressData.endProgress()
                    postOrderEvent.postValue(PostOrderEvent(false, null))
                }
            })
            Log.d("wowSingleDishVM", "addToCart finish")
        }

    }

    fun addNewDishToCart(dishId: Long = -1, quantity: Int? = 1) {
        progressData.startProgress()

        val newOrderItem = OrderItemRequest(dishId = dishId, quantity = quantity)

        val orderRequest = orderManager.getOrderRequest()
        orderRequest.orderItemRequests?.clear()
        orderRequest.orderItemRequests?.add(newOrderItem)

        apiService.updateOrder(orderManager.curOrderResponse!!.id, orderRequest)
            .enqueue(object : Callback<ServerResponse<Order>> {
                override fun onResponse(call: Call<ServerResponse<Order>>, response: Response<ServerResponse<Order>>) {
                    progressData.endProgress()
                    if (response.isSuccessful) {
                        val updatedOrder = response.body()?.data
                        orderManager.setOrderResponse(updatedOrder)
                        orderData.postValue(updatedOrder)
                        //getOrderDetails()
                    } else {
                        Log.d("wowCheckoutVm", "updateOrder FAILED")
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Order>>, t: Throwable) {
                    progressData.endProgress()
                    Log.d("wowCheckoutVm", "updateOrder big FAILED")
                }
            })
        Log.d("wowSingleDishVM", "addNewDishToCart finish")
    }

    //update order - items quantity
    fun updateOrder(updatedOrderItem: OrderItem) {
        val orderResponse = orderManager.curOrderResponse

        val orderRequest = orderManager.getOrderRequest()
        val orderItems = orderRequest.orderItemRequests

        val tempOrderItems: ArrayList<OrderItemRequest> = arrayListOf()
            for (item in orderResponse?.orderItems!!) {
                if (item.id == updatedOrderItem.id) {
                    item.quantity = updatedOrderItem.quantity
                }
                val removedIngredientIds: ArrayList<Long> = arrayListOf()
                for (ingItem in item.removedIndredients){
                    ingItem.id?.let { removedIngredientIds.add(it) }
                }
                val updatedOrderItemRequest = OrderItemRequest(item.id, item.dish.id, item.quantity, removedIngredientIds, item.notes)
                if(item.quantity <= 0){ //this line make sure dishes with 0 quantitny will be left outside of the order
                    updatedOrderItemRequest._destroy = true
                }
                tempOrderItems.add(updatedOrderItemRequest)
        }

        orderItems!!.clear()
        orderItems.addAll(tempOrderItems)

        postUpdateOrder(orderRequest)
    }

    private fun postUpdateOrder(orderRequest: OrderRequest) {
        progressData.startProgress()
        apiService.updateOrder(orderManager.curOrderResponse!!.id, orderRequest)
            .enqueue(object : Callback<ServerResponse<Order>> {
                override fun onResponse(call: Call<ServerResponse<Order>>, response: Response<ServerResponse<Order>>) {
                    progressData.endProgress()
                    if (response.isSuccessful) {
                        val updatedOrder = response.body()?.data
                        orderManager.setOrderResponse(updatedOrder)
                        orderData.postValue(updatedOrder)
                        //getOrderDetails()
                    } else {
                        Log.d("wowCheckoutVm", "updateOrder FAILED")
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Order>>, t: Throwable) {
                    progressData.endProgress()
                    Log.d("wowCheckoutVm", "updateOrder big FAILED")
                }
            })
    }

    fun calcTotalDishesPrice(): Double {
        var total = 0.0
        orderData.value?.orderItems?.let{
            it.forEach {
                total += (it.price?.value * it.quantity)
            }
        }
        return total
    }

    val hasOpenOrder: SingleLiveEvent<HasOpenOrder> = SingleLiveEvent()
    data class HasOpenOrder(val hasOpenOrder: Boolean, val cookInCart: Cook? = null, val currentShowingCook: Cook? = null)
    //check order status - is there any open order?
    fun checkForOpenOrder(currentShowingCook: Cook) {
        if (orderManager.haveCurrentActiveOrder()) {
            val inCartOrder = orderManager.curOrderResponse
            val cookInCart = inCartOrder?.cook
            if (currentShowingCook.id != cookInCart?.id) {
                //if the showing dish's (cook) is the same as the in-cart order's cook
                hasOpenOrder.postValue(HasOpenOrder(true, cookInCart, currentShowingCook))
            } else {
                hasOpenOrder.postValue(HasOpenOrder(false))
            }
        } else {
            hasOpenOrder.postValue(HasOpenOrder(false))
        }
    }


    val checkCartStatus: SingleLiveEvent<CheckCartStatusEvent> = SingleLiveEvent()
    data class CheckCartStatusEvent(val hasPendingOrder: Boolean)
    fun checkCartStatus() {
        val hasPendingOrder = orderManager.haveCurrentActiveOrder()
        checkCartStatus.postValue(CheckCartStatusEvent(hasPendingOrder))
    }


    data class CheckoutEvent(val isSuccess: Boolean)
    val checkoutOrderEvent: SingleLiveEvent<CheckoutEvent> = SingleLiveEvent()
    //checkout order
    fun checkoutOrder(orderId: Long) {
        progressData.startProgress()
        apiService.checkoutOrder(orderId, eaterDataManager.getCustomerCardId())
            .enqueue(object : Callback<ServerResponse<Void>> {
                override fun onResponse(call: Call<ServerResponse<Void>>, response: Response<ServerResponse<Void>>) {
                    progressData.endProgress()
                    if (response.isSuccessful) {
                        checkoutOrderEvent.postValue(CheckoutEvent(true))
                        orderManager.clearCurrentOrder()
                    } else {
                        checkoutOrderEvent.postValue(CheckoutEvent(false))
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
                    progressData.endProgress()
                    checkoutOrderEvent.postValue(CheckoutEvent(false))
                }
            })
    }


    //clear order
    fun clearCart() {
        orderManager.clearCurrentOrder()
    }



    //Additional single params

    fun resetTip(){
        tipInDollars.postValue(0)
        tipPercentage.postValue(0)
    }

    val tipInDollars = MutableLiveData<Int>()
    val tipPercentage = MutableLiveData<Int>()
//    val orderData = MutableLiveData<Order>()
    fun updateTipPercentage(tipPercentage: Int) {
        this.tipPercentage.postValue(tipPercentage)
    }

    fun updateTipInDollars(tipInDollars: Int) {
        this.tipInDollars.postValue(tipInDollars)
    }

//    fun getTempTipPercent(): Int {
//        return orderManager.tempTipPercentage
//    }
//
//    fun getTempTipInDollars(): Int {
//        return orderManager.tempTipInDollars
//    }

    fun updateAddUtensils(shouldAdd: Boolean){
        orderManager.updateOrderRequest(addUtensils = shouldAdd)
    }

    fun updateRecurringOrder(isRecurring: Boolean){
        orderManager.updateOrderRequest(recurringOrder = isRecurring)
    }

    fun setAdditionalDishes(dishes: ArrayList<Dish>) {
        val availableArr = arrayListOf<Dish>()
        //get only available dishes
        dishes.forEach { dish ->
            dish.menuItem?.let{
                availableArr.add(dish)
            }

        }
        additionalDishes.postValue(availableArr)
    }

    data class EditDeliveryTime(val startAt: Date?, val endsAt: Date?)
    val editDeliveryTime: SingleLiveEvent<EditDeliveryTime> = SingleLiveEvent()
    fun editDeliveryTime() {
        val availableCookingSlotStartsAt = orderData.value?.cookingSlot?.startsAt
        val availableCookingSlotEndsAt = orderData.value?.cookingSlot?.endsAt
        editDeliveryTime.postValue(EditDeliveryTime(availableCookingSlotStartsAt, availableCookingSlotEndsAt))
    }


}
