package com.bupp.wood_spoon_eaters.features.free_delivery

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FreeDeliveryProgressViewBinding
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.Price

class FreeDeliveryProgressView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val binding =
        FreeDeliveryProgressViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var currentThreshold: Price? = null


    fun setFreeDeliveryThreshold(deliveryFeeThreshold: Price?) {
        currentThreshold = deliveryFeeThreshold
        with(binding) {
            freeDeliveryProgressViewUntilFreeTxt.text = deliveryFeeThreshold?.formatedValue
            freeDeliveryProgressViewPb.max = deliveryFeeThreshold?.value?.toInt() ?: 49
        }
    }

    fun setOrder(order: Order?) {
        with(binding) {
            compareThresholdToSubtotal(order)
            freeDeliveryProgressViewSubtotalTxt.text = order?.subtotal?.formatedValue
            setFreeDeliveryProgress(order?.subtotal?.value?.toInt() ?: 0)
        }
    }

    private fun setFreeDeliveryProgress(progress: Int) {
        with(binding) {
            freeDeliveryProgressViewPb.progress = progress
        }
    }

    private fun compareThresholdToSubtotal(order: Order?){
        with(binding){
            val subtotal = order?.subtotal?.value ?: 0.0
            val deliveryFee = currentThreshold?.value ?: 0.0
            if (subtotal >= deliveryFee) {
                freeDeliveryProgressViewTitle.text =
                    context.getString(R.string.free_delivery_getting_free_delivery_message)
                freeDeliveryProgressViewCheckImg.isVisible = true
            } else {
                freeDeliveryProgressViewTitle.text = context.getString(
                    R.string.free_delivery_amount_remain_message,
                    order?.untilFreeDelivery?.formatedValue
                )
                freeDeliveryProgressViewCheckImg.isVisible = false
            }
        }
    }


}