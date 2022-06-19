package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.eatwoodspoon.analytics.events.AnalyticsEvent
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.segment.analytics.Analytics
import com.segment.analytics.Properties
import com.segment.analytics.Traits
import com.uxcam.UXCam
import java.math.BigDecimal
import java.util.*

//todo 🚩 Keep in mind this EatersAnalyticsTracker treks a sensitive user data like email, phoneNumber, fullName.
class EatersAnalyticsTracker(
    val context: Context,
    private var firebaseAnalytics: FirebaseAnalytics
) {

    private var currentUserId: String? = null
    private val shouldFireEvent = true//FlavorClassThing.equals("release", true)

    fun initSegment(eater: Eater?, address: Address?) {
        eater?.let { user ->

            val userIdString = user.id.toString().also {
                this.currentUserId = it
            }
            val unifiedUserIdString = "Eater-$userIdString"
            val traits = Traits()
                .putName(user.getFullName())
                .putEmail(user.email)
                .putPhone(user.phoneNumber)
                .putValue("shipped Order Count", user.ordersCount)

            address?.let {
                traits.putAddress(
                    Traits.Address()
                        .putCity(it.city?.name)
                        .putCountry(it.country?.name)
                        .putState(it.state?.name)
                        .putStreet(it.streetLine1)
                        .putPostalCode(it.zipCode)
                )
            }

            Analytics.with(context).apply {
                identify(userIdString, traits, null)
                alias(unifiedUserIdString)
                identify(unifiedUserIdString, traits, null)

            }

            UXCam.setUserIdentity(user.id.toString())
            UXCam.setUserProperty("email", user.email ?: "N/A")
            UXCam.setUserProperty("name", user.getFullName() ?: "N/A")
            UXCam.setUserProperty("phone", user.phoneNumber ?: "N/A")
            UXCam.setUserProperty("created_at", DateUtils.parseDateToDate(user.createdAt))

        }
    }


    fun sendPurchaseEvent(orderId: Long?, purchaseCost: Double) {
        if (shouldFireEvent) {
            orderId?.let {
                sendFirstPurchaseEvent(it, purchaseCost)
            }
        }
    }

    private fun sendFirstPurchaseEvent(orderId: Long?, purchaseCost: Double) {
        if (shouldFireEvent) {
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, "[{\"orderId\": $orderId]")
            logger.logPurchase(BigDecimal.valueOf(purchaseCost), Currency.getInstance("USD"), params)
        }
    }

    private fun logFBAddToCart(eventData: Map<String, Any>?) {
        if (shouldFireEvent) {
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()

            val formattedPrice = (eventData?.get("dish_price") as String).replace("$", "")

            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, eventData.get("dish_name") as String?)
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Dish")
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, eventData.get("dish_id") as String?)
            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD")
            logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, formattedPrice.toDouble(), params)
        }
    }

    private fun logFBAddAdditionalToCart(eventData: Map<String, Any>?) {
        if (shouldFireEvent) {
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()

            val formattedPrice = (eventData?.get("dish_price") as String).replace("$", "")

            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, eventData.get("dish_name") as String?)
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Dish-Upsale")
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, eventData.get("dish_id") as String?)
            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD")
            logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, formattedPrice.toDouble(), params)
        }
    }

    private fun logFBCreateAccount(eventData: Map<String, Any>?) {
        if (shouldFireEvent) {
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()

            params.putString("user_id", eventData?.get("user_id") as String?)
            logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, params)
        }
    }

    private fun logOnRestaurantClickEvent(eventData: Map<String, Any>?) {
        if (shouldFireEvent) {
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()

            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, eventData?.get("home_chef_name") as String?)
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Home Chef")
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, eventData?.get("home_chef_id") as String?)
            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD")
            logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, 0.0, params)
        }
    }

    private fun trackEventTimeSpentInOnboarding(eventData: Map<String, Any>?) {
        if (shouldFireEvent) {
            AppEventsLogger
                .newLogger(context)
                .logEvent(
                    AppEventsConstants.EVENT_PARAM_CONTENT,
                    bundleOf().apply {
                        putString("seconds", eventData?.get("seconds") as String?)
                    })
        }
    }

    private fun logOnDishClickEvent(eventData: Map<String, Any>?) {
        if (shouldFireEvent) {
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()

            val formattedPrice = (eventData?.get("dish_price") as String).replace("$", "")

            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, eventData["dish_name"] as String?)
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "dISH")
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, eventData.get("dish_id") as String?)
            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD")
            logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, formattedPrice.toDouble(), params)
        }
    }

    fun logEvent(eventName: String, params: Map<String, Any>? = null) {
        if (params != null) {
            UXCam.logEvent(eventName, params)
        } else {
            UXCam.logEvent(eventName)
        }

        val eventData = Properties()
        eventData.putValue("user_id", currentUserId)

        params?.forEach {
            eventData.putValue(it.key, it.value)
        }
        when (eventName) {
            Constants.EVENT_ADD_DISH -> {
                Analytics.with(context).track(Constants.EVENT_ADD_DISH, eventData)
                logFBAddToCart(params)
            }
            Constants.EVENT_CREATE_ACCOUNT -> {
                Analytics.with(context).track(Constants.EVENT_CREATE_ACCOUNT, eventData)
                logFBCreateAccount(params)
            }
            Constants.EVENT_CLICK_RESTAURANT -> {
                Analytics.with(context).track(Constants.EVENT_CLICK_RESTAURANT, eventData)
                logOnRestaurantClickEvent(eventData)
            }
            Constants.EVENT_CLICK_ON_DISH -> {
                Analytics.with(context).track(Constants.EVENT_CLICK_ON_DISH, eventData)
                logOnDishClickEvent(eventData)
            }
            Constants.EVENT_TIME_SPENT_IN_ONBOARDING -> {
                Analytics.with(context).track(Constants.EVENT_TIME_SPENT_IN_ONBOARDING, eventData)
                trackEventTimeSpentInOnboarding(eventData)
            }
            else -> {
                Analytics.with(context).track(eventName, eventData)
            }
        }
    }

    fun onFlowEventFired(curEvent: FlowEventsManager.FlowEvents) {
        when (curEvent) {
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
            FlowEventsManager.FlowEvents.PAGE_VISIT_SEARCH -> {
                Analytics.with(context).screen("view_search_page")
            }
            else -> {}
        }
    }

    fun logErrorToFirebase(eventName: String, errorMsg: String) {
        val bundle = Bundle()
        bundle.putString("func_$eventName", errorMsg)
        logToFirebase(bundle)
    }

    private fun logToFirebase(bundle: Bundle) {
        if (BuildConfig.DEBUG) {
            firebaseAnalytics.logEvent("Error_Staging", bundle)
        } else {
            firebaseAnalytics.logEvent("Error_Prod", bundle)
        }
    }
}

fun EatersAnalyticsTracker.logEvent(event: AnalyticsEvent) {
    this.logEvent(event.name, event.params)
}
