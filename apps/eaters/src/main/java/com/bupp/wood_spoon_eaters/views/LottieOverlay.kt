package com.bupp.wood_spoon_eaters.views

import android.animation.Animator
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.view.doOnAttach
import com.bupp.wood_spoon_eaters.databinding.LottieOverlayViewBinding

class LottieOverlay constructor(context: Context): Animator.AnimatorListener {

    private lateinit var popupWindow: PopupWindow
    private lateinit var binding: LottieOverlayViewBinding

    init {
        initUi(context)
    }

    private fun initUi(context: Context){
        binding = LottieOverlayViewBinding.inflate(LayoutInflater.from(context), null, false)
        binding.lottieOverlayView.addAnimatorListener(this)
        popupWindow = PopupWindow(binding.root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun showLottieOverlay(view: View, lottieFileName: String) {
        view.doOnAttach {
            binding.apply {
                binding.lottieOverlayView.setAnimation(lottieFileName)
                popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, view.height)
            }
        }
    }

    override fun onAnimationStart(animation: Animator?) {}

    override fun onAnimationEnd(animation: Animator?) {
        popupWindow.dismiss()
    }

    override fun onAnimationCancel(animation: Animator?) {}

    override fun onAnimationRepeat(animation: Animator?) {}

}