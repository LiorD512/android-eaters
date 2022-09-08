package com.bupp.wood_spoon_eaters.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieDrawable
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.WoodspoonProgressBarBinding


class WSProgressBar @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var pendingShow = false
    private var pendingHide = false

    private var showAnimationSet: AnimatorSet? = null
    private var hideAnimationSet: AnimatorSet? = null
    private var isBlue: Boolean = false

    private var binding: WoodspoonProgressBarBinding =
        WoodspoonProgressBarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        if (attrs != null) {
            val attrSet = context.obtainStyledAttributes(attrs, R.styleable.WoodSpoonProgressBar)

            if (attrSet.hasValue(R.styleable.WoodSpoonProgressBar_isBlueStyle)) {
                isBlue = attrSet.getBoolean(R.styleable.WoodSpoonProgressBar_isBlueStyle, false)
            }
        }
    }

    fun show() {
        Log.d(TAG, "pb - show called")
//        if (!binding.progressBarLayoutLottie.isAnimating) {
        pendingShow = false
        Log.d(TAG, "pb - show action")
//            binding.progressBarLayoutLottie.visibility = View.VISIBLE

        val imageFadeIn = ObjectAnimator.ofFloat(binding.progressBarLayout, "alpha", 0f, 1f)
        imageFadeIn.duration = DURATION
        isVisible = true
        showAnimationSet = AnimatorSet()
        showAnimationSet?.play(imageFadeIn)
        showAnimationSet?.start()


//            showAnimationSet?.addListener(object : Animator.AnimatorListener {
//                override fun onAnimationStart(animation: Animator?) {
//
//                }
//
//                override fun onAnimationEnd(animation: Animator?) {
//                    if (pendingHide) {
//                        Log.d(TAG, "pb - show - start pendingHide")
//                        hide()
//                    }
//                }
//
//                override fun onAnimationCancel(animation: Animator?) {
//
//                }
//
//                override fun onAnimationRepeat(animation: Animator?) {
//
//                }
//
//            })

        binding.progressBarLayout.isClickable = true
        binding.progressBarLayoutLottie.setAnimation("dish_loader.json")
        binding.progressBarLayoutLottie.repeatCount = LottieDrawable.INFINITE
        binding.progressBarLayoutLottie.playAnimation()
        binding.progressBarLayoutLottie.enableMergePathsForKitKatAndAbove(true)

//        }
//        else {
//            Log.d(TAG, "pb - show called but canceled")
//            pendingShow = true
//        }
    }

    fun hide() {
        Log.d(TAG, "pb - hide called")
//        if (binding.progressBarLayout.alpha != 0f) {
        pendingHide = false
        Log.d(TAG, "pb - hide action")
//        binding.progressBarLayoutLottie.visibility = View.GONE
        isVisible = false
        binding.progressBarLayoutLottie.cancelAnimation()

        val imageFadeOut = ObjectAnimator.ofFloat(binding.progressBarLayout, "alpha", 1f, 0f)
        imageFadeOut.duration = DURATION

        hideAnimationSet = AnimatorSet()
        hideAnimationSet?.play(imageFadeOut)
        hideAnimationSet?.start()

//        hideAnimationSet?.addListener(object : Animator.AnimatorListener {
//            override fun onAnimationStart(animation: Animator?) {
//
//            }
//
//            override fun onAnimationEnd(animation: Animator?) {
//                if (pendingShow) {
//                    Log.d(TAG, "pb - hide - start pendingShow")
//                    show()
//                }
//            }
//
//            override fun onAnimationCancel(animation: Animator?) {
//
//            }
//
//            override fun onAnimationRepeat(animation: Animator?) {
//
//            }
//
//        })

        binding.progressBarLayout.isClickable = false

//        } else {
//            Log.d(TAG, "pb - hide called but canceled")
//            pendingHide = true
//
//        }
    }

    fun setProgress(progress: Boolean) {
        if (progress) {
            show()
        } else {
            hide()
        }
    }

    override fun onDetachedFromWindow() {
        showAnimationSet = null
        hideAnimationSet = null
        super.onDetachedFromWindow()
    }

    companion object {
        const val DURATION: Long = 500
        const val TAG = "wowWSPb"
    }

}