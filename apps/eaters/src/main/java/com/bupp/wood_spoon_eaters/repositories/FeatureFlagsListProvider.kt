package com.bupp.wood_spoon_eaters.repositories

import android.content.Context
import androidx.annotation.Keep

interface FeatureFlagsListProvider {
    fun getFeatureFlagsList(): List<String>
}

interface FeatureFlagKey {
    val key: String
}

fun AppSettingsRepository.featureFlag(key: FeatureFlagKey) = featureFlag(key.key)

fun AppSettingsRepository.Companion.cachedFeatureFlag(key: FeatureFlagKey, context: Context) = cachedFeatureFlag(key.key, context)

@Keep
enum class EatersFeatureFlags(override val key: String) : FeatureFlagKey {
    TestMobileShowBuildNumber(key = "test_mobile_show_build_number"),
    EatersFeeInPrice(key = "backend_eaters_dish_pricing_with_fee"),
    OnboardingDynamicScrollableContent(key = "android_eater_onboarding_dynamic_scrollable_content"),
    MobileEaterLongFeed(key = "mobile_eater_long_feed"),
    GiftIsEnabled(key = "mobile_order_is_gift_enabled"),
    ShowCartSubtotal(key = "show_cart_subtotal"),
    NewAuth(key = "new_auth"),
    FreeDeliveryEnabled(key = "backend_waive_delivery_fee_based_on_subtotal"),
    LogS3Enabled(key = "mobile_log_s3_enabled"),
    LogShipbookEnabled(key = "mobile_log_shipbook_enabled"),
}

class StaticFeatureFlagsListProvider : FeatureFlagsListProvider {

    override fun getFeatureFlagsList() = EatersFeatureFlags.values().map { it.key }
}