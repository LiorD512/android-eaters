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

    fun updateUserCustomerCard(paymentMethod: PaymentMethod) {
        eaterDataManager.updateCustomerCard(paymentMethod)
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
