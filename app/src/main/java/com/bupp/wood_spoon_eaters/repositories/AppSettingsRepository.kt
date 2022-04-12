package com.bupp.wood_spoon_eaters.repositories

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.getAppSettings
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.ResultManager
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
}

fun AppSettingsRepository.stringAppSetting(key: String) = appSetting(key) as? String

fun AppSettingsRepository.booleanAppSetting(key: String) = appSetting(key) as? Boolean

fun AppSettingsRepository.intAppSetting(key: String) = appSetting(key) as? Int

fun AppSettingsRepository.priceAppSetting(key: String) = appSetting(key) as? Price

fun AppSettingsRepository.appSettingOrFeatureFlag(key: String) = featureFlag(key) ?: booleanAppSetting(key)

sealed class AppSettingsRepoState {
    object NotInitialized : AppSettingsRepoState()

    data class Failed(val reason: ResultHandler<Nothing>) : AppSettingsRepoState()
    data class Success(val appSettings: List<AppSetting>, val featureFlags: Map<String, Boolean>) : AppSettingsRepoState()
}

internal class AppSettingsRepositoryImpl(
    private val apiService: ApiService,
    private val resultManager: ResultManager,
    private val featureListProvider: FeatureFlagsListProvider
) : AppSettingsRepository {

    private val _state = MutableStateFlow<AppSettingsRepoState>(AppSettingsRepoState.NotInitialized)
    override val state: StateFlow<AppSettingsRepoState> = _state

    private suspend fun getAppSetting(): ResultHandler<ServerResponse<AppSettings>> {
        return resultManager.safeApiCall {
            apiService.getAppSettings(featureListProvider.getFeatureFlagsList())
        }
    }

    override suspend fun initAppSettings(dispatcher: CoroutineDispatcher) = withContext(dispatcher) {
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
        return appSettings.firstOrNull { it.key == key }?.value
    }

    override fun featureFlag(key: String): Boolean? {
        return featureFlags[key]
    }
}
