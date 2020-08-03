package com.bupp.wood_spoon_eaters.features.new_order

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.new_order.service.EphemeralKeyProvider
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.BaseCallback
import com.stripe.android.model.PaymentMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class NewOrderSharedViewModel(
    val apiService: ApiService,
    val metaDataManager: MetaDataManager,
    val orderManager: OrderManager,
    val eaterDataManager: EaterDataManager,
    val eventsManager: EventsManager,
    val paymentManager: PaymentManager
) : ViewModel(),
    EphemeralKeyProvider.EphemeralKeyProviderListener {

    private var fullDishData: FullDish? = null
    private var curCookingSlotId: Long? = null
    var selectedShippingMethod: ShippingMethod? = null
    var menuItemId: Long = -1
    var isCheckout: Boolean = false
    var isEvent: Boolean = false

    val progressData = ProgressData()
    val errorEvent: SingleLiveEvent<List<WSError>> = SingleLiveEvent()


//    fun getListOfAddresses(): java.util.ArrayList<Address>? {
//        return eaterDataManager.currentEater!!.addresses
//    }
//
//    fun getChosenAddress(): Address? {
//        return eaterDataManager.getLastChosenAddress()
//    }

    fun updateDeliveryTime(time: Date) {
//        progressData.startProgress()
        eaterDataManager.orderTime = time

//        orderManager.updateOrderRequest(deliveryAt = eaterDataManager.getLastOrderTimeParam())
        orderData.postValue(orderData.value)
        val orderRequest = OrderRequest()
        orderRequest.deliveryAt = eaterDataManager.getLastOrderTimeParam()
        postUpdateOrder(orderRequest)
    }

    fun updateAddress() {
//        orderManager.updateOrderRequest(deliveryAddress = deliveryAddress)
//        orderData.postValue(orderData.value)
        val deliveryAddress = eaterDataManager.getLastChosenAddress()
        deliveryAddress?.let {
            val orderRequest = OrderRequest()
            orderRequest.deliveryAddressId = it.id
            postUpdateOrder(orderRequest)
        }
    }

    data class EmptyCartEvent(val shouldShow: Boolean = false)

    val emptyCartEvent = SingleLiveEvent<EmptyCartEvent>()

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

    fun checkOrderStatus() {
        if (orderManager.haveCurrentActiveOrder()) {
            orderStatusEvent.postValue(OrderStatusEvent(true))
        } else {
            orderStatusEvent.postValue(OrderStatusEvent(false))
        }
    }

    fun initStripe(activity: Activity) {
        //todo - fix this
//        PaymentConfiguration.init(activity, metaDataManager.getStripePublishableKey())
//        CustomerSession.initCustomerSession(activity, EphemeralKeyProvider(this), false)
    }


    val ephemeralKeyProvider: SingleLiveEvent<EphemeralKeyProviderEvent> = SingleLiveEvent()

    data class EphemeralKeyProviderEvent(val isSuccess: Boolean = false)

    override fun onEphemeralKeyProviderError() {
        ephemeralKeyProvider.postValue(EphemeralKeyProviderEvent(false))
    }

    fun setChosenAddress(address: Address) {
        eaterDataManager.setUserChooseSpecificAddress(true)
        eaterDataManager.setLastChosenAddress(address)
    }

    fun loadPreviousDish() {
        navigationEvent.postValue(NavigationEvent(menuItemId = menuItemId))
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

    //create new order
    fun initNewOrder() {
        orderManager.clearCurrentOrder()
        orderRequestData.postValue(orderManager.initNewOrder())
    }

    var isFirstPurchase: Boolean = true
    val showDialogEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val procceedToCheckoutEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()

    data class PostOrderEvent(val isSuccess: Boolean = false, val order: Order?)

    val postOrderEvent: SingleLiveEvent<PostOrderEvent> = SingleLiveEvent()

    //post current order - order details and order items - this method is used to add single dish to cart.
    fun addToCart(
        fullDish: FullDish? = null, quantity: Int = 1, removedIngredients: ArrayList<Long>? = null, note: String? = null, tipPercentage: Float? = null,
        tipAmount: String? = null,
        promoCode: String? = null
    ) {
        Log.d("wowNewOrderVM", "addToCart START")
        val hasPendingOrder = orderManager.haveCurrentActiveOrder()
        val hasPendingOrderFromDifferentCook = checkForDifferentOpenOrder(fullDish?.menuItem?.cookingSlot?.id)
        if (hasPendingOrder) {
            if (hasPendingOrderFromDifferentCook) {
                fullDish?.let {
                    checkForOpenOrderAndShowClearCartDialog(fullDish.menuItem?.cookingSlot?.id, fullDish.cook.getFullName())
                }
            }else{
                addNewDishToCart(fullDish!!.id, quantity)
            }
        } else {
            var cookId: Long = -1
            var dishId: Long = -1
            var cookingSlotId: Long = -1

            fullDish?.let {
                cookId = it.cook.id
                dishId = it.id
                it.menuItem?.let {
                    cookingSlotId = it.cookingSlot.id
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
                        curCookingSlotId = order?.cookingSlot?.id
                        initAdditionalDishDialog() //it will be more alegant if server will return the right list of additional dishes instead of client do that. todo - incase of major refactor
                        orderManager.setOrderResponse(order)
                        orderData.postValue(order)
                        Log.d("wowNewOrderVM", "postOrder success: ${order.toString()}")
                        showAdditionalDialogOrProcceedToCheckout()
                        //update additional dishes
//                        fullDish?.let {
//                            setAdditionalDishes(it.getAdditionalDishes(curCookingSlotId))
//                            if (it.getAdditionalDishes().size > 1) {
//                                showDialogEvent.postValue(isFirst)
//                                isFirst = false
//                            } else {
//                                showDialogEvent.postValue(false)
//                            }
//                        }
                        postOrderEvent.postValue(PostOrderEvent(true, order))

                        eventsManager.sendAddToCart(order?.id)


                    } else {
                        Log.d("wowNewOrderVM", "postOrder fail")
                        postOrderEvent.postValue(PostOrderEvent(false, null))
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Order>>, t: Throwable) {
                    Log.d("wowNewOrderVM", "postOrder big fail")
                    progressData.endProgress()
                    postOrderEvent.postValue(PostOrderEvent(false, null))
                }
            })
            Log.d("wowNewOrderVM", "addToCart finish")
        }

    }

    fun initAdditionalDishDialog() {
        if(isFirstPurchase) {
            curCookingSlotId?.let {
                fullDishData?.let {
                    setAdditionalDishes(it.getAdditionalDishes(curCookingSlotId))
                }
            }
        }
    }

    fun showAdditionalDialogOrProcceedToCheckout() {
        if (fullDishData?.cook?.dishes != null) {
            if (fullDishData?.cook?.dishes!!.size > 1 && isFirstPurchase) {
                showDialogEvent.postValue(isFirstPurchase)
                isFirstPurchase = false
            } else {
                procceedToCheckoutEvent.postValue(true)
            }
        } else {
            procceedToCheckoutEvent.postValue(true)
        }
    }

    fun showAdditionalDialogIfFirst() {
        if (fullDishData?.cook?.dishes != null) {
            if (fullDishData?.cook?.dishes!!.size > 1 && isFirstPurchase) {
                showDialogEvent.postValue(isFirstPurchase)
                isFirstPurchase = false
            }
        }
    }


    fun addNewDishToCart(dishId: Long = -1, quantity: Int? = 1) {
        Log.d("wowNewOrderVM", "addNewDishToCart START")
        progressData.startProgress()

        val newOrderItem = OrderItemRequest(dishId = dishId, quantity = quantity)

        val orderRequest = orderManager.getOrderRequest()

        orderRequest.orderItemRequests?.clear()
        orderRequest.orderItemRequests?.add(newOrderItem)
        val orderId = orderManager.curOrderResponse!!.id
        apiService.updateOrder(orderId, orderRequest)
            .enqueue(object : Callback<ServerResponse<Order>> {
                override fun onResponse(call: Call<ServerResponse<Order>>, response: Response<ServerResponse<Order>>) {
                    progressData.endProgress()
                    if (response.isSuccessful) {
                        val updatedOrder = response.body()?.data
                        curCookingSlotId = updatedOrder?.cookingSlot?.id
                        initAdditionalDishDialog()
                        orderManager.setOrderResponse(updatedOrder)
                        orderData.postValue(updatedOrder)
                        showAdditionalDialogIfFirst()
                    } else {
                        Log.d("wowNewOrderVM", "updateOrder FAILED")
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Order>>, t: Throwable) {
                    progressData.endProgress()
                    Log.d("wowNewOrderVM", "updateOrder big FAILED")
                }
            })
        Log.d("wowNewOrderVM", "addNewDishToCart finish")
    }

    //update order - items quantity
    fun updateOrder(updatedOrderItem: OrderItem) {
        val orderResponse = orderManager.curOrderResponse

        val orderRequest = orderManager.getOrderRequest()
        val orderItems = orderRequest.orderItemRequests

        //check emptyCart State - one item in cart and current updated item quantity is 0
        if (orderResponse?.orderItems?.size == 1 && updatedOrderItem.quantity == 0) {
            //show empty cart dialog
            emptyCartEvent.postValue(EmptyCartEvent(shouldShow = true))
        } else {
            val tempOrderItems: ArrayList<OrderItemRequest> = arrayListOf()
            for (item in orderResponse?.orderItems!!) {
                if (item.id == updatedOrderItem.id) {
                    item.quantity = updatedOrderItem.quantity
                }
                val removedIngredientIds: ArrayList<Long> = arrayListOf()
                for (ingItem in item.removedIndredients) {
                    ingItem.id?.let { removedIngredientIds.add(it) }
                }
                val updatedOrderItemRequest = OrderItemRequest(item.id, item.dish.id, item.quantity, removedIngredientIds, item.notes)
                if (item.quantity <= 0) { //this line make sure dishes with 0 quantitny will be left outside of the order
                    updatedOrderItemRequest._destroy = true
                }
                tempOrderItems.add(updatedOrderItemRequest)
            }

            orderItems!!.clear()
            orderItems.addAll(tempOrderItems)

            postUpdateOrder(orderRequest)
        }

    }


    val updateOrderError: SingleLiveEvent<Int> = SingleLiveEvent()
    private fun postUpdateOrder(orderRequest: OrderRequest) {
        progressData.startProgress()
        apiService.updateOrder(orderManager.curOrderResponse!!.id, orderRequest)
            .enqueue(object : BaseCallback<ServerResponse<Order>>() {
                override fun onSuccess(result: ServerResponse<Order>) {
                    progressData.endProgress()
                    val updatedOrder = result.data
                    orderManager.setOrderResponse(updatedOrder)
                    orderData.postValue(updatedOrder)
                }

                override fun onError(error: List<WSError>) {
                    progressData.endProgress()
                    errorEvent.postValue(error)

//                    val errorCode = response.code()
//                    if (errorCode == 400) {
//                        updateOrderError.postValue(errorCode)
//                    }
                }
            })
    }

    fun calcTotalDishesPrice(): Double {
        var total = 0.0
        orderData.value?.orderItems?.let {
            it.forEach {
                total += (it.price.value * it.quantity)
            }
        }
        return total
    }

    val hasOpenOrder: SingleLiveEvent<HasOpenOrder> = SingleLiveEvent()

    data class HasOpenOrder(val hasOpenOrder: Boolean, val cookInCartName: String? = null, val currentShowingCookName: String? = null)

    //check order status - is there any open order?
    fun checkForOpenOrderAndShowClearCartDialog(currentCookingSlotid: Long?, currentShowingCookName: String) {
        if (orderManager.haveCurrentActiveOrder()) {
            val inCartOrder = orderManager.curOrderResponse
            val inCartCookingSlot = inCartOrder?.cookingSlot
            inCartCookingSlot?.let {
                if (it.id != currentCookingSlotid) {
                    val cookInCartName = inCartOrder.cook.getFullName()
                    //if the showing dish's (cook) is the same as the in-cart order's cook
                    hasOpenOrder.postValue(HasOpenOrder(true, cookInCartName, currentShowingCookName))
                } else {
                    hasOpenOrder.postValue(HasOpenOrder(false))
                }
            }
        } else {
            hasOpenOrder.postValue(HasOpenOrder(false))
        }
    }

    //check if there is an order in cart from diffrent cook
    fun checkForDifferentOpenOrder(currentCookingSlotId: Long?): Boolean {
        if (orderManager.haveCurrentActiveOrder()) {
            val inCartOrder = orderManager.curOrderResponse
            val inCartCookingSlot = inCartOrder?.cookingSlot
            inCartCookingSlot?.let {
                if (it.id != currentCookingSlotId) {
                    return true
                }
            }
        }
        return false
    }


    val checkCartStatus: MutableLiveData<CheckCartStatusEvent> = SingleLiveEvent()

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
            .enqueue(object : BaseCallback<ServerResponse<Void>>() {
                override fun onSuccess(result: ServerResponse<Void>) {
                    progressData.endProgress()
                    checkoutOrderEvent.postValue(CheckoutEvent(true))
                    //todo - check this ! that it send purchase cost
                    val totalCost = orderManager.getTotalCost()
                    eventsManager.sendPurchaseEvent(orderId, "")
                    orderManager.clearCurrentOrder()
                }

                override fun onError(errors: List<WSError>) {
                    progressData.endProgress()
                    errorEvent.postValue(errors)
                }
            })
    }


    //clear order
    fun clearCart() {
        orderManager.clearCurrentOrder()
    }


    //Additional single params
    fun resetTip() {
        tipInDollars.postValue(0)
        tipPercentage.postValue(0)
        postUpdateOrder(OrderRequest(tipPercentage = null))
        postUpdateOrder(OrderRequest(tip = null))
    }

    val tipInDollars = MutableLiveData<Int>()
    val tipPercentage = MutableLiveData<Int>()

    fun updateTip(tipPercentage: Int? = null, tipInCents: Int? = null) {
        tipPercentage?.let {
            this.tipPercentage.postValue(tipPercentage)
        }
        tipInCents?.let {
            this.tipInDollars.postValue(tipInCents)
        }
        postUpdateOrder(OrderRequest(tip = tipInCents?.times(100), tipPercentage = tipPercentage?.toFloat()))
    }

    fun updateAddUtensils(shouldAdd: Boolean) {
        orderManager.updateOrderRequest(addUtensils = shouldAdd)
        postUpdateOrder(OrderRequest(addUtensils = shouldAdd))
    }

    fun updateShppingMethod(shippingMethod: ShippingMethod){
        selectedShippingMethod = shippingMethod
        postUpdateOrder(OrderRequest(shippingService = shippingMethod.code))
    }

    fun setAdditionalDishes(dishes: ArrayList<Dish>) {
        //get only available dishes
        Log.d("wowAdditionalMainAdptr", "setAdditionalDishes $dishes")
        additionalDishes.postValue(dishes)
    }

    data class EditDeliveryTime(val startAt: Date?, val endsAt: Date?)

    val editDeliveryTime: SingleLiveEvent<EditDeliveryTime> = SingleLiveEvent()
    fun editDeliveryTime() {
        val now = Date()
//        var availableCookingSlotStartsAt = orderData.value?.cookingSlot?.startsAt
        var availableCookingSlotStartsAt = orderData.value?.cookingSlot?.orderFrom
        availableCookingSlotStartsAt?.let {
            if (now.time > it.time) {
                availableCookingSlotStartsAt = now
            }
        }
        val availableCookingSlotEndsAt = orderData.value?.cookingSlot?.endsAt
        editDeliveryTime.postValue(EditDeliveryTime(availableCookingSlotStartsAt, availableCookingSlotEndsAt))
    }

    fun rollBackToPreviousAddress() {
        eaterDataManager.rollBackToPreviousAddress()
    }


    //Stripe stuff
//    val getStripeCustomerCards: SingleLiveEvent<StripeCustomerCardsEvent> = SingleLiveEvent()

    data class StripeCustomerCardsEvent(val isSuccess: Boolean, val paymentMethods: List<PaymentMethod>? = null)

    fun getStripeCustomerCards(): SingleLiveEvent<List<PaymentMethod>> {
        return paymentManager.getStripeCustomerCards()
    }

    fun updateUserCustomerCard(paymentMethod: PaymentMethod) {
        eaterDataManager.updateCustomerCard(paymentMethod)
    }

    val shippingMethodsEvent = SingleLiveEvent<ArrayList<ShippingMethod>>()
    fun onNationwideShippingSelectClick() {
        progressData.startProgress()
        orderData.value?.id?.let {
            apiService.getUpsShippingRates(it).enqueue(object : BaseCallback<ServerResponse<ArrayList<ShippingMethod>>>() {
                override fun onSuccess(result: ServerResponse<ArrayList<ShippingMethod>>) {
                    shippingMethodsEvent.postValue(result.data)
                    progressData.endProgress()
                }

                override fun onError(errors: List<WSError>) {
                    progressData.endProgress()
                    errorEvent.postValue(errors)
                }
            })
        }
    }

    fun setFullDishParam(fullDish: FullDish) {
        this.fullDishData = fullDish
    }


}
