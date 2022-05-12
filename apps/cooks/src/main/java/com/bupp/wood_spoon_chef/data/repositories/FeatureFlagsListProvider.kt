package com.bupp.wood_spoon_chef.data.repositories

import androidx.annotation.Keep

interface FeatureFlagsListProvider {
    fun getFeatureFlagsList(): List<String>
}

@Suppress("EnumEntryName")
@Keep
enum class CooksFeatureFlags {
    test_mobile_show_build_number,
    mobile_can_chef_cancel_orders
}

class StaticFeatureFlagsListProvider : FeatureFlagsListProvider {

    override fun getFeatureFlagsList() = CooksFeatureFlags.values().map { it.name }
}