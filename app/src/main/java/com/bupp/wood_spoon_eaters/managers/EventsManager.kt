package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.model.*
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.segment.analytics.Analytics
import com.segment.analytics.Properties
import com.segment.analytics.Traits
import com.uxcam.UXCam
import java.math.BigDecimal
import java.util.*

class EventsManager(val context: Context, private val sharedPreferences: SharedPreferences){

    private val shouldFireEvent = BuildConfig.BUILD_TYPE.equals("release", true)
    private var isFirstPurchase: Boolean
        get() = sharedPreferences.getBoolean(IS_FIRST_PURCHASE, true)
        set(isFirstTime) = sharedPreferences.edit().putBoolean(IS_FIRST_PURCHASE, isFirstTime).apply()

    fun initSegment(eater: Eater?, address: Address?){
//        val user = eaterDataManager.currentEater
        Log.d(TAG, "user: $eater")
        eater?.let{ user ->
            Analytics.with(context).identify(
                user.id.toString(), Traits()
                    .putName(user.getFullName())
                    .putEmail(user.email)
                    .putPhone(user.phoneNumber)
                    .putValue("shipped Order Count", user.ordersCount)
//                    .putValue("Last order made at", user.)
                , null
            )

//            val address = eaterDataManager.getLastChosenAddress()
            Log.d(TAG, "address: $address")
            address?.let{
                Analytics.with(context).identify(
                    user.id.toString(), Traits()
                        .putAddress(
                            Traits.Address()
                                .putCity(it.city?.name)
                                .putCountry(it.country?.name)
                                .putState(it.state?.name)
                                .putStreet(it.streetLine1)
                                .putPostalCode(it.zipCode)
                        ), null
                )
            }
        }
    }

    fun sendAddToCart(orderId: Long?) {
        if(shouldFireEvent) {
            orderId?.let {
                if (isFirstPurchase) {
                    Log.d(TAG, "sendAddToCart")
                    val bundle = Bundle()
                    bundle.putString("OrderId", orderId.toString())

                    val logger = AppEventsLogger.newLogger(context)
                    logger.logEvent(Constants.EVENT_ADD_DISH, bundle)
                    isFirstPurchase = false
                }
            }
        }
    }

    fun sendPurchaseEvent(orderId: Long?, purchaseCost: Double) {
        if(shouldFireEvent){
            orderId?.let{
                sendFirstPurchaseEvent(it, purchaseCost)
            }
        }
    }

    private fun sendFirstPurchaseEvent(orderId: Long?, purchaseCost: Double) {
        if(shouldFireEvent) {
            Log.d(TAG, "sendFirstPurchaseEvent")
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, "[{\"orderId\": $orderId]")
            logger.logPurchase(BigDecimal.valueOf(purchaseCost), Currency.getInstance("USD"), params)
        }
    }

    fun sendRegistrationCompletedEvent() {
        if(shouldFireEvent) {
            Log.d(TAG, "sendRegistrationCompletedEvent")
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()
            params.putString(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, "onboarding_finished")
            logger.logEvent("onboarding_finished", params)
        }
    }


    fun logOnDishClickEvent(dishId: String) {
        if(shouldFireEvent) {
            Log.d(TAG, "sendRegistrationCompletedEvent")
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()
            params.putString("dish_id", dishId)
            logger.logEvent(Constants.EVENT_CLICK_ON_DISH, params)
        }
    }

    fun proceedToCheckoutEvent() {
        if(shouldFireEvent) {
            Log.d(TAG, "sendRegistrationCompletedEvent")
            val logger = AppEventsLogger.newLogger(context)
            logger.logEvent(Constants.EVENT_PROCEED_TO_CART)
        }
    }

    fun logEvent(eventName: String, params: Map<String, String>? = null){
        Log.d(TAG, "logUxCamEvent: $eventName PARAMS: $params")
        if(params != null ){
            UXCam.logEvent(eventName, params)
        }else{
            UXCam.logEvent(eventName)
        }

        val eventData = Properties()
        params?.forEach{
            eventData.putValue(it.key, it.value)
        }
        when(eventName){
            Constants.EVENT_ORDER_PLACED -> {
                Analytics.with(context).track(Constants.EVENT_ORDER_PLACED, eventData)
            }
            Constants.EVENT_ADD_ADDITIONAL_DISH -> {
                Analytics.with(context).track(Constants.EVENT_ADD_ADDITIONAL_DISH, eventData)
            }
            Constants.EVENT_ADD_DISH -> {
                Analytics.with(context).track(Constants.EVENT_ADD_DISH, eventData)
            }
            Constants.EVENT_SEARCHED_ITEM -> {
                Analytics.with(context).track("search", eventData)
            }
            else -> {
                Analytics.with(context).track(eventName, eventData)
            }
        }
    }


    companion object{
        const val IS_FIRST_PURCHASE = "is_first_purchase"
        const val TAG = "wowEventsManager"

    }

}