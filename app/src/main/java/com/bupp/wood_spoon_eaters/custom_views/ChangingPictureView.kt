package com.bupp.wood_spoon_eaters.custom_views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.changing_picture_view.view.*


class ChangingPictureView : FrameLayout {

    private var runnableCode: Runnable
    private val mHandler: Handler = Handler()
    private var lastShowingView = 0
    private val DELAY: Long = 4000
    private val DURATION: Long = 500
    private val MAX_COUNT: Int = 6
    var imgCount: Int = 0

    var imgArray: ArrayList<Int> = arrayListOf()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.changing_picture_view, this, true)

        imgArray.add(R.drawable.changing_pic_1)
        imgArray.add(R.drawable.changing_pic_2)
        imgArray.add(R.drawable.changing_pic_3)
        imgArray.add(R.drawable.changing_pic_4)
        imgArray.add(R.drawable.changing_pic_5)
        imgArray.add(R.drawable.changing_pic_6)

        runnableCode = object : Runnable {
            override fun run() {
                animate(getCurrentImg(), getCurrentImageView(), getSecondImageView(lastShowingView))
                mHandler.postDelayed(this, DELAY)
            }
        }
        runnableCode.run()
    }

    private fun getCurrentImageView(): ImageView? {
        if (lastShowingView == 0) {
            lastShowingView = 1
            return imageView1
        } else {
            lastShowingView = 0
            return imageView0
        }
    }

    private fun getSecondImageView(lastShowingView: Int): ImageView? {
        if (lastShowingView == 0) {
            return imageView1
        } else {
            return imageView0
        }
    }

    private fun getCurrentImg(): Int {
        if (imgCount + 1 >= MAX_COUNT) {
            imgCount = 0
        } else {
            imgCount++
        }
        return imgArray.get(imgCount)
    }

    private fun animate(imgId: Int, curImageView: ImageView?, lastImageView: ImageView?) {
        Log.d(
            "wowChangingImages",
            "imgId: " + imgId + ", cur: " + curImageView!!.id + ", second: " + lastImageView!!.id
        )
        curImageView.setImageResource(imgId)

        val fadeOut = ObjectAnimator.ofFloat(lastImageView, "alpha", 1f, 0f)
        fadeOut.duration = DURATION

        val fadeIn = ObjectAnimator.ofFloat(curImageView, "alpha", 0f, 1f)
        fadeIn.duration = DELAY
        fadeIn.duration = DURATION

        val mAnimationSet = AnimatorSet()
        mAnimationSet.play(fadeIn).with(fadeOut)
        mAnimationSet.start()
    }

    fun stopAnimation() {
//        Log.d("wowChangingImages", "stopAnimation")
        mHandler.removeCallbacks(runnableCode)
    }

}