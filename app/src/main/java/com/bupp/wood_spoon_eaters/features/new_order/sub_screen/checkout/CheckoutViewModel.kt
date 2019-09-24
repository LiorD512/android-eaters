package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout

import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.stripe.android.CustomerSession
import com.stripe.android.StripeError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.stripe.android.model.PaymentMethod
import com.stripe.android.CustomerSession.PaymentMethodsRetrievalListener
import com.stripe.android.model.Customer
import com.stripe.android.CustomerSession.CustomerRetrievalListener


class CheckoutViewModel(val api: ApiService, val orderManager: OrderManager, val eaterDataManager: EaterDataManager) :
    ViewModel() {

    data class OrderDetailsEvent(val order: Order?)

    val getOrderDetailsEvent: SingleLiveEvent<OrderDetailsEvent> = SingleLiveEvent()
    fun getOrderDetails() {
        getOrderDetailsEvent.postValue(OrderDetailsEvent(orderManager.curOrderResponse))
    }

    data class DeliveryDetailsEvent(val address: Address?, val time: String?)

    val getDeliveryDetailsEvent: SingleLiveEvent<DeliveryDetailsEvent> = SingleLiveEvent()
    fun getDeliveryDetails() {
        val address = eaterDataManager.getLastChosenAddress()
        val time = eaterDataManager.getLastOrderTimeString()
        getDeliveryDetailsEvent.postValue(DeliveryDetailsEvent(address, time))

    }

    data class CheckoutEvent(val isSuccess: Boolean)

    val checkoutOrderEvent: SingleLiveEvent<CheckoutEvent> = SingleLiveEvent()
    fun checkoutOrder(orderId: Long) {
        api.checkoutOrder(orderId, eaterDataManager.getCustomerCardId())
            .enqueue(object : Callback<ServerResponse<Void>> {
                override fun onResponse(call: Call<ServerResponse<Void>>, response: Response<ServerResponse<Void>>) {
                    if (response.isSuccessful) {
                        checkoutOrderEvent.postValue(CheckoutEvent(true))
                        orderManager.clearCurrentOrder()
                    } else {
                        checkoutOrderEvent.postValue(CheckoutEvent(false))
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
                    checkoutOrderEvent.postValue(CheckoutEvent(false))
                }

            })
    }


    fun clearCart() {
        orderManager.clearCurrentOrder()
    }


    val getStripeCustomerCards: SingleLiveEvent<StripeCustomerCardsEvent> = SingleLiveEvent()

    data class StripeCustomerCardsEvent(val isSuccess: Boolean, val paymentMethods: List<PaymentMethod>? = null)

    fun getStripeCustomerCards() {
        CustomerSession.getInstance().getPaymentMethods(PaymentMethod.Type.Card,
            object : PaymentMethodsRetrievalListener {
                override fun onPaymentMethodsRetrieved(@NonNull paymentMethods: List<PaymentMethod>) {
                    Log.d("wowCheckoutVM", "getStripeCustomerCards $paymentMethods")
                    getStripeCustomerCards.postValue(StripeCustomerCardsEvent(true, paymentMethods))
                }

                override fun onError(errorCode: Int, @NonNull errorMessage: String, @Nullable stripeError: StripeError?) {
                    Log.d("wowCheckoutVM", "getStripeCustomerCards ERROR $errorMessage")
                    getStripeCustomerCards.postValue(StripeCustomerCardsEvent(false))
                }
            })
    }


    fun getCurrentCustomer() {
        CustomerSession.getInstance().retrieveCurrentCustomer(object : CustomerRetrievalListener {
            override fun onCustomerRetrieved(customer: Customer) {
                Log.d("wowCheckoutVM", "getCurrentCustomer ${customer.defaultSource}")
            }

            override fun onError(errorCode: Int, errorMessage: String, stripeError: StripeError?) {
                Log.d("wowCheckoutVM", "getCurrentCustomer ERROR $errorMessage")
            }
        })
    }

    fun updateUserCustomerCard(paymentMethod: PaymentMethod) {
        eaterDataManager.updateCustomerCard(paymentMethod)
    }

    fun updateTipPercentage(tipSelection: Int) {
        orderManager.tempTipPercentage = tipSelection
    }

    fun updateTipInDollars(tipInDollars: Int) {
        orderManager.tempTipInDollars = tipInDollars
    }

    fun getTempTipPercent(): Int {
        return orderManager.tempTipPercentage
    }

    fun getTempTipInDollars(): Int {
        return orderManager.tempTipInDollars
    }


    //update items quantity
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
                ingItem!!.id?.let { removedIngredientIds.add(it) }
            }
            val updatedOrderItemRequest =
                OrderItemRequest(item.id, item.dish.id, item.quantity, removedIngredientIds, item.notes)
            tempOrderItems.add(updatedOrderItemRequest)
        }

        orderItems!!.clear()
        orderItems!!.addAll(tempOrderItems)

        api.updateOrder(orderManager.curOrderResponse!!.id, orderRequest)
            .enqueue(object : Callback<ServerResponse<Order>> {
                override fun onResponse(call: Call<ServerResponse<Order>>, response: Response<ServerResponse<Order>>) {
                    if (response.isSuccessful) {
                        val updatedOrder = response.body()?.data
                        orderManager.setOrderResponse(updatedOrder)
                        getOrderDetails()
                    } else {
                        Log.d("wowCheckoutVm", "updateOrder FAILED")
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Order>>, t: Throwable) {
                    Log.d("wowCheckoutVm", "updateOrder big FAILED")
                }
            })

    }


}
