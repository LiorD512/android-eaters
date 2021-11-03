package com.bupp.wood_spoon_eaters.bottom_sheets.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.SettingsBottomSheetBinding
import com.bupp.wood_spoon_eaters.features.main.settings.NotificationsGroupAdapter
import com.bupp.wood_spoon_eaters.features.main.settings.SettingsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsBottomSheet: BottomSheetDialogFragment(), NotificationsGroupAdapter.NotificationsGroupAdapterListener, HeaderView.HeaderViewListener {

    private var binding: SettingsBottomSheetBinding? = null
    private var adapter: NotificationsGroupAdapter? = null
    private val viewModel: SettingsViewModel by viewModel()
    var lastClickedSwitchId: Long = -1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.settings_bottom_sheet, container, false)
        binding = SettingsBottomSheetBinding.bind(view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.bottom_sheet_bkg)

//        Analytics.with(requireContext()).screen("Communication settings")
        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_COMMUNICATION_SETTINGS)

        binding!!.settingsFragHeader.setHeaderViewListener(this)

        binding!!.settingsFragLocationSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setLocationSetting(isChecked)
        }

        viewModel.settingsDetails.observe(viewLifecycleOwner, { settings -> loadSettings(settings) })
        viewModel.postNotificationGroup.observe(viewLifecycleOwner, { updateUser -> onUpdateDone(updateUser.isSuccessful) })
        viewModel.loadSettings()

        initNotificationGroup()
    }

    private fun onUpdateDone(successful: Boolean) {
        if(!successful){
            adapter?.reverseSwitchThis(lastClickedSwitchId)
        }
    }

    private fun initNotificationGroup() {
        with(binding!!){
            settingsFragNotificationGroupList.layoutManager = LinearLayoutManager(context)
            val notificationGroupList = viewModel.getNotificationsGroup()
            val userSettings = viewModel.getEaterNotificationsGroup()
            Log.d("wowSettings","userSettings ids: $userSettings")

            adapter = NotificationsGroupAdapter(requireContext(), notificationGroupList, userSettings!!, this@SettingsBottomSheet)
            settingsFragNotificationGroupList.adapter = adapter
        }
    }

    override fun onNotificationChange(notificationGroupIds: List<Long>) {
//        this.lastClickedSwitchId = notificationGroupId
        viewModel.updateEaterNotificationGroup(notificationGroupIds)
    }


    private fun loadSettings(settings: SettingsViewModel.SettingsDetails) {
        binding!!.settingsFragLocationSwitch.isChecked = settings.enableUserLocation
        binding!!.settingsFragLocationSwitch.jumpDrawablesToCurrentState()
    }

    override fun onHeaderCloseClick() {
        dismiss()
    }

    override fun onDestroyView() {
        adapter = null
        binding = null
        super.onDestroyView()
    }
}