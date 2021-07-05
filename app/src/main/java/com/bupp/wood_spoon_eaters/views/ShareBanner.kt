package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.databinding.ShareBannerBinding
import com.bupp.wood_spoon_eaters.model.Campaign


class ShareBanner @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ShareBannerBinding = ShareBannerBinding.inflate(LayoutInflater.from(context), this, true)
    private var curCampaign: Campaign? = null
    var listener: WSCustomBannerListener? = null


    interface WSCustomBannerListener {
        fun onShareBannerClick(campaign: Campaign?){}
    }

    fun initCustomBanner(campaign: Campaign, listener: WSCustomBannerListener){
        this.listener = listener
        this.curCampaign = campaign
        handleData(campaign)
    }

    private fun handleData(campaign: Campaign) {
        Glide.with(context).load(campaign.photoLarge).into(binding.customBanner)
        binding.customBannerTitle.text = campaign.header
        binding.customBannerSubTitle.text = campaign.bodyText1

        binding.customBannerLayout.setOnClickListener {
            listener?.onShareBannerClick(curCampaign)
        }
    }

}
