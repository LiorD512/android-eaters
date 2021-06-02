package com.bupp.wood_spoon_eaters.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_eaters.databinding.CampaignBannerBinding
import com.bupp.wood_spoon_eaters.databinding.WsEditTextBinding
import com.bupp.wood_spoon_eaters.managers.CampaignManager
import com.bupp.wood_spoon_eaters.model.Campaign
import com.bupp.wood_spoon_eaters.model.CampaignData
import com.bupp.wood_spoon_eaters.utils.Utils

class CampaignBanner @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    var currentCampaign: CampaignData? = null
    private var binding: CampaignBannerBinding = CampaignBannerBinding.inflate(LayoutInflater.from(context), this, true)

    init {
         initUi(attrs)
    }

    var listener: CampaignBannerListener? = null
    interface CampaignBannerListener{
        fun onCampaignDetailsClick(campaign: CampaignData)
    }

    private fun initUi(attrs: AttributeSet?) {
        attrs?.let{

            with(binding){
//                val attr = context.obtainStyledAttributes(attrs, R.styleable.WSEditText)
//
//                attr.recycle()

                campaignBannerDetails.setOnClickListener {
                    currentCampaign?.let{
                        listener?.onCampaignDetailsClick(it)
                    }
                }

            }

        }
    }


    fun initCampaignHeader(campaignData: CampaignData, listener: CampaignBannerListener){
        this.listener = listener
        this.currentCampaign = campaignData

        with(binding){
            campaignData.eater?.let{
                campaignBannerUserImage.setUser(campaignData.eater)
            }
            campaignBannerTitle.text = campaignData.campaign.header
            campaignBannerLayout.setBackgroundColor(Color.parseColor(campaignData.campaign.bannerColor))
            campaignBannerLayout.visibility = View.VISIBLE
        }
    }




}
