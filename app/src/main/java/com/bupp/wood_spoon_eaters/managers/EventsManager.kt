package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.model.AppSetting
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.segment.analytics.Analytics
import com.segment.analytics.Properties
import com.segment.analytics.Traits
import com.uxcam.UXCam
import org.json.JSONObject

class EventsManager(val context: Context, private val appSetting: AppSettings, private val userRepository: UserRepository, private val eaterDataManager: EaterDataManager){


    fun initSegment(){
        val user = eaterDataManager.currentEater
        Log.d("wowEventsManager", "user: $user")
        user?.let{ user ->
            Analytics.with(context).identify(
                user.id.toString(), Traits()
                    .putName(user.getFullName())
                    .putEmail(user.email)
                    .putPhone(user.phoneNumber), null
            )

            val address = eaterDataManager.getLastChosenAddress()
            Log.d("wowEventsManager", "address: $address")
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
        orderId?.let{
            if (appSetting.isFirstPurchase) {
                Log.d("wowEventsManager", "sendAddToCart")
                val bundle = Bundle()
                bundle.putString("OrderId", orderId.toString())

                val logger = AppEventsLogger.newLogger(context)
                logger.logEvent("Add To Cart", bundle)
                appSetting.isFirstPurchase = false
            }
        }
    }

    fun sendPurchaseEvent(orderId: Long?, purchaseCost: String) {
        orderId?.let{
            if (appSetting.isFirstPurchase) {
                sendFirstPurchaseEvent(it, purchaseCost)
                appSetting.isFirstPurchase = false
            } else {
                sendOtherPurchaseEvent(it, purchaseCost)
            }
        }
    }

    private fun sendFirstPurchaseEvent(orderId: Long?, purchaseCost: String) {
        Log.d("wowEventsManager", "sendFirstPurchaseEvent")
        val bundle = Bundle()
        bundle.putString("OrderId", orderId.toString())

        bundle.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "$")
        bundle.putString(AppEventsConstants.EVENT_PARAM_VALUE_TO_SUM, purchaseCost)

        val logger = AppEventsLogger.newLogger(context)
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_WISHLIST, bundle)
    }

    private fun sendOtherPurchaseEvent(orderId: Long?, purchaseCost: String) {
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

        val eventData = Properties()
        params?.forEach{
            eventData.putValue(it.key, it.value)
        }
        when(eventName){
            Constants.UXCAM_EVENT_ORDER_PLACED -> {
                Analytics.with(context).track("Order placed", eventData)
            }
            Constants.UXCAM_EVENT_ADD_DISH -> {
                Analytics.with(context).track(Constants.UXCAM_EVENT_ADD_DISH, eventData)
            }
            Constants.UXCAM_EVENT_ADD_ADDITIONAL_DISH -> {
                Analytics.with(context).track("Add Additional Dish To Cart", eventData)
            }
            Constants.UXCAM_EVENT_SEARCHED_ITEM -> {
                Analytics.with(context).track("Item Searched", eventData)
            }
            else -> {
                Analytics.with(context).track(eventName)
            }
        }
    }

}