package com.bupp.wood_spoon_eaters.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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

    private var showAnimationSet: AnimatorSet? = null
    private var hideAnimationSet: AnimatorSet? = null
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
        if(!binding.progressBarLayoutLottie.isAnimating && binding.progressBarLayout.alpha == 0f){
//            binding.progressBarLayoutLottie.visibility = View.VISIBLE

            val imageFadeIn = ObjectAnimator.ofFloat(binding.progressBarLayout, "alpha", 0f, 1f)
            imageFadeIn.duration = ChangingPictureView.DURATION

            showAnimationSet = AnimatorSet()
            showAnimationSet?.play(imageFadeIn)
            showAnimationSet?.start()

            binding.progressBarLayout.isClickable = true
            binding.progressBarLayoutLottie.playAnimation()
        }
    }

    fun hide() {
        if(binding.progressBarLayout.alpha != 0f) {
//        binding.progressBarLayoutLottie.visibility = View.GONE
            binding.progressBarLayoutLottie.cancelAnimation()

            val imageFadeOut = ObjectAnimator.ofFloat(binding.progressBarLayout, "alpha", 1f, 0f)
            imageFadeOut.duration = ChangingPictureView.DURATION

            hideAnimationSet = AnimatorSet()
            hideAnimationSet?.play(imageFadeOut)
            hideAnimationSet?.start()

            binding.progressBarLayout.isClickable = false
        }
    }

    override fun onDetachedFromWindow() {
        showAnimationSet = null
        hideAnimationSet = null
        super.onDetachedFromWindow()
    }

}