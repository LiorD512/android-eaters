package com.bupp.wood_spoon_eaters.repositories

import androidx.annotation.Keep

interface FeatureFlagsListProvider {
    fun getFeatureFlagsList(): List<String>
}

interface FeatureFlagKey {
    val key: String
}

fun AppSettingsRepository.featureFlag(key: FeatureFlagKey) = featureFlag(key.key)

@Keep
enum class EatersFeatureFlags(override val key: String) : FeatureFlagKey {
    TestMobileShowBuildNumber(key = "test_mobile_show_build_number"),
    EatersFeeInPrice(key = "backend_eaters_dish_pricing_with_fee")
}

class StaticFeatureFlagsListProvider : FeatureFlagsListProvider {

    override fun getFeatureFlagsList() = EatersFeatureFlags.values().map { it.key }
}