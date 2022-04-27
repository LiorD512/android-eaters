package com.bupp.wood_spoon_eaters.views

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.databinding.MapHeaderViewLayoutBinding
import render.animations.Render
import render.animations.Slide

class MapHeaderView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    enum class MapHeaderViewType {
        CORRECT,
        WRONG,
        SHAKE,
    }
    private var currentVerificationStatus: MapHeaderViewType = MapHeaderViewType.CORRECT
    private var binding: MapHeaderViewLayoutBinding = MapHeaderViewLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.mapHeaderView2.alpha = 0f
    }

    fun updateMapHeaderView(verificationStatus: MapHeaderViewType) {
        if (verificationStatus != currentVerificationStatus) {
            when (verificationStatus) {
                MapHeaderViewType.CORRECT -> {
                    currentVerificationStatus = verificationStatus
                    animate2OutAnd1In()
                }
                MapHeaderViewType.WRONG -> {
                    currentVerificationStatus = verificationStatus
                    animate1OutAnd2In()
                }
                MapHeaderViewType.SHAKE -> {
                    shake2()
                }
            }
        }
    }

    private fun animate1OutAnd2In() {
        Log.d(TAG, "animate1OutAnd2In")
        val render = Render(context)
        render.setAnimation(Slide().OutUp(binding.mapHeaderView1))
        render.setDuration(250)
        render.start()

        postDelayed({
            val render2 = Render(context)
            render2.setAnimation(Slide().InDown(binding.mapHeaderView2))
            render2.setDuration(350)
            render2.start()
        }, 250)
    }

    private fun animate2OutAnd1In() {
        Log.d(TAG, "animate2OutAnd1In")
        val render = Render(context)
        render.setAnimation(Slide().OutUp(binding.mapHeaderView2))
        render.setDuration(250)
        render.start()

        postDelayed({
            val render2 = Render(context)
            render2.setAnimation(Slide().InDown(binding.mapHeaderView1))
            render2.setDuration(350)
            render2.start()
        }, 250)
    }

    private fun shake2() {
        ObjectAnimator.ofFloat(
            binding.mapHeaderView2, "translationX",
            0f, -30f, 30.0f, -15.0f, 15.0f, -5.0f, 5.0f, 0f,
        ).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = 0
            start()
        }
    }

    companion object {
        const val TAG = "wowMapHeaderView"
    }
}
