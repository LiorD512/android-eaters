package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.eatwoodspoon.analytics.app_attributes.AppAttributesDataSource
import com.eatwoodspoon.analytics.AnalyticsEventReporter
import com.eatwoodspoon.analytics.SessionId
import com.eatwoodspoon.analytics.events.AnalyticsEvent
import com.eatwoodspoon.logsender.Logger
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
    private val firebaseAnalytics: FirebaseAnalytics,
    private val logSender: Logger
) : AnalyticsEventReporter {

    private var currentUserId: String? = null
    private val shouldFireEvent = true//FlavorClassThing.equals("release", true)

    private val facebook = AppEventsLogger.newLogger(context)
    val segment: Analytics = Analytics.with(context)

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

            segment.apply {
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

    private fun enrichParameters(eventName: String, params: Map<String, Any?>?): Map<String, Any?> {
        return mutableMapOf<String, Any?>(
            "user_id" to currentUserId,
            "name" to eventName
        ).apply {
            params?.let { putAll(it) }
        }.toMap()
    }


    fun sendPurchaseEvent(orderId: Long?, purchaseCost: Double) {
        if (shouldFireEvent) {
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, "[{\"orderId\": $orderId]")
            logger.logPurchase(
                BigDecimal.valueOf(purchaseCost),
                Currency.getInstance("USD"),
                params
            )
        }
    }

    fun sendFirstPurchaseEvent(orderId: Long?, purchaseCost: Double) {
        if (shouldFireEvent) {
            orderId?.let {
                val logger = AppEventsLogger.newLogger(context)
                val params = Bundle()
                params.putString(AppEventsConstants.EVENT_PARAM_ORDER_ID, orderId.toString())
                params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, Currency.getInstance("USD").currencyCode)
                params.putDouble("PurchaseAmount", purchaseCost)
                logger.logEvent("FirstOrderPlaced", params)

            }
        }
    }

    private fun logFBAddToCart(eventData: Map<String, Any>?) {
        if (shouldFireEvent) {
            val params = Bundle()

            val formattedPrice = (eventData?.get("dish_price") as String).replace("$", "")

            params.putString(
                AppEventsConstants.EVENT_PARAM_CONTENT,
                eventData.get("dish_name") as String?
            )
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Dish")
            params.putString(
                AppEventsConstants.EVENT_PARAM_CONTENT_ID,
                eventData.get("dish_id") as String?
            )
            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD")
            facebook.logEvent(
                AppEventsConstants.EVENT_NAME_ADDED_TO_CART,
                formattedPrice.toDouble(),
                params
            )
        }
    }

    private fun logFBAddAdditionalToCart(eventData: Map<String, Any>?) {
        if (shouldFireEvent) {
            val logger = AppEventsLogger.newLogger(context)
            val params = Bundle()

            val formattedPrice = (eventData?.get("dish_price") as String).replace("$", "")

            params.putString(
                AppEventsConstants.EVENT_PARAM_CONTENT,
                eventData.get("dish_name") as String?
            )
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Dish-Upsale")
            params.putString(
                AppEventsConstants.EVENT_PARAM_CONTENT_ID,
                eventData.get("dish_id") as String?
            )
            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD")
            logger.logEvent(
                AppEventsConstants.EVENT_NAME_ADDED_TO_CART,
                formattedPrice.toDouble(),
                params
            )
        }
    }

    private fun logFBCreateAccount(eventData: Map<String, Any>?) {
        if (shouldFireEvent) {
            val params = Bundle()

            params.putString("user_id", eventData?.get("user_id") as String?)
            facebook.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, params)
        }
    }

    private fun logOnRestaurantClickEvent(eventData: Map<String, Any>?) {
        if (shouldFireEvent) {
            val params = Bundle()

            params.putString(
                AppEventsConstants.EVENT_PARAM_CONTENT,
                eventData?.get("home_chef_name") as String?
            )
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Home Chef")
            params.putString(
                AppEventsConstants.EVENT_PARAM_CONTENT_ID,
                eventData?.get("home_chef_id") as String?
            )
            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD")
            facebook.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, 0.0, params)
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

            params.putString(
                AppEventsConstants.EVENT_PARAM_CONTENT,
                eventData["dish_name"] as String?
            )
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "dISH")
            params.putString(
                AppEventsConstants.EVENT_PARAM_CONTENT_ID,
                eventData.get("dish_id") as String?
            )
            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD")

            formattedPrice.toDoubleOrNull()?.let {
                logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, it, params)
            }
        }
    }

    fun logEvent(eventName: String, params: Map<String, Any>? = null) {

        val enrichedParams =
            enrichParameters(eventName, params).filterValues { it != null } as Map<String, Any>

        //UXCam
        UXCam.logEvent(eventName, params)

        //Segment
        logToSegment(eventName, params)

        //WS
        logSender.log(enrichedParams)

        when (eventName) {
            Constants.EVENT_ADD_DISH -> {
                logFBAddToCart(params)
            }
            Constants.EVENT_CREATE_ACCOUNT -> {
                logFBCreateAccount(params)
            }
            Constants.EVENT_CLICK_RESTAURANT -> {
                logOnRestaurantClickEvent(enrichedParams)
            }
            Constants.EVENT_CLICK_ON_DISH -> {
                logOnDishClickEvent(enrichedParams)
            }
            Constants.EVENT_TIME_SPENT_IN_ONBOARDING -> {
                trackEventTimeSpentInOnboarding(enrichedParams)
            }
        }
    }

    private fun logToSegment(eventName: String, params: Map<String, Any>?) {
        val segmentProperties = Properties().apply {
            params?.let {
                putAll(it)
            }

        }
        segment.track(eventName, segmentProperties)
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

    override fun reportEvent(event: AnalyticsEvent) {
        this.logEvent(event.name, event.params)
    }
}

fun EatersAnalyticsTracker.logEvent(event: AnalyticsEvent) {
    this.logEvent(event.name, event.params)
}
