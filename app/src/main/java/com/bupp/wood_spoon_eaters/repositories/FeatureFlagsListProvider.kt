package com.bupp.wood_spoon_eaters.repositories

interface FeatureFlagsListProvider {
    fun getFeatureFlagsList(): List<String>
}

@Suppress("EnumEntryName")
enum class EatersFeatureFlags {
    test_mobile_show_build_number
}

class StaticFeatureFlagsListProvider : FeatureFlagsListProvider {

    override fun getFeatureFlagsList() = EatersFeatureFlags.values().map { it.name }
}