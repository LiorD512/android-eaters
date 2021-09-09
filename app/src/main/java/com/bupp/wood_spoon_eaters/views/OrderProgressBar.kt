package com.bupp.wood_spoon_eaters.views

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.bupp.wood_spoon_eaters.databinding.OrderProgressBarBinding
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
        Log.d(TAG, "orderPb: $progress")
        disableAllView()
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
            in (1..17) -> {
                enableIcon(binding.orderPbReceived)
            }
            in (18..50) -> {
                enableIcon(binding.orderPbReceived)
                enableIcon(binding.orderPbPrepared)
            }
            in (50..83) -> {
                enableIcon(binding.orderPbReceived)
                enableIcon(binding.orderPbPrepared)
                enableIcon(binding.orderPbOnTheWay)
            }
            in (83..100) -> {
                enableIcon(binding.orderPbReceived)
                enableIcon(binding.orderPbPrepared)
                enableIcon(binding.orderPbOnTheWay)
                enableIcon(binding.orderPbDelivered)
            }
        }
    }

    private fun disableAllView() {
        binding.orderPbReceived.isSelected = false
        binding.orderPbPrepared.isSelected = false
        binding.orderPbOnTheWay.isSelected = false
        binding.orderPbDelivered.isSelected = false
    }

    private fun enableIcon(iconView: ImageView) {
        iconView.isSelected = true
    }

    fun setState(orderState: OrderState) {
        Log.d(TAG, "orderState: $orderState")
        currentState = orderState
        when(currentState){
            OrderState.NONE -> {
                currentState = OrderState.NONE
                setProgress(0) // <- 33/2 - center of the first third
            }
            OrderState.RECEIVED -> {
                currentState = OrderState.RECEIVED
                setProgress(17) // <- 33/2 - center of the first third
            }
            OrderState.PREPARED -> {
                currentState = OrderState.PREPARED
                setProgress(50)
            }
            OrderState.ON_THE_WAY -> {
                currentState = OrderState.ON_THE_WAY
                setProgress(83) // <- 100 - 17 - center of the last third
            }
            OrderState.DELIVERED -> {
                currentState = OrderState.DELIVERED
                setProgress(100)
            }
        }
    }


    companion object {
        const val TAG = "wowOrderPb"
    }

}