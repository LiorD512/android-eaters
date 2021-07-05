package com.bupp.wood_spoon_eaters.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class SlideAnimation(val view: View, private val fromHeight: Int, private val toHeight: Int): Animation() {

    override fun applyTransformation(interpolatedTime: Float, transformation: Transformation?) {
        val newHeight: Int
        if (view.height != toHeight) {
            newHeight = (fromHeight + (toHeight - fromHeight) * interpolatedTime).toInt()
            view.layoutParams?.height = newHeight
            view.requestLayout()
        }
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
    }

    override fun willChangeBounds(): Boolean {
        return true
    }

}