package com.bupp.wood_spoon_eaters.features.order_checkout

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.CustomCartItem
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.CheckoutViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.RestaurantPageFragmentDirections
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishInitParams
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.RestaurantInitParams
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.CookingSlot
import com.bupp.wood_spoon_eaters.model.MenuItem
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
    }

    fun handleMainNavigation(type: NavigationEvent) {
        navigationEvent.postValue(type)
    }

    //stripe
    val stripeInitializationEvent = paymentManager.getStripeInitializationEvent()
    fun startStripeOrReInit(){
        MTLogger.c(CheckoutViewModel.TAG, "startStripeOrReInit")
        if(paymentManager.hasStripeInitialized){
            Log.d(CheckoutViewModel.TAG, "start payment method")
            navigationEvent.postValue(NavigationEvent.START_PAYMENT_METHOD_ACTIVITY)
        }else{
            MTLogger.c(NewOrderMainViewModel.TAG, "re init stripe")
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
            val result = cartManager.updateOrderDeliveryParam()
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



}
