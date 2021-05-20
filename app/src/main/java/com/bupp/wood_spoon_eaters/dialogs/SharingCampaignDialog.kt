package com.bupp.wood_spoon_eaters.dialogs

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.SharingCampaignDialogBinding
import com.bupp.wood_spoon_eaters.model.Campaign
import com.google.android.material.shape.CornerFamily


class SharingCampaignDialog() : DialogFragment(){

    lateinit var binding: SharingCampaignDialogBinding
    var curCampaign: Campaign? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
        arguments?.let {
            curCampaign = it.getParcelable(CAMPAIGN_ARGS)
        }
    }

    companion object {
        const val CAMPAIGN_ARGS = "campaign"
        @JvmStatic
        fun newInstance(campaign: Campaign) =
            SharingCampaignDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(CAMPAIGN_ARGS, campaign)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.sharing_campaign_dialog, null)
        getDialog()!!.getWindow()?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)));
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SharingCampaignDialogBinding.bind(view)

        initUi()
}

    private fun initUi() {
        with(binding){
            sharingCampaignDialogShareBtn.setOnClickListener{
                shareText(getShareText())
                dismiss()
            }
            sharingCampaignDialogCloseBtn.setOnClickListener{
                dismiss()}
    //        dialogBkg.setOnClickListener{
    //            dismiss()}

            curCampaign?.let{
                val radius = resources.getDimension(R.dimen.default_corner_radius)
                sharingCampaignDialogCover.setShapeAppearanceModel(
                    sharingCampaignDialogCover.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopRightCorner(CornerFamily.ROUNDED, radius)
                        .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                        .build()
                )
                Glide.with(requireContext()).load(it.thumbnail).into(sharingCampaignDialogCover)

                sharingCampaignDialogBody.text = it.description
                it.shareBtnText?.let{
                    sharingCampaignDialogShareBtn.text = it
                }
            }
        }

    }

    fun getShareText(): String {
        val inviteUrl = curCampaign?.inviteUrl
//        val text = curCampaign?.description
        val text = "${curCampaign?.shareText}"// \n ${curCampaign?.description}"
        return "$text \n $inviteUrl"
    }

    fun shareText(text: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(shareIntent, "Share"))
    }

}