package com.bupp.wood_spoon_eaters.features.free_delivery

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FreeDeliveryProgressViewBinding
import com.bupp.wood_spoon_eaters.model.Price
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.views.LottieOverlay

class FreeDeliveryProgressView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val binding =
        FreeDeliveryProgressViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var previousUntilFreeDelivery: Price? = null
    private var listener: FreeDeliveryProgressViewListener? = null

    interface FreeDeliveryProgressViewListener {
        fun thresholdAchieved()
        fun viewClicked()
    }

    init {
        hide()
    }

    fun setFreeDeliveryProgressViewListener(listener: FreeDeliveryProgressViewListener) {
        this.listener = listener
        binding.freeDeliveryProgressViewMainLayout.setOnClickListener {
            listener.viewClicked()
        }
    }

    fun setFreeDeliveryState(freeDeliveryState: FreeDeliveryState?) {
        with(binding) {
            if (freeDeliveryState != null) {
                freeDeliveryProgressViewUntilFreeTxt.text =
                    freeDeliveryState.currentThreshold?.formatedValue
                freeDeliveryProgressViewPb.max =
                    freeDeliveryState.currentThreshold?.value?.toInt() ?: 49
                checkUntilFreeDeliveryValue(
                    freeDeliveryState.untilFreeDelivery,
                    freeDeliveryState.showAddMoreItemsBtn
                )
                freeDeliveryProgressViewSubtotalTxt.text = freeDeliveryState.subtotal?.formatedValue
                val progress = freeDeliveryState.currentThreshold?.value?.minus(
                    freeDeliveryState.untilFreeDelivery?.value ?: 0.0
                ) ?: 0
                setFreeDeliveryProgress(progress.toInt())
                if ((!freeDeliveryProgressViewMainLayout.isVisible)) {
                    show()
                }
            } else {
                hide()
            }
        }
    }

    private fun setFreeDeliveryProgress(progress: Int) {
        with(binding) {
            ObjectAnimator.ofInt(freeDeliveryProgressViewPb, "progress", progress)
                .setDuration(300)
                .start()
        }
    }

    private fun checkUntilFreeDeliveryValue(
        untilFreeDelivery: Price?,
        showAddMoreItemsBtn: Boolean
    ) {
        with(binding) {
            if (untilFreeDelivery?.value == 0.0) {
                if (freeDeliveryViewSwitcher.currentView != freeDeliveryProgressViewCompletedTitle) {
                    freeDeliveryViewSwitcher.showNext()
                }
                freeDeliveryProgressViewAddItemsBtn.isVisible = false
                if (previousUntilFreeDelivery != untilFreeDelivery && previousUntilFreeDelivery != null) {
                    LottieOverlay(context).showLottieOverlay(
                        freeDeliveryViewSwitcher,
                        "free_delivery_lottie.json"
                    )
                    freeDeliveryProgressViewMainLayout.isHapticFeedbackEnabled = true
                    freeDeliveryProgressViewMainLayout.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    listener?.thresholdAchieved()
                }
            } else {
                if (freeDeliveryViewSwitcher.currentView != freeDeliveryNotCompletedLayout) {
                    freeDeliveryViewSwitcher.showPrevious()
                }
                freeDeliveryProgressViewNotCompletedTitle.text = context.getString(
                    R.string.free_delivery_amount_remain_message,
                    untilFreeDelivery?.formatedValue
                )
                freeDeliveryProgressViewAddItemsBtn.isVisible = showAddMoreItemsBtn
            }
            previousUntilFreeDelivery = untilFreeDelivery
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

    fun setAddItemsClickListener(clickListener: () -> Unit) {
        binding.freeDeliveryProgressViewAddItemsBtn.setOnClickListener { clickListener() }
    }

}