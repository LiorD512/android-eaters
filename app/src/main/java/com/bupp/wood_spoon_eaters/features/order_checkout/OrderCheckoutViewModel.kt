package com.bupp.wood_spoon_eaters.features.order_checkout

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.CheckoutFragmentDirections
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.CustomOrderItem
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantMainViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.RestaurantPageFragmentDirections
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.DishInitParams
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.stripe.android.model.PaymentMethod
import kotlinx.coroutines.launch
import java.util.*

class OrderCheckoutViewModel(
    private val paymentManager: PaymentManager,
    private val cartManager: CartManager,
    private val flowEventsManager: FlowEventsManager,
private val eventsManager: EventsManager) : ViewModel() {


    val navigationEvent = LiveEventData<NavigationEvent>()
    val deliveryAtChangeEvent = cartManager.getDeliveryAtChangeEvent()

    data class NavigationEvent(
        val navigationType: NavigationEventType,
        val navDirections: NavDirections? = null
    )

    enum class NavigationEventType{
        START_LOCATION_AND_ADDRESS_ACTIVITY,
        FINISH_ACTIVITY_AFTER_PURCHASE,
        START_PAYMENT_METHOD_ACTIVITY,
        FINISH_CHECKOUT_ACTIVITY,
        INITIALIZE_STRIPE,
        OPEN_PROMO_CODE_FRAGMENT,
        OPEN_DISH_PAGE,
        OPEN_TIP_FRAGMENT
    }

    fun handleMainNavigation(type: NavigationEventType) {
        navigationEvent.postRawValue(NavigationEvent(type))
    }

    fun openDishPageWithOrderItem(customOrderItem: CustomOrderItem) {
        val extras = DishInitParams(orderItem = customOrderItem.orderItem, cookingSlot = customOrderItem.cookingSlot, menuItem = null)
        val action = CheckoutFragmentDirections.actionCheckoutFragmentToDishPageFragment(extras)
        navigationEvent.postRawValue(NavigationEvent(NavigationEventType.OPEN_DISH_PAGE, action))
    }

    //stripe
    fun startStripeOrReInit(){
        MTLogger.c(TAG, "startStripeOrReInit")
        if(paymentManager.hasStripeInitialized){
            Log.d(TAG, "start payment method")
            navigationEvent.postRawValue(NavigationEvent(NavigationEventType.START_PAYMENT_METHOD_ACTIVITY))
        }else{
            MTLogger.c(TAG, "re init stripe")
            navigationEvent.postRawValue(NavigationEvent(NavigationEventType.INITIALIZE_STRIPE))
        }
    }

    fun reInitStripe(context: Context) {
        viewModelScope.launch {
            paymentManager.initPaymentManagerWithListener(context)
        }
    }

    fun updatePaymentMethod(context: Context, paymentMethod: PaymentMethod?) {
        paymentManager.updateSelectedPaymentMethod(context, paymentMethod)
    }

    fun onLocationChanged() {
        viewModelScope.launch {
            val result = cartManager.updateOrderDeliveryAddressParam()
            result?.let {
                when (result.type) {
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {
                        eventsManager.logEvent(Constants.EVENT_UPDATE_DELIVERY_ADDRESS, mapOf(Pair("success", true)))
                    }
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {
                        eventsManager.logEvent(Constants.EVENT_UPDATE_DELIVERY_ADDRESS, mapOf(Pair("success", false)))
                    }
                    OrderRepository.OrderRepoStatus.WS_ERROR -> {
                        cartManager.onLocationInvalid()
                        cartManager.handleWsError(result.wsError)
                        eventsManager.logEvent(Constants.EVENT_UPDATE_DELIVERY_ADDRESS, mapOf(Pair("success", false)))
                    }
                    else -> {
                    }
                }
            }
        }
    }

    fun logPageEvent(eventType: FlowEventsManager.FlowEvents) {
        flowEventsManager.logPageEvent(eventType)
    }

    fun logEvent(eventName: String) {
        eventsManager.logEvent(eventName)
    }

    fun logChangeTime(date: Date?) {
        eventsManager.logEvent(Constants.EVENT_CHANGE_DELIVERY_TIME, getChangedTimeData(date))
    }

    private fun getChangedTimeData(date: Date?): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["selected_date"] = DateUtils.parseDateToFullTime(date)
        data["day"] = DateUtils.parseDateToDayName(date)
        return data
    }

    companion object{
        const val TAG = "wowOrderCheckoutVM"
    }



}
