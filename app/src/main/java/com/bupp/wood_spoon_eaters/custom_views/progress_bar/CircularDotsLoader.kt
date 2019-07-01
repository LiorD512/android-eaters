package com.bupp.wood_spoon_eaters.custom_views.progress_bar

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.bupp.wood_spoon_eaters.R
import java.util.*


class CircularDotsLoader : CircularAbstractView {

    private var timer: Timer? = null

    constructor(context: Context) : super(context) {
        initCoordinates()
        initPaints()
        initShadowPaints()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
        initCoordinates()
        initPaints()
        initShadowPaints()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttributes(attrs)
        initCoordinates()
        initPaints()
        initShadowPaints()
    }

    override fun initAttributes(attrs: AttributeSet) {
        super.initAttributes(attrs)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularDotsLoader, 0, 0)

        this.bigCircleRadius =
            typedArray.getDimensionPixelSize(R.styleable.CircularDotsLoader_loader_bigCircleRadius, 60)

        typedArray.recycle()
    }


    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        if (visibility != VISIBLE) {
            timer?.cancel()
        } else if (shouldAnimate) {
            scheduleTimer()
        }
    }

    private fun scheduleTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                selectedDotPos++

                if (selectedDotPos > noOfDots) {
                    selectedDotPos = 1
                }

                getActivity(context).runOnUiThread { invalidate() }
            }
        }, 0, animDur.toLong())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircle(canvas)
    }

    private fun getActivity(context: Context): Activity {
        return when (context) {
            is Activity -> context
            is ContextWrapper -> getActivity(context.baseContext)
            else -> context as Activity
        }
    }

    private fun drawCircle(canvas: Canvas) {
        val firstShadowPos = if (selectedDotPos == 1) 8 else selectedDotPos - 1
        val secondShadowPos = if (firstShadowPos == 1) 8 else firstShadowPos - 1

        for (i in 0 until noOfDots) {

            if (i + 1 == selectedDotPos) {
                canvas.drawCircle(dotsXCorArr[i], dotsYCorArr[i], radius.toFloat(), selectedCirclePaint)
            } else if (this.showRunningShadow && i + 1 == firstShadowPos) {
                canvas.drawCircle(dotsXCorArr[i], dotsYCorArr[i], radius.toFloat(), firstShadowPaint)
            } else if (this.showRunningShadow && i + 1 == secondShadowPos) {
                canvas.drawCircle(dotsXCorArr[i], dotsYCorArr[i], radius.toFloat(), secondShadowPaint)
            } else {
                canvas.drawCircle(dotsXCorArr[i], dotsYCorArr[i], radius.toFloat(), defaultCirclePaint)
            }

        }
    }

    fun stop(){
        timer?.cancel()
        timer = null
    }
}