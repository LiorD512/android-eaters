package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.WoodspoonProgressBarBinding
import com.bupp.wood_spoon_eaters.databinding.WsEditTextBinding
import kotlinx.android.synthetic.main.lottie_animation_view.view.*
import kotlinx.android.synthetic.main.woodspoon_progress_bar.view.*


class WSProgressBar @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var isBlue: Boolean = false

    private var binding: WoodspoonProgressBarBinding = WoodspoonProgressBarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        if (attrs != null) {
            val attrSet = context.obtainStyledAttributes(attrs, R.styleable.WoodSpoonProgressBar)

            if (attrSet.hasValue(R.styleable.WoodSpoonProgressBar_isBlueStyle)) {
                isBlue = attrSet.getBoolean(R.styleable.WoodSpoonProgressBar_isBlueStyle, false)
            }
        }
    }

    fun show() {
        binding.progressBarLayout.visibility = View.VISIBLE
        binding.progressBarLayoutLottie.setAnimation("loader.json")
        binding.progressBarLayoutLottie.playAnimation()
    }

    fun hide() {
        binding.progressBarLayout.visibility = View.GONE
        binding.progressBarLayoutLottie.cancelAnimation()
    }

//    private fun setProgressBarStyle() {
//        if (isBlue) {
//            progressBar.defaultColor = ContextCompat.getColor(context, R.color.white)
//            progressBar.selectedColor = ContextCompat.getColor(context, R.color.white)
//            progressBar.firstShadowColor = ContextCompat.getColor(context, R.color.white)
//            progressBar.secondShadowColor = ContextCompat.getColor(context, R.color.white)
//            progressBarLayout.background = ContextCompat.getDrawable(context, R.color.teal_blue_80)
//        } else {
//            progressBar.defaultColor = ContextCompat.getColor(context, R.color.teal_blue)
//            progressBar.selectedColor = ContextCompat.getColor(context, R.color.teal_blue)
//            progressBar.firstShadowColor = ContextCompat.getColor(context, R.color.teal_blue)
//            progressBar.secondShadowColor = ContextCompat.getColor(context, R.color.teal_blue)
//            progressBarLayout.background = ContextCompat.getDrawable(context, R.color.white_80)
//        }
//    }
}