package com.bupp.wood_spoon_eaters.repositories

import android.util.Log
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.model.CloudinaryTransformations
import com.bupp.wood_spoon_eaters.model.CloudinaryTransformationsType
import com.bupp.wood_spoon_eaters.model.Price
import java.math.BigDecimal

/**
 * These extension functions should be a part of corresponding features
 */

fun AppSettingsRepository.getTermsOfServiceUrl() = stringAppSetting("terms_url") ?: ""

fun AppSettingsRepository.getPrivacyPolicyUrl() = stringAppSetting("privacy_policy_url") ?: ""

fun AppSettingsRepository.getStripePublishableKey() = stringAppSetting("stripe_publishable_key")

fun AppSettingsRepository.getReportsEmailAddress() = stringAppSetting("client_support_email") ?: ""

fun AppSettingsRepository.getUpdateDialogTitle() = stringAppSetting("android_version_control_title") ?: ""

fun AppSettingsRepository.getUpdateDialogBody() = stringAppSetting("android_version_control_body") ?: ""

fun AppSettingsRepository.getUpdateDialogUrl() = stringAppSetting("android_version_control_link") ?: ""

fun AppSettingsRepository.getMinOrderFeeStr(nationwide: Boolean): String {
    return priceAppSetting(if (nationwide) "nationwide_min_order" else "min_order")?.formatedValue ?: ""
}

fun AppSettingsRepository.getCurrentFreeDeliveryThreshold(): Price?{
    return priceAppSetting("free_delivery_order_subtotal")
}

fun AppSettingsRepository.getQaUrl() = stringAppSetting("qa_url") ?: ""

fun AppSettingsRepository.getContactUsPhoneNumber() = stringAppSetting("contact_us_number") ?: ""

fun AppSettingsRepository.getContactUsTextNumber() = stringAppSetting("text_message_num") ?: ""

fun AppSettingsRepository.getLocationDistanceThreshold() = intAppSetting("location_distance_threshold") ?: 20

fun AppSettingsRepository.getDefaultFeedLocationName() = stringAppSetting("default_feed_location_name") ?: ""

// WIP

private fun AppSettingsRepository.getSettings() = appSettings

private fun AppSettingsRepository.getMinAndroidVersion(): String {
    for (settings in getSettings()) {
        if (settings.key == "eaters_min_android_version")
            return (settings.value!!) as String
    }
    return ""
}

fun AppSettingsRepository.checkMinVersionFail(): Boolean {
    val minVersion = getMinAndroidVersion()
    Log.d("wowMetaDataRepo", "minimum version: $minVersion")
    minVersion.let {
        val versionName = BuildConfig.VERSION_NAME

        val myCurrVersion = getNumberFromStr(versionName)
        val minimumVersion = getNumberFromStr(minVersion)
        Log.d("wowMetaDataRepo", "curVersion: $myCurrVersion, minimum version: $minimumVersion")
        return myCurrVersion < minimumVersion
    }
}

private fun getNumberFromStr(str: String): Int {
    var versionNumber = 0
    val numParts = str.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    if (numParts.size in 1..3) {
        var multiplier = 1
        for (i in numParts.indices.reversed()) {
            versionNumber += Integer.parseInt(numParts[i].replace("\"", "")) * multiplier
            multiplier *= 1000
        }
    }
    return versionNumber
}

fun AppSettingsRepository.getMinFutureOrderWindow(): Int {
    for (settings in getSettings()) {
        if (settings.key == "min_future_order_window")
            return (settings.value!!) as Int
    }
    return 60
}

fun AppSettingsRepository.getDefaultLat(): Double {
    for (settings in getSettings()) {
        if (settings.key == "default_feed_lat")
            return ((settings.value!!) as BigDecimal).toDouble()
    }
    return 0.0
}

fun AppSettingsRepository.getDefaultLng(): Double {
    for (settings in getSettings()) {
        if (settings.key == "default_feed_lng")
            return ((settings.value!!) as BigDecimal).toDouble()
    }
    return 0.0
}



fun AppSettingsRepository.getCloudinaryTransformations(): CloudinaryTransformations? {
    for (settings in getSettings()) {
        if (settings.key == "cloudinary_transformations") {
            val cloudinaryMap = settings.value as Map<*, *>?
            cloudinaryMap?.let {
                return CloudinaryTransformations(cloudinaryMap as Map<CloudinaryTransformationsType, String>?)
            }
        }
    }
    return null
}


fun AppSettingsRepository.getOnboardingSlideDelay(): Double {
//    for (settings in getSettings()) {
//        if (settings.key == "mobile_onboarding_animation_delay")
//            return ((settings.value!!) as BigDecimal).toDouble()
//    }
    return 0.0
}