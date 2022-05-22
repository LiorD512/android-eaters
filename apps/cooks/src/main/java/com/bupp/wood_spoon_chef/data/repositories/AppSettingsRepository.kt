package com.bupp.wood_spoon_chef.data.repositories

import com.bupp.wood_spoon_chef.analytics.ChefAnalyticsTracker
import com.bupp.wood_spoon_chef.data.remote.model.AppSetting
import com.bupp.wood_spoon_chef.data.remote.model.Price
import timber.log.Timber

data class AppSettings(
    val settings: List<AppSetting>?,
    val ff: Map<String, Boolean>?
)

interface AppSettingsRepository {

    val appSettings: List<AppSetting>
    val featureFlags: Map<String, Boolean>

    fun appSetting(key: String): Any?
    fun featureFlag(key: String): Boolean?
}

fun AppSettingsRepository.stringAppSetting(key: String) = appSetting(key) as? String

fun AppSettingsRepository.booleanAppSetting(key: String) = appSetting(key) as? Boolean

fun AppSettingsRepository.intAppSetting(key: String) = appSetting(key) as? Int

fun AppSettingsRepository.priceAppSetting(key: String) = appSetting(key) as? Price

fun AppSettingsRepository.appSettingOrFeatureFlag(key: String) = featureFlag(key)
        ?: booleanAppSetting(key)

internal class AppSettingsRepositoryImpl(
    private val metaDataRepository: MetaDataRepository,
    private val chefAnalyticsTracker: ChefAnalyticsTracker
) : AppSettingsRepository {

    private fun getAppSettings() = metaDataRepository.getMetaDataObject()?.let {
        Timber.e("MetaDataRepository is not initialized")
        AppSettings(settings = it.settings, ff = it.ff)
    }

    override val appSettings: List<AppSetting>
        get() = getAppSettings()?.settings ?: emptyList()

    override val featureFlags: Map<String, Boolean>
        get() = getAppSettings()?.ff ?: emptyMap()

    override fun appSetting(key: String): Any? {
        val value = appSettings.firstOrNull { it.key == key }?.value
        if (value == null) {
            chefAnalyticsTracker.trackEvent(
                    MissingKeyErrorEventName,
                    mapOf(
                            "key" to key
                    )
            )
        }
        return value
    }

    override fun featureFlag(key: String): Boolean? {
        return featureFlags[key]
    }

    companion object {
        const val MissingKeyErrorEventName = "missing_app_setting_key"
    }
}
