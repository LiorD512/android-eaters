package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_chef.BuildConfig
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.AboutBottomSheetBinding
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.delete_account.DeleteAccountBottomSheet
import com.bupp.wood_spoon_chef.data.repositories.AppSettingsRepository
import com.bupp.wood_spoon_chef.data.repositories.CooksFeatureFlags
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject


class AboutBottomSheet : TopCorneredBottomSheet() {

    private val appSettingsRepository: AppSettingsRepository by inject()

    private var binding: AboutBottomSheetBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.about_bottom_sheet, container, false)
        binding = AboutBottomSheetBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFullScreenDialog()
        initUi()
    }

    private fun initUi() {
        with(binding!!) {
            aboutFragHeader.setOnIconClickListener {
                dismiss()
            }
            aboutFragDeleteAccount.setOnClickListener {
                DeleteAccountBottomSheet().show(childFragmentManager, Constants.DELETE_ACCOUNT_BOTTOM_SHEET)
            }
            aboutFragDeleteVersion.text = buildVersionString()
        }
    }

    private fun buildVersionString(): String {
        val buildNumberString = if (appSettingsRepository.featureFlag(CooksFeatureFlags.test_mobile_show_build_number.name) != false) {
            " (${BuildConfig.VERSION_CODE})"
        } else {
            ""
        }
        return "Version number ${BuildConfig.VERSION_NAME}$buildNumberString"
    }

    override fun clearClassVariables() {
        binding = null
    }

    companion object {
        fun newInstance(): BottomSheetDialogFragment {
            return AboutBottomSheet().apply {
            }
        }
    }

}