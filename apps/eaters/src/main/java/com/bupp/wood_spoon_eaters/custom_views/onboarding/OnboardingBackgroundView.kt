package com.bupp.wood_spoon_eaters.custom_views.onboarding

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.coroutineScope
import com.bupp.wood_spoon_eaters.databinding.ViewBackgroundOnboardingBinding
import kotlinx.coroutines.*

data class SliderContentPage(
    val bgImageResources: Int,
    val titleResource: Int,
    val descriptionResource: Int
)

class OnboardingBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LifecycleObserver {

    private var binding = ViewBackgroundOnboardingBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    private lateinit var scope: CoroutineScope
    private val job = Job()
    private var slideList: List<SliderContentPage> = emptyList()
    private var flipSlideInterval = 6000L
    private var parallaxShiftInterval = 80L
    private var showedSlideIndex = 0

    fun showStaticBackground(sliderContentPage: SliderContentPage) {
        binding.ivFlipper.setBackgroundResource(sliderContentPage.bgImageResources)
    }

    fun showSliderAnimation(isImageSliding: Boolean, isTextSliding: Boolean) {
        scope.launch(job) {
            repeat(Int.MAX_VALUE) {
                if (isImageSliding) {
                    prepareImageFlipper()
                    binding.ivFlipper.showNext()
                }

                delay(parallaxShiftInterval)

                if (isTextSliding) {
                    prepareTextFlipper()
                    binding.textBlockFlipper.showNext()
                }

                delay(flipSlideInterval)
            }
        }
    }

    fun showVideoBackground(path: Uri) {
        binding.let {
            binding.ivFlipper.isVisible = false

            it.videoBackground.setOnPreparedListener { mediaPlayer ->
                val videoRatio = mediaPlayer.videoWidth / mediaPlayer.videoHeight.toFloat()
                val screenRatio = it.videoBackground.width / it.videoBackground.height.toFloat()
                val scaleX = videoRatio / screenRatio
                if (scaleX >= 1f) {
                    it.videoBackground.scaleX = scaleX
                } else {
                    it.videoBackground.scaleY = 1f / scaleX
                }
                mediaPlayer.isLooping = true
            }
            it.videoBackground.setVideoURI(path)
            it.videoBackground.start()
        }
    }

    private fun prepareTextFlipper() = binding.textBlockFlipper.apply {
        val slideNumber = calculateSlideIndex(slideList.size)
        binding.tvTitle.text = this.resources.getString(slideList[slideNumber].titleResource)
        binding.tvDescription.text = this.resources.getString(slideList[slideNumber].descriptionResource)

        inAnimation = AnimationUtils.loadAnimation(this.context, android.R.anim.fade_in)
        outAnimation = AnimationUtils.loadAnimation(this.context, android.R.anim.fade_out)
        animate().interpolator = AnticipateInterpolator()
    }

    private fun prepareImageFlipper() = binding.ivFlipper.apply {
        background = null
        slideList.forEach {
            val imageView = ImageView(this.context).apply {
                setImageResource(it.bgImageResources)
                scaleType = ImageView.ScaleType.FIT_XY
            }
            addView(imageView)
        }

        inAnimation = AnimationUtils.loadAnimation(this.context, android.R.anim.fade_in)
        outAnimation = AnimationUtils.loadAnimation(this.context, android.R.anim.fade_out)
        animate().interpolator = AnticipateInterpolator()
    }

    private fun calculateSlideIndex(slidesAmount: Int): Int {
        if (showedSlideIndex == slidesAmount) {
            showedSlideIndex = 0
        }

        return showedSlideIndex++
    }

    fun registerLifecycleOwner(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
        scope = lifecycle.coroutineScope
    }

    fun setupSlideList(list: List<SliderContentPage>) {
        slideList = list
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job.cancel()
    }
}