package com.bupp.wood_spoon_eaters.features.order_checkout

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.stripe.android.model.PaymentMethod
import kotlinx.coroutines.launch

class OrderCheckoutViewModel(private val paymentManager: PaymentManager, private val cartManager: CartManager) : ViewModel() {


    val navigationEvent = MutableLiveData<NavigationEvent>()
    enum class NavigationEvent{
        START_LOCATION_AND_ADDRESS_ACTIVITY,
        FINISH_ACTIVITY_AFTER_PURCHASE,
        START_PAYMENT_METHOD_ACTIVITY,
        FINISH_CHECKOUT_ACTIVITY,
        INITIALIZE_STRIPE,
        OPEN_PROMO_CODE_FRAGMENT
    }

    fun handleMainNavigation(type: NavigationEvent) {
        navigationEvent.postValue(type)
    }

    //stripe
    val stripeInitializationEvent = paymentManager.getStripeInitializationEvent()
    fun startStripeOrReInit(){
        MTLogger.c(TAG, "startStripeOrReInit")
        if(paymentManager.hasStripeInitialized){
            Log.d(TAG, "start payment method")
            navigationEvent.postValue(NavigationEvent.START_PAYMENT_METHOD_ACTIVITY)
        }else{
            MTLogger.c(TAG, "re init stripe")
            navigationEvent.postValue(NavigationEvent.INITIALIZE_STRIPE)
        }
    }

    fun reInitStripe(context: Context) {
        viewModelScope.launch {
            paymentManager.initPaymentManagerWithListener(context)
        }
    }

    fun updatePaymentMethod(paymentMethod: PaymentMethod?) {
        paymentMethod?.let{
            paymentManager.updateSelectedPaymentMethod(it)
        }
    }

    fun onLocationChanged() {
        viewModelScope.launch {
            val result = cartManager.updateOrderDeliveryAddressParam()
            result?.let {
                when (result.type) {
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {
                    }
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {
                    }
                    OrderRepository.OrderRepoStatus.WS_ERROR -> {
                        cartManager.onLocationInvalid()
                        cartManager.handleWsError(result.wsError)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    companion object{
        const val TAG = "wowOrderCheckoutVM"
    }



}
