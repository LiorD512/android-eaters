package com.bupp.wood_spoon_eaters.views

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import com.bupp.wood_spoon_eaters.databinding.OrderProgressBarBinding
import com.bupp.wood_spoon_eaters.model.OrderState


class OrderProgressBar @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: OrderProgressBarBinding = OrderProgressBarBinding.inflate(LayoutInflater.from(context), this, true)
    var currentState = OrderState.FINALIZED


    fun reset() {
        Log.d(TAG, "reset pb")
        binding.orderPb.progress = 100
    }

    private fun setProgress(orderState: OrderState) {
        Log.d(TAG, "orderState: $orderState")
        val currentProgress = binding.orderPb.progress
        var progress = (100/3F) * (orderState.ordinal - 1)
        if(progress < 0){
            progress = 0f
        }
        Log.d(TAG, "orderState: progress: $progress")
        Log.d(TAG, "orderState: currentProgress: $currentProgress")
        ObjectAnimator.ofInt(
            binding.orderPb, "progress",
            currentProgress, progress.toInt()
        ).apply {
            duration = 250
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }.doOnEnd {
            showLoader(orderState)
        }
        enableIcon(orderState)
    }

    private fun enableIcon(orderState: OrderState) {
        binding.orderPbReceived.isSelected = orderState.ordinal > 0
        binding.orderPbPrepared.isSelected = orderState.ordinal > 1
        binding.orderPbOnTheWay.isSelected = orderState.ordinal > 2
        binding.orderPbDelivered.isSelected = orderState.ordinal > 3
    }

    fun setState(orderState: OrderState) {
        Log.d(TAG, "orderState: $orderState")
        currentState = orderState
        setProgress(orderState)
    }

    private fun showLoader(orderState: OrderState) {
        with(binding) {
            val width = orderPb.width / 6
            val height = orderPbLoader.height
            val layoutParams = LayoutParams(width, height)
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            orderPbLoader.x = orderPb.x + (width * ((orderState.ordinal - 1) * 2 ))
            orderPbLoader.layoutParams = layoutParams
        }
    }

    companion object {
        const val TAG = "wowOrderPb"
    }

}