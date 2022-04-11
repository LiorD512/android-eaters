package com.bupp.wood_spoon_eaters.repositories

import android.util.Log
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.getAppSettings
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.bupp.wood_spoon_eaters.network.result_handler.ResultManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

interface AppSettingsRepository {

    val state: StateFlow<AppSettingsRepoState>
    val appSettings: List<AppSetting>
    val featureFlags: Map<String, Boolean>

    suspend fun initAppSettings()
    fun appSetting(key: String): String?
}

sealed class AppSettingsRepoState {
    object NotInitialized : AppSettingsRepoState()

    //    object Loading : AppSettingsRepoState()
    data class Failed(val reason: ResultHandler<Nothing>) : AppSettingsRepoState()
    data class Success(val appSettings: List<AppSetting>, val featureFlags: Map<String, Boolean>) : AppSettingsRepoState()
}

internal class AppSettingsRepositoryImpl(
    private val apiService: ApiService,
    private val resultManager: ResultManager
) : AppSettingsRepository {

    private val _state = MutableStateFlow<AppSettingsRepoState>(AppSettingsRepoState.NotInitialized)
    override val state: StateFlow<AppSettingsRepoState> = _state

    private suspend fun getAppSetting(): ResultHandler<ServerResponse<AppSettings>> {
        return resultManager.safeApiCall {
            apiService.getAppSettings(
                listOf("feature", "feature2", "other_feature")
            )
        }
    }

    override suspend fun initAppSettings() = withContext(Dispatchers.IO) {
        _state.value = when (val result = getAppSetting()) {
            is ResultHandler.NetworkError -> {
                Log.d(TAG, "initAppSetting - NetworkError")
                AppSettingsRepoState.Failed(result)
            }
            is ResultHandler.GenericError -> {
                Log.d(TAG, "initAppSetting - GenericError")
                AppSettingsRepoState.Failed(result)
            }
            is ResultHandler.Success -> {
                Log.d(TAG, "initAppSetting - Success")
                AppSettingsRepoState.Success(
                    appSettings = result.value.data?.settings ?: emptyList(),
                    featureFlags = result.value.data?.ff ?: emptyMap()
                )
            }
            is ResultHandler.WSCustomError -> {
                Log.d(TAG, "initAppSetting - WSCustomError")
                AppSettingsRepoState.Failed(result)
            }
        }
    }

    override val appSettings: List<AppSetting> = (state.value as? AppSettingsRepoState.Success)?.appSettings ?: emptyList()

    override val featureFlags: Map<String, Boolean> = (state.value as? AppSettingsRepoState.Success)?.featureFlags ?: emptyMap()

    override fun appSetting(key: String): String? {
        return appSettings.firstOrNull { it.key == key }?.value?.toString()
    }

    fun featureFlag(key: String): Boolean? {
        return featureFlags[key]
    }

    companion object {
        const val TAG = "AppSettingsRepository"
    }
}
