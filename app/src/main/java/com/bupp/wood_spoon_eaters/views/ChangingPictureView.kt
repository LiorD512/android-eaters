package com.bupp.wood_spoon_eaters.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.databinding.ChangingPictureViewBinding
import com.bupp.wood_spoon_eaters.model.WelcomeScreen
import kotlinx.android.synthetic.main.changing_picture_view.view.*


class ChangingPictureView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {


    private var binding: ChangingPictureViewBinding = ChangingPictureViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var runnableCode: Runnable? = null
    private val mHandler: Handler = Handler()
    private var lastShowingView = 0
    private var lastShowingTextView = 0

    var imgCount: Int = 0
    var textCount: Int = 0

    var welcomeScreen: List<WelcomeScreen> = listOf()

    fun init(welcomeScreen: List<WelcomeScreen>){
        this.welcomeScreen = welcomeScreen

        runnableCode = object : Runnable {
            override fun run() {
                animateImages(getCurrentImg(), getCurrentImageView(), getSecondImageView(lastShowingView))
                animateText(getCurrentText(), getCurrentTextView(), getSecondTextView(lastShowingTextView))
                mHandler.postDelayed(this, DELAY)
            }
        }
        runnableCode?.run()

    }

    private fun getCurrentImageView(): ImageView? {
        return if (lastShowingView == 0) {
            lastShowingView = 1
            binding.changingImages1
        } else {
            lastShowingView = 0
            binding.changingImages0
        }
    }

    private fun getSecondImageView(lastShowingView: Int): ImageView? {
        return if (lastShowingView == 0) {
            binding.changingImages1
        } else {
            binding.changingImages0
        }
    }

    private fun getCurrentTextView(): TextView? {
        return if (lastShowingTextView == 0) {
            lastShowingTextView = 1
            binding.changingImagesText1
        } else {
            lastShowingTextView = 0
            binding.changingImagesText0
        }
    }

    private fun getSecondTextView(lastShowingView: Int): TextView? {
        return if (lastShowingTextView == 0) {
            binding.changingImagesText1
        } else {
            binding.changingImagesText0
        }
    }

    private fun getCurrentImg(): String? {
        if (imgCount + 1 >= MAX_COUNT) {
            imgCount = 0
        } else {
            imgCount++
        }
        return welcomeScreen.get(imgCount).url
    }

    private fun getCurrentText(): String? {
        if (textCount + 1 >= MAX_COUNT) {
            textCount = 0
        } else {
            textCount++
        }
        return welcomeScreen.get(textCount).text
    }

    private fun animateImages(imageUrl: String?, curImageView: ImageView?, lastImageView: ImageView?) {
        Log.d(
            "wowChangingImages",
            "imageUrl: " + imageUrl + ", cur: " + curImageView!!.id + ", second: " + lastImageView!!.id
        )
        Glide.with(context).load(imageUrl).into(curImageView)
//        curImageView.setImageResource(imgId)

        val imageFadeOut = ObjectAnimator.ofFloat(lastImageView, "alpha", 1f, 0f)
        imageFadeOut.duration = DURATION

        val imageFadeIn = ObjectAnimator.ofFloat(curImageView, "alpha", 0f, 1f)
//        fadeIn.duration = DELAY
        imageFadeIn.duration = DURATION

        val imageAnimationSet = AnimatorSet()
        imageAnimationSet.play(imageFadeIn).with(imageFadeOut)
        imageAnimationSet.start()

    }

    private fun animateText(text: String?, curTextView: TextView?, lastTextView: TextView?) {
        curTextView?.text = text

        val textFadeOut = ObjectAnimator.ofFloat(lastTextView, "alpha", 1f, 0f)
        textFadeOut.duration = DURATION

        val textFadeIn = ObjectAnimator.ofFloat(curTextView, "alpha", 0f, 1f)
        textFadeIn.duration = DURATION

        val textAnimationSet = AnimatorSet()
        textAnimationSet.play(textFadeIn).with(textFadeOut)
        textAnimationSet.start()

    }

    fun stopAnimation() {
//        Log.d("wowChangingImages", "stopAnimation")
        mHandler.removeCallbacks(runnableCode)
        runnableCode = null
    }

    companion object{
        const val DELAY: Long = 2000
        const val DURATION: Long = 500
        const val MAX_COUNT: Int = 3
    }
}