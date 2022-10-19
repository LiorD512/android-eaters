package com.bupp.wood_spoon_eaters.repositories

import android.content.Context
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.getAppSettings
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.ResultManager
import com.eatwoodspoon.analytics.app_attributes.AppAttributesDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

interface AppSettingsRepository {

    val state: StateFlow<AppSettingsRepoState>
    val appSettings: List<AppSetting>
    val featureFlags: Map<String, Boolean>

    suspend fun initAppSettings(dispatcher: CoroutineDispatcher = Dispatchers.IO)
    fun appSetting(key: String): Any?
    fun featureFlag(key: String): Boolean?
    fun cachedFeatureFlag(key: String): Boolean?

    companion object {
        fun cachedFeatureFlag(key: String, context: Context): Boolean? {
            val featureFlagsLocalDataSource = FeatureFlagLocalDataSourceImpl(context)
            return featureFlagsLocalDataSource.get(key)
        }
    }
}

fun AppSettingsRepository.stringAppSetting(key: String) = appSetting(key) as? String

fun AppSettingsRepository.booleanAppSetting(key: String) = appSetting(key) as? Boolean

fun AppSettingsRepository.intAppSetting(key: String) = appSetting(key) as? Int

fun AppSettingsRepository.priceAppSetting(key: String) = appSetting(key) as? Price

fun AppSettingsRepository.appSettingOrFeatureFlag(key: String) =
    featureFlag(key) ?: booleanAppSetting(key)

sealed class AppSettingsRepoState {
    object NotInitialized : AppSettingsRepoState()

    data class Failed(val reason: ResultHandler<Nothing>) : AppSettingsRepoState()
    data class Success(val appSettings: List<AppSetting>, val featureFlags: Map<String, Boolean>) :
        AppSettingsRepoState()
}

interface FeatureFlagLocalDataSource {
    fun get(key: String): Boolean?
    fun save(featureFlags: Map<String, Boolean>)
}

internal class FeatureFlagLocalDataSourceImpl(context: Context) : FeatureFlagLocalDataSource {

    private val featureFlagsPrefs =
        context.getSharedPreferences(FeatureFlagPrefsName, Context.MODE_PRIVATE)

    override fun get(key: String): Boolean? {
        return if (featureFlagsPrefs.contains(key)) {
            featureFlagsPrefs.getBoolean(key, false)
        } else null
    }

    override fun save(featureFlags: Map<String, Boolean>) {
        featureFlagsPrefs.edit().apply {
            featureFlags.forEach { (key, value) ->
                putBoolean(key, value)
            }
            apply()
        }
    }
}

internal class AppSettingsRepositoryImpl(
    private val apiService: ApiService,
    private val resultManager: ResultManager,
    private val featureListProvider: FeatureFlagsListProvider,
    private val appAttributesDataSource: AppAttributesDataSource,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker,
    private val featureFlagLocalDataSource: FeatureFlagLocalDataSource
) : AppSettingsRepository {


    private val _state = MutableStateFlow<AppSettingsRepoState>(AppSettingsRepoState.NotInitialized)
    override val state: StateFlow<AppSettingsRepoState> = _state

    private suspend fun getAppSetting(): ResultHandler<ServerResponse<AppSettings>> {
        return resultManager.safeApiCall {
            apiService.getAppSettings(
                featureListProvider.getFeatureFlagsList(),
                appAttributesDataSource.appAttributes
            )
        }
    }

    override suspend fun initAppSettings(dispatcher: CoroutineDispatcher) =
        withContext(dispatcher) {
            _state.value = when (val result = getAppSetting()) {
                is ResultHandler.NetworkError -> {
                    Timber.d("initAppSetting - NetworkError")
                    AppSettingsRepoState.Failed(result)
                }
                is ResultHandler.GenericError -> {
                    Timber.d("initAppSetting - GenericError")
                    AppSettingsRepoState.Failed(result)
                }
                is ResultHandler.Success -> {
                    Timber.d("initAppSetting - Success")
                    result.value.data?.ff?.let { featureFlagLocalDataSource.save(it) }
                    AppSettingsRepoState.Success(
                        appSettings = result.value.data?.settings ?: emptyList(),
                        featureFlags = result.value.data?.ff ?: emptyMap()
                    )
                }
                is ResultHandler.WSCustomError -> {
                    Timber.d("initAppSetting - WSCustomError")
                    AppSettingsRepoState.Failed(result)
                }
            }
        }

    override val appSettings: List<AppSetting>
        get() = (state.value as? AppSettingsRepoState.Success)?.appSettings ?: emptyList()

    override val featureFlags: Map<String, Boolean>
        get() = (state.value as? AppSettingsRepoState.Success)?.featureFlags ?: emptyMap()

    override fun appSetting(key: String): Any? {
        val value = appSettings.firstOrNull { it.key == key }?.value
        if (value == null) {
            eatersAnalyticsTracker.logEvent(
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

    override fun cachedFeatureFlag(key: String): Boolean? {
        return featureFlagLocalDataSource.get(key)
    }

    companion object {
        const val MissingKeyErrorEventName = "missing_app_setting_key"
    }
}

private const val FeatureFlagPrefsName = "feature_flags_cache"

