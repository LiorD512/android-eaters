package com.bupp.wood_spoon_eaters.features.free_delivery

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FreeDeliveryProgressViewBinding
import com.bupp.wood_spoon_eaters.model.Price
import com.bupp.wood_spoon_eaters.utils.AnimationUtil

class FreeDeliveryProgressView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val binding =
        FreeDeliveryProgressViewBinding.inflate(LayoutInflater.from(context), this, true)


    init {
        hide()
    }


    fun setFreeDeliveryState(freeDeliveryState: FreeDeliveryState?) {
        with(binding) {
            if (freeDeliveryState != null){
                freeDeliveryProgressViewUntilFreeTxt.text =
                    freeDeliveryState.currentThreshold?.formatedValue
                freeDeliveryProgressViewPb.max =
                    freeDeliveryState.currentThreshold?.value?.toInt() ?: 49
                compareThresholdToSubtotal(
                    freeDeliveryState.subtotal?.value ?: 0.0,
                    freeDeliveryState.untilFreeDelivery,
                    freeDeliveryState.currentThreshold
                )
                freeDeliveryProgressViewSubtotalTxt.text = freeDeliveryState.subtotal?.formatedValue
                setFreeDeliveryProgress(freeDeliveryState.subtotal?.value?.toInt() ?: 0)
                if ((!freeDeliveryProgressViewMainLayout.isVisible)){
                    show()
                }
            }else{
                hide()
            }
        }
    }

    private fun setFreeDeliveryProgress(progress: Int) {
        with(binding) {
            freeDeliveryProgressViewPb.progress = progress
        }
    }

    private fun compareThresholdToSubtotal(
        subtotal: Double, untilFreeDelivery: Price?,
        currentThreshold: Price?
    ) {
        with(binding) {
            val deliveryFee = currentThreshold?.value ?: 0.0
            if (subtotal >= deliveryFee) {
                freeDeliveryProgressViewTitle.text =
                    context.getString(R.string.free_delivery_getting_free_delivery_message)
                freeDeliveryProgressViewCheckImg.isVisible = true
            } else {
                freeDeliveryProgressViewTitle.text = context.getString(
                    R.string.free_delivery_amount_remain_message,
                    untilFreeDelivery?.formatedValue
                )
                freeDeliveryProgressViewCheckImg.isVisible = false
            }
        }
    }

    fun show() {
        binding.freeDeliveryProgressViewMainLayout.isVisible = true
        AnimationUtil().enterFromBottomWithAlpha(binding.freeDeliveryProgressViewMainLayout)
    }

    fun hide() {
        binding.freeDeliveryProgressViewMainLayout.isVisible = false
        AnimationUtil().exitToBottomWithAlpha(binding.freeDeliveryProgressViewMainLayout)
    }

}