package com.bupp.wood_spoon_eaters.bottom_sheets.free_text_bottom_sheet

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.nationwide_shipping_bottom_sheet.NationwideShippingChooserDialog
import com.bupp.wood_spoon_eaters.databinding.CampaignBottomSheetBinding
import com.bupp.wood_spoon_eaters.databinding.TimePickerBottomSheetBinding
import com.bupp.wood_spoon_eaters.managers.CampaignManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.views.CampaignBanner
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FreeTextBottomSheet() : BottomSheetDialogFragment() {

    private var binding: CampaignBottomSheetBinding? = null
    private var campaignData: CampaignData? = null

    var listener: CampaignBottomSheetListener? = null
    interface CampaignBottomSheetListener{
        fun handleCampaignAction(campaign: Campaign)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTransparentStyle)
        arguments?.let {
            campaignData = it.getParcelable<CampaignData>(CAMPAIGN_ARGS)
        }
    }

    companion object {
        const val CAMPAIGN_ARGS = "CampaignBottomSheetArgs"
        @JvmStatic
        fun newInstance(campaignData: CampaignData) =
            FreeTextBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(CAMPAIGN_ARGS, campaignData)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.campaign_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = CampaignBottomSheetBinding.bind(view)

        initUi()
    }

    private fun initUi() {
        with(binding!!){
            campaignBSBtn.setOnClickListener {
                campaignData?.let{
                    listener?.handleCampaignAction(it.campaign)
                    dismiss()
                }
            }

            campaignData?.campaign?.let{
                campaignBSTitle.text = it.header
                campaignBSSubTitle.text = it.bodyText1
                campaignBSBtn.setBtnText(it.buttonText)
            }

            campaignData?.campaign?.termsAndConditions?.let{
                campaignBSDetails.visibility = View.VISIBLE
                campaignBSDetails.setOnClickListener {

                    }
                }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CampaignBottomSheetListener) {
            listener = context
        }
        else if (parentFragment is CampaignBottomSheetListener){
            this.listener = parentFragment as CampaignBottomSheetListener
        }
        else {
            throw RuntimeException("$context must implement CampaignBottomSheetListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }



}