package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout

import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.ErrorEventType
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.OrderRequest
import com.bupp.wood_spoon_eaters.model.WSError
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.stripe.android.CustomerSession
import com.stripe.android.StripeError
import com.stripe.android.model.PaymentMethod
import com.stripe.android.CustomerSession.PaymentMethodsRetrievalListener
import kotlinx.coroutines.launch


class CheckoutViewModel(private val cartManager: CartManager, private val paymentManager: PaymentManager, val eaterDataManager: EaterDataManager) :
    ViewModel() {

    val getStripeCustomerCards = paymentManager.getPaymentsLiveData()


    val timeChangeEvent = LiveEventData<List<MenuItem>>()
    fun getDeliveryTimeLiveData() = eaterDataManager.getDeliveryTimeLiveData()

    fun updateOrder(orderRequest: OrderRequest) {
        viewModelScope.launch {
            val result = cartManager.postUpdateOrder(orderRequest)
            when (result?.type) {
                OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {

                }
                OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {

                }
            }
        }
    }

    fun onTimeChangeClick() {
        val menuItems = cartManager.currentShowingDish?.availableMenuItems
        menuItems?.let{
            timeChangeEvent.postRawValue(it)
        }
    }


//    data class StripeCustomerCardsEvent(val paymentMethods: List<PaymentMethod>? = null)
//
//    private fun getStripeCustomerCards() {
//        CustomerSession.getInstance().getPaymentMethods(PaymentMethod.Type.Card,
//            object : PaymentMethodsRetrievalListener {
//                override fun onPaymentMethodsRetrieved(@NonNull paymentMethods: List<PaymentMethod>) {
//                    Log.d("wowCheckoutVM", "getStripeCustomerCards $paymentMethods")
//                    getStripeCustomerCards.postValue(StripeCustomerCardsEvent(paymentMethods))
//                }
//
//                override fun onError(errorCode: Int, @NonNull errorMessage: String, @Nullable stripeError: StripeError?) {
//                    Log.d("wowCheckoutVM", "getStripeCustomerCards ERROR $errorMessage")
//                    getStripeCustomerCards.postValue(StripeCustomerCardsEvent(null))
//                }
//            })
//    }

    fun refreshUi(){
        cartManager.refreshOrderUi()
    }

    fun updateUserCustomerCard(paymentMethod: PaymentMethod) {
//        eaterDataManager.updateCustomerCard(paymentMethod)//todo - nyyyy
    }


//    fun getCurrentCustomer() {
//        CustomerSession.getInstance().retrieveCurrentCustomer(object : CustomerRetrievalListener {
//            override fun onCustomerRetrieved(customer: Customer) {
//                Log.d("wowCheckoutVM", "getCurrentCustomer ${customer.defaultSource}")
//            }
//
//            override fun onError(errorCode: Int, errorMessage: String, stripeError: StripeError?) {
//                Log.d("wowCheckoutVM", "getCurrentCustomer ERROR $errorMessage")
//            }
//        })
//    }


}
