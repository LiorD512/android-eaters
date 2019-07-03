package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.favorite_btn.view.*
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation


class FavoriteBtn : FrameLayout {

    var isFavSelected = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.favorite_btn, this, true)

        initUi()
    }

    private fun initUi() {
        favBtn.setOnClickListener {
            animateView()
            onClick()
        }
    }

    private fun animateView() {
        val anim = ScaleAnimation(
            1f, 1.2f, // Start and end values for the X axis scaling
            1f, 1.2f, // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 0.5f
        ) // Pivot point of Y scaling
//        anim.fillAfter =  // Needed to keep the result of the animation
        anim.duration = 150
        anim.repeatMode = ScaleAnimation.REVERSE
        anim.interpolator = OvershootInterpolator()
        favBtn.startAnimation(anim)
    }

    private fun onClick() {
        isFavSelected = !isFavSelected
        favBtn.isSelected = isFavSelected
    }


}