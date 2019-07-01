package com.bupp.wood_spoon_eaters.features.main.sub_features.settings

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.utils.AppSettings

class SettingsViewModel(private val appSettings: AppSettings) : ViewModel() {

    data class SettingsDetails(
        val enableUserLocation: Boolean = false,
        val enableOrderStatusAlert: Boolean = false,
        val enableCommercialEmails: Boolean = false
    )

    val settingsDetails: SingleLiveEvent<SettingsDetails> = SingleLiveEvent()

    fun setLocationSetting(isEnabled: Boolean) {
        appSettings.shouldEnabledUserLocation = isEnabled
    }

    fun setAlertsSetting(isEnabled: Boolean) {
        appSettings.shouldEnabledOrderStatusAlerts = isEnabled
    }

    fun setEmailSetting(isEnabled: Boolean) {
        appSettings.shouldEnabledCommercialEmails = isEnabled
    }

    fun loadSettings() {
        val useLocation = appSettings.shouldEnabledUserLocation
        val sendAlerts = appSettings.shouldEnabledOrderStatusAlerts
        val sendEmails = appSettings.shouldEnabledCommercialEmails

        settingsDetails.postValue(SettingsDetails(useLocation, sendAlerts, sendEmails))
    }
}