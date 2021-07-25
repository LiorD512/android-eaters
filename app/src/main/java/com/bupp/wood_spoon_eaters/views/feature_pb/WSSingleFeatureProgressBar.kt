package com.bupp.wood_spoon_eaters.views.feature_pb

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_eaters.databinding.WsFeatureSinglePbBinding
import com.bupp.wood_spoon_eaters.utils.waitForLayout


class WSSingleFeatureProgressBar @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: WsFeatureSinglePbBinding = WsFeatureSinglePbBinding.inflate(LayoutInflater.from(context), this, true)


    fun setUpObserver() {
        binding.singleFeaturePbLayout.waitForLayout {
            startAnimation()
        }
    }

    fun startAnimation() {
        Log.d(TAG, "startAnimation")
        val width: Int = binding.singleFeaturePb.width
        binding.singleFeaturePb.max = width
        val animator = ValueAnimator.ofInt(0, width)
        animator.interpolator = LinearInterpolator()
        animator.startDelay = 0
        animator.duration = 1000
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            binding.singleFeaturePb.progress = value
        }
        animator.start()
    }
    fun startAnimationOff() {
        Log.d(TAG, "startAnimationOff")
        val width: Int = binding.singleFeaturePb.width
        binding.singleFeaturePb.max = width
        val animator = ValueAnimator.ofInt(width, 0)
        animator.interpolator = LinearInterpolator()
        animator.startDelay = 0
        animator.duration = 1000
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            binding.singleFeaturePb.progress = value
        }
        animator.start()
    }

    companion object{
        const val TAG = "wowWSSingleFeatureProgressBar"
    }

}
