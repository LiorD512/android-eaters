package com.bupp.wood_spoon_eaters.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.airbnb.lottie.LottieDrawable
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.OrderProgressBarBinding
import com.bupp.wood_spoon_eaters.databinding.WoodspoonProgressBarBinding
import com.bupp.wood_spoon_eaters.model.OrderState


class OrderProgressBar @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: OrderProgressBarBinding = OrderProgressBarBinding.inflate(LayoutInflater.from(context), this, true)
    var currentState = OrderState.NONE

    init {
        initUi()
    }

    private fun initUi() {

    }

    fun setProgress(progress: Int) {
        val currentProgress = binding.OrderPb.progress
        ObjectAnimator.ofInt(
            binding.OrderPb, "progress",
            currentProgress, progress
        ).apply {
            duration = 250
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        when(progress){
            in (0..17) -> {
                enableIcon(binding.orderPbReceived)
            }
            in (18..50) -> {
                enableIcon(binding.orderPbPrepared)
            }
            in (50..83) -> {
                enableIcon(binding.orderPbOnTheWay)
            }
            in (83..100) -> {
                enableIcon(binding.orderPbDelivered)
            }
        }
    }

    fun next() {
        when(currentState){
            OrderState.NONE -> {
                currentState = OrderState.RECEIVED
                setProgress(17) // <- 33/2 - center of the first third
            }
            OrderState.RECEIVED -> {
                currentState = OrderState.PREPARED
                setProgress(50)
            }
            OrderState.PREPARED -> {
                currentState = OrderState.ON_THE_WAY
                setProgress(83) // <- 100 - 17 - center of the last third
            }
            OrderState.ON_THE_WAY -> {
                currentState = OrderState.DELIVERED
                setProgress(100)
            }
            OrderState.DELIVERED -> {

            }
        }
    }

    private fun enableIcon(iconView: ImageView) {
        iconView.isSelected = true
    }

    fun setState(orderState: OrderState) {
        currentState = orderState
        next()
    }


    companion object {
        const val TAG = "wowOrderPb"
    }

}