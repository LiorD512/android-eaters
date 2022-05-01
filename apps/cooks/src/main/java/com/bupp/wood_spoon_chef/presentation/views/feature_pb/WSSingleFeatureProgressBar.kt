package com.bupp.wood_spoon_chef.presentation.views.feature_pb

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_chef.databinding.WsFeatureSinglePbBinding


class WSSingleFeatureProgressBar @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: WsFeatureSinglePbBinding = WsFeatureSinglePbBinding.inflate(LayoutInflater.from(context), this, true)


    fun setUpObserver() {
        binding.singleFeaturePb.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                startAnimation()
                binding.singleFeaturePb.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    fun startAnimation() {
        val width: Int = binding.singleFeaturePb.width
        binding.singleFeaturePb.max = width
        val animator = ValueAnimator.ofInt(0, width)
        animator.interpolator = LinearInterpolator()
        animator.startDelay = 0
        animator.duration = 500
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            binding.singleFeaturePb.progress = value
        }
        animator.start()
    }
    fun startAnimationOff() {
        val width: Int = binding.singleFeaturePb.width
        binding.singleFeaturePb.max = width
        val animator = ValueAnimator.ofInt(width, 0)
        animator.interpolator = LinearInterpolator()
        animator.startDelay = 0
        animator.duration = 500
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            binding.singleFeaturePb.progress = value
        }
        animator.start()
    }


}
