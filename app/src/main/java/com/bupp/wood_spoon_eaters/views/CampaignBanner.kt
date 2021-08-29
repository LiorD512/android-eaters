package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.CampaignBannerBinding
import com.bupp.wood_spoon_eaters.model.Campaign

class CampaignBanner @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: CampaignBannerBinding = CampaignBannerBinding.inflate(LayoutInflater.from(context), this, true)

    var listener: CampaignBannerListener? = null

    interface CampaignBannerListener {
        fun onCampaignDetailsClick(campaign: Campaign)
    }

    fun initCampaignHeader(campaign: Campaign, listener: CampaignBannerListener) {
        with(binding) {

            Glide.with(context).load(campaign.photoSmall).placeholder(R.mipmap.ic_launcher_round).apply(RequestOptions.circleCropTransform()).into(campaignBannerImage)
            campaignBannerTitle.text = campaign.header
            campaignBannerLayout.setBackgroundColor(Color.parseColor(campaign.bannerColor))
            campaignBannerLayout.visibility = View.VISIBLE

            campaignBannerDetails.setOnClickListener {
                listener.onCampaignDetailsClick(campaign)
            }
        }
    }

    fun hide(){
        with(binding) {
            campaignBannerLayout.visibility = View.GONE
        }
    }


}
