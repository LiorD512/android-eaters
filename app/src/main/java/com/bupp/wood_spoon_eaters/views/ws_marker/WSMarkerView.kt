package com.bupp.wood_spoon_eaters.views.ws_marker

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator.*
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.databinding.WsMarkerViewBinding

class WSMarkerView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: WsMarkerViewBinding = WsMarkerViewBinding.inflate(LayoutInflater.from(context), this, true)


    @SuppressLint("ObjectAnimatorBinding")
    fun pulse(view: View) {

        val scaleX = PropertyValuesHolder.ofFloat(
            View.SCALE_X, 0.0f, 1.0f)
        val scaleY = PropertyValuesHolder.ofFloat(
            View.SCALE_Y, 0.0f, 1.0f)
        val alpha = PropertyValuesHolder.ofFloat(
            "alpha",1f, 0.1f)
        val textColor = PropertyValuesHolder.ofInt(
            "textColor",
            Color.parseColor("#0097a3"),
            Color.parseColor("#337ccbd2")
        )
        textColor.setEvaluator(ArgbEvaluator())

        ObjectAnimator.ofPropertyValuesHolder(view,
            scaleX, scaleY, alpha).apply {
            duration = 2000
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = INFINITE
            repeatMode = RESTART
            start()
        }

    }

    fun happyPin(view: View){
        ObjectAnimator.ofFloat(
            view, "translationY",
            0f, -30f, 0f,
        ).apply {
            duration = 2000
            interpolator = BounceInterpolator()
            repeatCount = INFINITE
            repeatMode = RESTART
            start()
        }
        ObjectAnimator.ofFloat(
            view, "rotationY",
            0f, 90f, 0f, 90f, 0f, 90f, -15f, 15f,-5f, 0f,
        ).apply {
            duration = 2000
            interpolator = DecelerateInterpolator()
            repeatCount = INFINITE
            repeatMode = RESTART
            start()
        }

    }

    fun enableAnimation() {
        binding.wsMarkerSmallCircle.visibility = View.VISIBLE
        binding.wsMarkerCircle.visibility = View.VISIBLE
        startAllAnimations()
    }


    private fun startAllAnimations() {
        pulse(binding.wsMarkerSmallCircle)
        happyPin(binding.wsMarkerPin)
    }


    companion object{
        const val TAG = "wowWSMarkerView"
    }
}
