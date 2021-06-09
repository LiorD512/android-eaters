package com.bupp.wood_spoon_eaters.bottom_sheets.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.SettingsBottomSheetBinding
import com.bupp.wood_spoon_eaters.features.main.settings.NotificationsGroupAdapter
import com.bupp.wood_spoon_eaters.features.main.settings.SettingsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsBottomSheet: BottomSheetDialogFragment(), NotificationsGroupAdapter.NotificationsGroupAdapterListener {

    private lateinit var binding: SettingsBottomSheetBinding
    private lateinit var adapter: NotificationsGroupAdapter
    private val viewModel: SettingsViewModel by viewModel<SettingsViewModel>()
    var lastClickedSwitchId: Long = -1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_bottom_sheet, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SettingsBottomSheetBinding.bind(view)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.bottom_sheet_bkg)

        Analytics.with(requireContext()).screen("Communication settings")

        binding.settingsFragLocationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setLocationSetting(isChecked)
        }

        viewModel.settingsDetails.observe(viewLifecycleOwner, { settings -> loadSettings(settings) })
        viewModel.postNotificationGroup.observe(viewLifecycleOwner, { updateUser -> onUpdateDone(updateUser.isSuccessful) })
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

            adapter = NotificationsGroupAdapter(requireContext(), notificationGroupList, userSettings!!, this@SettingsBottomSheet)
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