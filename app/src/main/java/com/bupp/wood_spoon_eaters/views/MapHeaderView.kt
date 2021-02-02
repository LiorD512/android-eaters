package com.bupp.wood_spoon_eaters.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.databinding.MapHeaderViewLayoutBinding
import render.animations.Flip
import render.animations.Render
import render.animations.Slide
import java.util.logging.Handler

class MapHeaderView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var isMarkerGood: Boolean = true
    private var binding: MapHeaderViewLayoutBinding = MapHeaderViewLayoutBinding.inflate(LayoutInflater.from(context), this, true)

init {
    binding.mapHeaderView2.alpha = 0f
}

    fun updateMapHeaderView(isMarkerGood: Boolean) {
//        Log.d(TAG, "updateMapHeaderView: $isMarkerGood")
        if(this.isMarkerGood != isMarkerGood){
            Log.d(TAG, "changing to: $isMarkerGood")
            this.isMarkerGood = isMarkerGood
            if (isMarkerGood) {
                animate2OutAnd1In()
            } else {
                animate1OutAnd2In()
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
            val render = Render(context)
            render.setAnimation(Slide().InDown(binding.mapHeaderView2))
            render.setDuration(350)
            render.start()
        }, 250)
    }

    private fun animate2OutAnd1In() {
        Log.d(TAG, "animate2OutAnd1In")
        val render = Render(context)
        render.setAnimation(Slide().OutUp(binding.mapHeaderView2))
        render.setDuration(250)
        render.start()

        postDelayed({
            val render = Render(context)
            render.setAnimation(Slide().InDown(binding.mapHeaderView1))
            render.setDuration(350)
            render.start()
        }, 250)

//        val outX = Flip().OutX(binding.mapHeaderView2)
//        val inX = Flip().InX(binding.mapHeaderView1)
//
//        val animatorSet = AnimatorSet()
//        animatorSet.play(outX).before(inX)
//
//        animatorSet.start()

    }

    companion object{
        const val TAG = "wowMapHeaderView"
    }
}
