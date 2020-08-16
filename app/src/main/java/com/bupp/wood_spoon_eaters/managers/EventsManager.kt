package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.uxcam.UXCam
import org.json.JSONObject

class EventsManager(val context: Context, val sharedPreferences: SharedPreferences){

    val IS_FIRST_PURCHASE = "is_first_purchase"
    var isFirstPurchase: Boolean
        get() = sharedPreferences.getBoolean(IS_FIRST_PURCHASE, true)
        set(isFirstTime) = sharedPreferences.edit().putBoolean(IS_FIRST_PURCHASE, isFirstTime).apply()

    fun sendAddToCart(orderId: Long?) {
        orderId?.let{
            if (isFirstPurchase) {
                Log.d("wowEventsManager", "sendAddToCart")
                val bundle = Bundle()
                bundle.putString("OrderId", orderId.toString())

                val logger = AppEventsLogger.newLogger(context)
                logger.logEvent("Add To Cart", bundle)
                isFirstPurchase = false
            }
        }
    }

    fun sendPurchaseEvent(orderId: Long?, purchaseCost: String) {
        orderId?.let{
            if (isFirstPurchase) {
                sendFirstPurchaseEvent(it, purchaseCost)
                isFirstPurchase = false
            } else {
                sendOtherPurchaseEvent(it, purchaseCost)
            }
        }
    }

    fun sendFirstPurchaseEvent(orderId: Long?, purchaseCost: String) {
        Log.d("wowEventsManager", "sendFirstPurchaseEvent")
        val bundle = Bundle()
        bundle.putString("OrderId", orderId.toString())

        bundle.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "$")
        bundle.putString(AppEventsConstants.EVENT_PARAM_VALUE_TO_SUM, purchaseCost)

        val logger = AppEventsLogger.newLogger(context)
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_WISHLIST, bundle)
    }

    fun sendOtherPurchaseEvent(orderId: Long?, purchaseCost: String) {
        Log.d("wowEventsManager", "sendOtherPurchaseEvent")
        val bundle = Bundle()
        bundle.putString("OrderId", orderId.toString())

        val logger = AppEventsLogger.newLogger(context)
        logger.logEvent(AppEventsConstants.EVENT_NAME_PURCHASED, bundle)
    }

    fun logUxCamEvent(eventName: String, params: Map<String, String>? = null){
        Log.d("wowEventsManager", "logUxCamEvent: $eventName PARAMS: $params")
        if(params != null ){
            UXCam.logEvent(eventName, params)
        }else{
            UXCam.logEvent(eventName)
        }
    }

}