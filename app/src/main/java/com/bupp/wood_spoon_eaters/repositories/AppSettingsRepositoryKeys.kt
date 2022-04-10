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

fun AppSettingsRepository.getTermsOfServiceUrl() = appSetting("terms_url") ?: ""

fun AppSettingsRepository.getPrivacyPolicyUrl() = appSetting("privacy_policy_url") ?: ""

fun AppSettingsRepository.getStripePublishableKey() = appSetting("stripe_publishable_key")

fun AppSettingsRepository.getReportsEmailAddress() = appSetting("client_support_email") ?: ""

// WIP

private fun AppSettingsRepository.getSettings() = appSettings

fun AppSettingsRepository.getUpdateDialogTitle(): String {
    for (settings in getSettings()) {
        if (settings.key == "android_version_control_title")
            return settings.value!! as String
    }
    return ""
}

fun AppSettingsRepository.getUpdateDialogBody(): String {
    for (settings in getSettings()) {
        if (settings.key == "android_version_control_body")
            return settings.value!! as String
    }
    return ""
}

fun AppSettingsRepository.getUpdateDialogUrl(): String {
    for (settings in getSettings()) {
        if (settings.key == "android_version_control_link")
            return settings.value!! as String
    }
    return ""
}

fun AppSettingsRepository.getMinOrderFeeStr(nationwide: Boolean): String {
    if (nationwide) {
        for (settings in getSettings()) {
            if (settings.key == "nationwide_min_order")
                return (settings.value!! as Price).formatedValue as String
        }
    } else {
        for (settings in getSettings()) {
            if (settings.key == "min_order")
                return (settings.value!! as Price).formatedValue as String
        }
    }
    return ""
}

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

fun AppSettingsRepository.getContactUsPhoneNumber(): String {
    for (settings in getSettings()) {
        if (settings.key == "contact_us_number")
            return (settings.value!!) as String
    }
    return ""
}

fun AppSettingsRepository.getContactUsTextNumber(): String {
    for (settings in getSettings()) {
        if (settings.key == "text_message_num")
            return (settings.value!!) as String
    }
    return ""
}

fun AppSettingsRepository.getQaUrl(): String {
    for (settings in getSettings()) {
        if (settings.key == "qa_url")
            return (settings.value!!) as String
    }
    return ""
}

fun AppSettingsRepository.getLocationDistanceThreshold(): Int {
    for (settings in getSettings()) {
        if (settings.key == "location_distance_threshold")
            return (settings.value!!) as Int
    }
    return 20
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

fun AppSettingsRepository.getDefaultFeedLocationName(): String {
    for (settings in getSettings()) {
        if (settings.key == "default_feed_location_name")
            return (settings.value!!) as String
    }
    return ""
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