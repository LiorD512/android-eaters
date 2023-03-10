package com.bupp.wood_spoon_eaters.bottom_sheets.campaign_bottom_sheet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.free_text_bottom_sheet.FreeTextBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.CampaignBottomSheetBinding
import com.bupp.wood_spoon_eaters.model.Campaign
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CampaignBottomSheet : BottomSheetDialogFragment() {

    private var binding: CampaignBottomSheetBinding? = null
    private lateinit var campaign: Campaign

    var listener: CampaignBottomSheetListener? = null

    interface CampaignBottomSheetListener {
        fun handleCampaignAction(campaign: Campaign)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTransparentStyle)
        arguments?.let {
            campaign = it.getParcelable(CAMPAIGN_ARGS)!!
        }
    }

    companion object {
        private const val CAMPAIGN_ARGS = "CampaignBottomSheetArgs"

        @JvmStatic
        fun newInstance(campaign: Campaign) =
            CampaignBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(CAMPAIGN_ARGS, campaign)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.campaign_bottom_sheet, container, false)
        binding = CampaignBottomSheetBinding.bind(view)
        return view
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior.isDraggable = false
        }
        return dialog
    }

    //
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        with(binding!!) {
            campaignBSBtn.setOnClickListener {
                listener?.handleCampaignAction(campaign)
                dismiss()
            }

            campaign.let {
                campaignBSTitle.text = it.header
                campaignBSSubTitle.text = it.bodyText1
                campaignBSBtn.setBtnText(it.buttonText)
                it.termsAndConditions?.let { campaignConditions ->
                    campaignBSDetails.visibility = View.VISIBLE
                    campaignBSDetails.setOnClickListener {
                        FreeTextBottomSheet.newInstance(getString(R.string.campaign_full_details_title), campaignConditions)
                            .show(childFragmentManager, Constants.FREE_TEXT_BOTTOM_SHEET)
                    }
                }
                Glide.with(requireContext()).load(campaign.photoSmall).placeholder(R.mipmap.ic_launcher_round).apply(RequestOptions.circleCropTransform())
                    .into(campaignBSImage)
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CampaignBottomSheetListener) {
            listener = context
        } else if (parentFragment is CampaignBottomSheetListener) {
            this.listener = parentFragment as CampaignBottomSheetListener
        } else {
            throw RuntimeException("$context must implement CampaignBottomSheetListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


}