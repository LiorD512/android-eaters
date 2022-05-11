package com.bupp.wood_spoon_chef.managers

import android.content.Context
import android.os.Bundle
import com.bupp.wood_spoon_chef.BuildConfig
import com.bupp.wood_spoon_chef.data.remote.model.Address
import com.bupp.wood_spoon_chef.data.remote.model.Cook
import com.bupp.wood_spoon_chef.utils.DateUtils
import com.google.firebase.analytics.FirebaseAnalytics
import com.segment.analytics.Analytics
import com.segment.analytics.Properties
import com.segment.analytics.Traits
import com.uxcam.UXCam
import io.shipbook.shipbooksdk.ShipBook

//todo 🚩 Keep in mind this ChefAnalyticsTracker treks a sensitive user data like email, phoneNumber, fullName.
class ChefAnalyticsTracker(
    val context: Context,
    private var firebaseAnalytics: FirebaseAnalytics
) {

    fun trackEvent(eventName: String, params: Map<String, Any?>? = null) {
        if (params != null) {
            UXCam.logEvent(eventName, params)
        } else {
            UXCam.logEvent(eventName)
        }

        val eventData = Properties()
        params?.forEach {
            eventData.putValue(it.key, it.value)
        }

        Analytics.with(context).track(eventName, eventData)
    }

    fun trackErrorToFirebase(eventName: String, errorMsg: String) {
        val bundle = Bundle()
        bundle.putString("func_$eventName", errorMsg)
        logToFirebase(bundle)
    }

    fun initSegment(eater: Cook?, address: Address?) {
        eater?.let { user ->
            val userIdString = user.id.toString()
            val unifiedUserIdString = "Cook-$userIdString"

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
    }

    private fun initShipBook(user: Cook) {
        ShipBook.registerUser(
            user.id.toString(),
            null,
            user.getFullName(),
            user.email,
            user.phoneNumber,
            mapOf(Pair("status", user.accountStatus ?: ""))
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
