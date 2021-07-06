package com.bupp.wood_spoon_eaters.features.main.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentSettingsBinding
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment(R.layout.fragment_settings), NotificationsGroupAdapter.NotificationsGroupAdapterListener {

    val binding: FragmentSettingsBinding by viewBinding()
    private lateinit var adapter: NotificationsGroupAdapter
    private val viewModel: SettingsViewModel by viewModel<SettingsViewModel>()
    var lastClickedSwitchId: Long = -1

    companion object{
        fun newInstance() = SettingsFragment()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("Communication settings")

        binding.settingsFragLocationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setLocationSetting(isChecked)
        }

        viewModel.settingsDetails.observe(this, Observer { settings -> loadSettings(settings) })
        viewModel.postNotificationGroup.observe(this, Observer { updateUser -> onUpdateDone(updateUser.isSuccessful) })
        viewModel.loadSettings()

        initNotificationGroup()
    }

    private fun onUpdateDone(successful: Boolean) {
        if(!successful){
            adapter.reverseSwitchThis(lastClickedSwitchId)
        }
    }

    private fun initNotificationGroup() {
        with(binding){
            settingsFragNotificationGroupList.layoutManager = LinearLayoutManager(context)
            val notificationGroupList = viewModel.getNotificationsGroup()
            val userSettings = viewModel.getEaterNotificationsGroup()
            Log.d("wowSettings","userSettings ids: $userSettings")

            adapter = NotificationsGroupAdapter(requireContext(), notificationGroupList, userSettings!!, this@SettingsFragment)
            settingsFragNotificationGroupList.adapter = adapter
        }
    }

    override fun onNotificationChange(notificationGroupId: Long) {
        this.lastClickedSwitchId = notificationGroupId
        viewModel.updateEaterNotificationGroup(notificationGroupId)
    }


    private fun loadSettings(settings: SettingsViewModel.SettingsDetails) {
        binding.settingsFragLocationSwitch.isChecked = settings.enableUserLocation
    }

}