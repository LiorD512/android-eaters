package com.bupp.wood_spoon_chef.analytics

import android.content.Context
import android.os.Bundle
import com.bupp.wood_spoon_chef.BuildConfig
import com.bupp.wood_spoon_chef.data.remote.model.Address
import com.bupp.wood_spoon_chef.data.remote.model.Cook
import com.bupp.wood_spoon_chef.utils.DateUtils
import com.eatwoodspoon.analytics.SessionId
import com.eatwoodspoon.analytics.events.AnalyticsEvent
import com.eatwoodspoon.logsender.Logger
import com.google.firebase.analytics.FirebaseAnalytics
import com.segment.analytics.Analytics
import com.segment.analytics.Properties
import com.segment.analytics.Traits
import com.uxcam.UXCam
import io.shipbook.shipbooksdk.ShipBook

//todo 🚩 Keep in mind this ChefAnalyticsTracker treks a sensitive user data like email, phoneNumber, fullName.
class ChefAnalyticsTracker(
    val context: Context,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val logSender: Logger
    ) {

    private var currentUserId: String? = null

    fun trackEvent(eventName: String, params: Map<String, Any?>? = null) {

        val enrichedParams = enrichParameters(eventName, params).filterValues { it != null } as Map<String, Any>

        val effectiveParams = mutableMapOf<String, Any?>(
            "session_id" to SessionId.value
        ).apply {
            params?.let { putAll(it) }
        }

        if (effectiveParams != null) {
            UXCam.logEvent(eventName, effectiveParams)
        } else {
            UXCam.logEvent(eventName)
        }

        Analytics.with(context).track(eventName, Properties().apply {
            putAll(effectiveParams)
        })

        //WS
        logSender.log(enrichedParams)
    }

    fun trackErrorToFirebase(eventName: String, errorMsg: String) {
        val bundle = Bundle()
        bundle.putString("func_$eventName", errorMsg)
        logToFirebase(bundle)
    }

    private fun enrichParameters(eventName: String, params: Map<String, Any?>?): Map<String, Any?> {
        return mutableMapOf<String, Any?>(
            "user_id" to currentUserId,
            "name" to eventName
        ).apply {
            params?.let { putAll(it) }
        }.toMap()
    }

    fun initSegment(eater: Cook?, address: Address?) {
        eater?.let { user ->
            val userIdString = user.id.toString()
            val unifiedUserIdString = "Cook-$userIdString"
            currentUserId = userIdString

            val traits = Traits()
                .putName(user.getFullName())
                .putEmail(user.email)
                .putPhone(user.phoneNumber)

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

            initUXCam(user)
            initShipBook(user)
        }
    }

    private fun initUXCam(user: Cook) {
        UXCam.setUserIdentity(user.id.toString())
        UXCam.setUserProperty("email", user.email ?: "N/A")
        UXCam.setUserProperty("name", user.getFullName())
        UXCam.setUserProperty("phone", user.phoneNumber ?: "N/A")
        UXCam.setUserProperty("created_at", DateUtils.parseDateToDate(user.joinDate))
        UXCam.setUserProperty("session_id", SessionId.value)
    }

    private fun initShipBook(user: Cook) {
        ShipBook.registerUser(
            userId = user.id.toString(),
            userName = null,
            fullName = user.getFullName(),
            email = user.email,
            phoneNumber = user.phoneNumber,
            additionalInfo = mapOf(
                "status" to (user.accountStatus ?: ""),
                "session_id" to SessionId.value
            )
        )
    }

    private fun logToFirebase(bundle: Bundle) {
        if (BuildConfig.DEBUG) {
            firebaseAnalytics.logEvent("Error_Staging", bundle)
        } else {
            firebaseAnalytics.logEvent("Error_Prod", bundle)
        }
    }

    fun logout() {
        ShipBook.logout()
    }
}

fun ChefAnalyticsTracker.trackEvent(event: AnalyticsEvent) {
    this.trackEvent(event.name, event.params)
}

