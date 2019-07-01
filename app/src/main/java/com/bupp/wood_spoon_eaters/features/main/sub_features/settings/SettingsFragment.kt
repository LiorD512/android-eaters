package com.bupp.wood_spoon_eaters.features.main.sub_features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsFragLocationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setLocationSetting(isChecked)
        }
        settingsFragAlertsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setAlertsSetting(isChecked)
        }
        settingsFragEmailSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setEmailSetting(isChecked)
        }

        viewModel.settingsDetails.observe(this, Observer { settings -> loadSettings(settings) })
        viewModel.loadSettings()
    }

    private fun loadSettings(settings: SettingsViewModel.SettingsDetails) {
        settingsFragLocationSwitch.isChecked = settings.enableUserLocation
        settingsFragAlertsSwitch.isChecked = settings.enableOrderStatusAlert
        settingsFragEmailSwitch.isChecked = settings.enableCommercialEmails
    }

}