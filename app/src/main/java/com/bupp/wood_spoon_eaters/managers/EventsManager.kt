package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics

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

    fun sendPurchaseEvent(orderId: Long?) {
        orderId?.let{
            if (isFirstPurchase) {
                sendFirstPurchaseEvent(it)
                isFirstPurchase = false
            } else {
                sendOtherPurchaseEvent(it)
            }
        }
    }

    fun sendFirstPurchaseEvent(orderId: Long?) {
        Log.d("wowEventsManager", "sendFirstPurchaseEvent")
        val bundle = Bundle()
        bundle.putString("OrderId", orderId.toString())

        val logger = AppEventsLogger.newLogger(context)
        logger.logEvent("first purchase", bundle)
    }

    fun sendOtherPurchaseEvent(orderId: Long?) {
        Log.d("wowEventsManager", "sendOtherPurchaseEvent")
        val bundle = Bundle()
        bundle.putString("OrderId", orderId.toString())

        val logger = AppEventsLogger.newLogger(context)
        logger.logEvent("repeat purchase", bundle)
    }

}