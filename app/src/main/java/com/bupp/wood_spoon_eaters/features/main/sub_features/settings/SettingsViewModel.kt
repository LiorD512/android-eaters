package com.bupp.wood_spoon_eaters.features.main.sub_features.settings

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.utils.AppSettings

class SettingsViewModel(private val appSettings: AppSettings) : ViewModel() {

    data class SettingsDetails(
        val useLocation: Boolean = false,
        val sendAlerts: Boolean = false,
        val sendEmails: Boolean = false
    )

    val settingsDetails: SingleLiveEvent<SettingsDetails> = SingleLiveEvent()

    fun setLocationSetting(isEnabled: Boolean) {
        appSettings.setUseLocation(isEnabled)
    }

    fun setAlertsSetting(isEnabled: Boolean) {
        appSettings.setAlerts(isEnabled)
    }

    fun setEmailSetting(isEnabled: Boolean) {
        appSettings.setEmails(isEnabled)
    }

    fun loadSettings() {
        val useLocation = appSettings.getUseLocation()
        val sendAlerts = appSettings.getAlerts()
        val sendEmails = appSettings.getEmails()

        settingsDetails.postValue(SettingsDetails(useLocation, sendAlerts, sendEmails))
    }
}