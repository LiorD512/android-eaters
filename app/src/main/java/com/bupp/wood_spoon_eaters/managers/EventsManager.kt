package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.segment.analytics.Analytics
import com.segment.analytics.Properties
import com.segment.analytics.Traits
import com.uxcam.UXCam
import java.math.BigDecimal
import java.util.*
import kotlin.math.log

class EventsManager(val context: Context, private val sharedPreferences: SharedPreferences){

    private var currentUserId: String? = null
    private val shouldFireEvent = true//BuildConfig.BUILD_TYPE.equals("release", true)
    private var isFirstPurchase: Boolean
        get() = sharedPreferences.getBoolean(IS_FIRST_PURCHASE, true)
        set(isFirstTime) = sharedPreferences.edit().putBoolean(IS_FIRST_PURCHASE, isFirstTime).apply()

    fun initSegment(eater: Eater?, address: Address?){
//        val user = eaterDataManager.currentEater
//        Log.d(TAG, "user: $eater")
        eater?.let{ user ->

            this.currentUserId = user.id.toString()
            Analytics.with(context).identify(
                user.id.toString(), Traits()
                    .putName(user.getFullName())
                    .putEmail(user.email)
                    .putPhone(user.phoneNumber)
                    .putValue("shipped Order Count", user.ordersCount)
                , null
            )

            UXCam.setUserIdentity(user.id.toString())
            UXCam.setUserProperty("email", user.email ?: "N/A")
            UXCam.setUserProperty("name", user.getFullName() ?: "N/A")
            UXCam.setUserProperty("phone",user.phoneNumber ?: "N/A")
            UXCam.setUserProperty("created_at", DateUtils.parseDateToDate(user.createdAt))

//            Log.d(TAG, "address: $address")
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

    private fun logFBAddToCart(eventData: Map<String, Any>?) {
        if(shouldFireEvent) {
            Log.d(TAG, "logFBAddToCart")
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()

            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, eventData?.get("dish_name") as String?)
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Dish")
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, eventData?.get("dish_id") as String?)
            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD")
            logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, eventData?.get("dish_price").toString().toDouble(), params)
        }
    }

    private fun logFBAddAdditionalToCart(eventData: Map<String, Any>?) {
        if(shouldFireEvent) {
            Log.d(TAG, "logFBAddAdditionalToCart")
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()

            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, eventData?.get("dish_name") as String?)
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Dish-Upsale")
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, eventData?.get("dish_id") as String?)
            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD")
            logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, eventData?.get("dish_price").toString().toDouble(), params)
        }
    }

    private fun logFBCreateAccount(eventData: Map<String, Any>?) {
        if(shouldFireEvent) {
            Log.d(TAG, "logFBAddAdditionalToCart")
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()

            params.putString("user_id", eventData?.get("user_id") as String?)
            logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, params)
        }
    }

    fun logOnDishClickEvent(eventData: Map<String, Any>?) {
        if(shouldFireEvent) {
            Log.d(TAG, "logOnDishClickEvent")
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()

            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, eventData?.get("dish_name") as String?)
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Dish-Upsale")
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, eventData?.get("dish_id") as String?)
            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD")
            logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, eventData?.get("dish_price").toString().toDouble(), params)
        }
    }

//    fun sendRegistrationCompletedEvent() {
//        if(shouldFireEvent) {
//            Log.d(TAG, "sendRegistrationCompletedEvent")
//            val logger = AppEventsLogger.newLogger(context)
//            val params = Bundle()
//            params.putString(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, "onboarding_finished")
//            logger.logEvent("onboarding_finished", params)
//        }
//    }


//    fun proceedToCheckoutEvent() {
//        if(shouldFireEvent) {
//            Log.d(TAG, "sendRegistrationCompletedEvent")
//            val logger = AppEventsLogger.newLogger(context)
//            logger.logEvent(Constants.EVENT_PROCEED_TO_CART)
//        }
//    }

    fun logEvent(eventName: String, params: Map<String, Any>? = null){
        Log.d(TAG, "logEvent: $eventName PARAMS: $params")
        if(params != null ){
            UXCam.logEvent(eventName, params)
        }else{
            UXCam.logEvent(eventName)
        }

        val eventData = Properties()
        eventData.putValue("user_id", currentUserId)

        params?.forEach{
            eventData.putValue(it.key, it.value)
        }
        when(eventName){
            Constants.EVENT_ORDER_PLACED -> {
                Analytics.with(context).track(Constants.EVENT_ORDER_PLACED, eventData)
            }
            Constants.EVENT_ADD_ADDITIONAL_DISH -> {
                Analytics.with(context).track(Constants.EVENT_ADD_ADDITIONAL_DISH, eventData)
                logFBAddAdditionalToCart(params)
            }
            Constants.EVENT_ADD_DISH -> {
                Analytics.with(context).track(Constants.EVENT_ADD_DISH, eventData)
                logFBAddToCart(params)
            }
            Constants.EVENT_SEARCHED_ITEM -> {
                Analytics.with(context).track(Constants.EVENT_SEARCH, eventData)
            }
            Constants.EVENT_CREATE_ACCOUNT -> {
                Analytics.with(context).track(Constants.EVENT_CREATE_ACCOUNT, eventData)
                logFBCreateAccount(params)
            }
            Constants.EVENT_CAMPAIGN_INVITE -> {
                Analytics.with(context).track(Constants.EVENT_CAMPAIGN_INVITE, eventData)
            }
            else -> {
                Analytics.with(context).track(eventName, eventData)
            }
        }
    }

    fun onFlowEventFired(curEvent: FlowEventsManager.FlowEvents) {
        when(curEvent){
            FlowEventsManager.FlowEvents.PAGE_VISIT_ON_BOARDING -> {
                Analytics.with(context).screen("onboarding")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_GET_OTF_CODE -> {
                Analytics.with(context).screen("getOtpCode")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_CREATE_ACCOUNT -> {
                Analytics.with(context).screen("createAccount")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_FEED -> {
                Analytics.with(context).screen("feed")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_ACCOUNT -> {
                Analytics.with(context).screen("account")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_ORDERS -> {
                Analytics.with(context).screen("orders")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_PRIVACY_POLICY -> {
                Analytics.with(context).screen("privacyPolicy")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_QA -> {
                Analytics.with(context).screen("popularQA")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_COMMUNICATION_SETTINGS -> {
                Analytics.with(context).screen("communicationSettings")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_EDIT_ACCOUNT -> {
                Analytics.with(context).screen("editAccount")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_JOIN_HOME_CHEF -> {
                Analytics.with(context).screen("joinHomeChef")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_ADDRESSES -> {
                Analytics.with(context).screen("addresses")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_DELETE_ACCOUNT -> {
                Analytics.with(context).screen("deleteAccount")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_CHECKOUT -> {
                Analytics.with(context).screen("checkout")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_TRACK_ORDER -> {
                Analytics.with(context).screen("trackOrder")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_LOCATION_PERMISSION -> {
                Analytics.with(context).screen("locationPersuasion")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_DISH -> {
                Analytics.with(context).screen("dish")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_HOME_CHEF -> {
                Analytics.with(context).screen("homeChef")
            }
            FlowEventsManager.FlowEvents.PAGE_VISIT_CART -> {
                Analytics.with(context).screen("cart")
            }
        }
    }

    companion object{
        const val IS_FIRST_PURCHASE = "is_first_purchase"
        const val TAG = "wowEventsManager"

    }

}