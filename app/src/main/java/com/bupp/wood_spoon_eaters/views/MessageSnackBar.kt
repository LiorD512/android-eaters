package com.bupp.wood_spoon_eaters.views

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.MessageSnackBarBinding
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.utils.Utils
import timber.log.Timber


class MessageSnackBar @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: MessageSnackBarBinding

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.message_snack_bar, this, true)
        binding = MessageSnackBarBinding.bind(view)
    }

    fun showTopNotification(message: String) {
        with(binding) {

            snackBarMessage.text = message
        }
        startSnackBarAnimation()
    }

    private fun startSnackBarAnimation() {
        ObjectAnimator.ofFloat(
            binding.snackBarView, "translationY", 0f
        ).apply {
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(
            binding.snackBarView, "translationY", Utils.toDp(-100).toFloat()
        ).apply {
            duration = 500
            startDelay = 3000
            start()
        }
    }


}
