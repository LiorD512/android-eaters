package com.bupp.wood_spoon_chef.data.repositories

import android.content.Context
import com.bupp.wood_spoon_chef.analytics.ChefAnalyticsTracker
import com.bupp.wood_spoon_chef.data.remote.model.AppSetting
import com.bupp.wood_spoon_chef.data.remote.model.Price
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
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

    companion object {
        fun cachedFeatureFlag(key: String, context: Context): Boolean? {
            val featureFlagsCache =
                context.getSharedPreferences(FeatureFlagPrefsName, Context.MODE_PRIVATE)
            return if (featureFlagsCache.contains(key)) {
                featureFlagsCache.getBoolean(key, false)
            } else null
        }
    }
}

fun AppSettingsRepository.stringAppSetting(key: String) = appSetting(key) as? String

fun AppSettingsRepository.booleanAppSetting(key: String) = appSetting(key) as? Boolean

fun AppSettingsRepository.intAppSetting(key: String) = appSetting(key) as? Int

fun AppSettingsRepository.priceAppSetting(key: String) = appSetting(key) as? Price

fun AppSettingsRepository.appSettingOrFeatureFlag(key: String) = featureFlag(key)
    ?: booleanAppSetting(key)

fun AppSettingsRepository.Companion.cachedFeatureFlag(key: CooksFeatureFlags, context: Context) =
    cachedFeatureFlag(key.name, context)

internal class AppSettingsRepositoryImpl(
    private val metaDataRepository: MetaDataRepository,
    private val chefAnalyticsTracker: ChefAnalyticsTracker,
    context: Context,
    scope: CoroutineScope = GlobalScope
) : AppSettingsRepository {

    private val featureFlagsCache =
        context.getSharedPreferences(FeatureFlagPrefsName, Context.MODE_PRIVATE)

    init {
        scope.launch {
            metaDataRepository.metaDataStateFlow.mapNotNull { it?.ff }.collect {
                updateFeatureFlagsCache(it)
            }
        }
    }

    private fun getAppSettings() = metaDataRepository.getMetaDataObject()?.let {
        Timber.e("MetaDataRepository is not initialized")
        AppSettings(settings = it.settings, ff = it.ff)
    }

    private fun updateFeatureFlagsCache(featureFlags: Map<String, Boolean>) {
        featureFlagsCache.edit().clear().apply {
            featureFlags.forEach { (key, value) ->
                putBoolean(key, value)
            }
        }.apply()
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

private const val FeatureFlagPrefsName = "feature_flags"
