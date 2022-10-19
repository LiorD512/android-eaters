package com.bupp.wood_spoon_chef.data.repositories

import androidx.annotation.Keep

interface FeatureFlagsListProvider {
    fun getFeatureFlagsList(): List<String>
}

@Suppress("EnumEntryName")
@Keep
enum class CooksFeatureFlags {
    test_mobile_show_build_number,
    mobile_can_chef_cancel_orders,
    mobile_cooking_slot_new_flow_enabled,
    mobile_log_s3_enabled,
    mobile_log_shipbook_enabled
}

class StaticFeatureFlagsListProvider : FeatureFlagsListProvider {

    override fun getFeatureFlagsList() = CooksFeatureFlags.values().map { it.name }
}