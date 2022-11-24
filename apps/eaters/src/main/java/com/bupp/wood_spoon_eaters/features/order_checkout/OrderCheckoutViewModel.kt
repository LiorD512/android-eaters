package com.bupp.wood_spoon_eaters.features.order_checkout

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.domain.FeatureFlagNewAuthUseCase
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.CustomOrderItem
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.DishInitParams
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.eatwoodspoon.analytics.events.EatersGiftEvent
import com.stripe.android.model.PaymentMethod
import kotlinx.coroutines.launch
import java.util.*

class OrderCheckoutViewModel(
    application: Application,
    private val paymentManager: PaymentManager,
    private val cartManager: CartManager,
    private val flowEventsManager: FlowEventsManager,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker,
    private val eaterDataManager: EaterDataManager,
    featureFlagNewAuthUseCase: FeatureFlagNewAuthUseCase
) : AndroidViewModel(application) {


    val navigationEvent = LiveEventData<NavigationEvent>()
    val deliveryAtChangeEvent = cartManager.getDeliveryAtChangeEvent()
    val progressData = ProgressData()

    private val isNewAuthEnabled = featureFlagNewAuthUseCase.execute(null)

    sealed class NavigationEvent {
        object START_LOCATION_AND_ADDRESS_ACTIVITY: NavigationEvent()
        data class START_USER_DETAILS_ACTIVITY(val alternativeReasonDescription: String?): NavigationEvent()
        object FINISH_ACTIVITY_AFTER_PURCHASE: NavigationEvent()
        object START_PAYMENT_METHOD_ACTIVITY: NavigationEvent()
        object FINISH_CHECKOUT_ACTIVITY: NavigationEvent()
        object OPEN_PROMO_CODE_FRAGMENT: NavigationEvent()
        data class OPEN_DISH_PAGE(val dishInitParams: DishInitParams): NavigationEvent()
        object OPEN_TIP_FRAGMENT: NavigationEvent()
        object OPEN_GIFT_FRAGMENT: NavigationEvent()
    }

    fun handleMainNavigation(event: NavigationEvent) {
        navigationEvent.postRawValue(event)
    }

    fun openDishPageWithOrderItem(customOrderItem: CustomOrderItem) {
        val dishInitParams = DishInitParams(orderItem = customOrderItem.orderItem, cookingSlot = customOrderItem.cookingSlot, menuItem = null)
        navigationEvent.postRawValue(NavigationEvent.OPEN_DISH_PAGE(dishInitParams))
    }

    //stripe
    fun startStripeOrReInit(){
        MTLogger.c(TAG, "startStripeOrReInit")
        if(isNewAuthEnabled && !eaterDataManager.hasUserSetDetails()) {
            navigationEvent.postRawValue(NavigationEvent.START_USER_DETAILS_ACTIVITY(alternativeReasonDescription = "Please finish setting up your account details before adding a payment method."))
        } else {
            viewModelScope.launch {
                if(!paymentManager.hasStripeInitialized) {
                    MTLogger.c(TAG, "re init stripe")
                    paymentManager.initPaymentManagerWithListener(getApplication())
                }
                Log.d(TAG, "start payment method")
                navigationEvent.postRawValue(NavigationEvent.START_PAYMENT_METHOD_ACTIVITY)
            }
        }
    }

    fun updatePaymentMethod(context: Context, paymentMethod: PaymentMethod?) {
        paymentManager.updateSelectedPaymentMethod(context, paymentMethod)
    }

    fun onLocationChanged(selectedAddress: Address?) {
        viewModelScope.launch {
            val result = cartManager.updateOrderDeliveryAddressParam(selectedAddress)
            result?.let {
                when (result.type) {
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {
                        eatersAnalyticsTracker.logEvent(Constants.EVENT_UPDATE_DELIVERY_ADDRESS, mapOf(Pair("success", true)))
                    }
                    OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {
                        eatersAnalyticsTracker.logEvent(Constants.EVENT_UPDATE_DELIVERY_ADDRESS, mapOf(Pair("success", false)))
                    }
                    OrderRepository.OrderRepoStatus.WS_ERROR -> {
                        cartManager.onLocationInvalid()
                        cartManager.handleWsError(result.wsError)
                        eatersAnalyticsTracker.logEvent(Constants.EVENT_UPDATE_DELIVERY_ADDRESS, mapOf(Pair("success", false)))
                    }
                    else -> {
                    }
                }
            }
        }
    }

    fun onUserDetailsUpdated() {

    }

    fun logPageEvent(eventType: FlowEventsManager.FlowEvents) {
        flowEventsManager.trackPageEvent(eventType)
    }

    fun logEvent(eventName: String) {
        eatersAnalyticsTracker.logEvent(eventName)
    }

    fun logChangeTime(date: Date?) {
        eatersAnalyticsTracker.logEvent(Constants.EVENT_CHANGE_DELIVERY_TIME, getChangedTimeData(date))
    }

    fun logEventGiftClicked() {
        val orderId = cartManager.getCurrentOrderData().value?.id?.toString() ?: ""
        eatersAnalyticsTracker.logEvent(EatersGiftEvent.ClickGiftInCheckoutEvent(order_id = orderId))
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