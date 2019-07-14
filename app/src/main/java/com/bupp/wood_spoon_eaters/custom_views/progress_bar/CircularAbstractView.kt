package com.bupp.wood_spoon_eaters.custom_views.progress_bar

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet


open class CircularAbstractView : DotsLoaderBaseView {

    protected val noOfDots = 8
    private val SIN_45 = 0.7071f

    lateinit var dotsYCorArr: FloatArray

    var bigCircleRadius: Int = 60

    var useMultipleColors: Boolean = false
    var dotsColorsArray = IntArray(8) { resources.getColor(android.R.color.darker_gray) }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun initCoordinates() {
        val sin45Radius = SIN_45 * bigCircleRadius

        dotsXCorArr = FloatArray(noOfDots)
        dotsYCorArr = FloatArray(noOfDots)

        for (i in 0 until noOfDots) {
            dotsYCorArr[i] = (this.bigCircleRadius + radius).toFloat()
            dotsXCorArr[i] = dotsYCorArr[i]
        }

        dotsXCorArr[1] = dotsXCorArr[1] + sin45Radius - 5
        dotsXCorArr[2] = dotsXCorArr[2] + bigCircleRadius - 5
        dotsXCorArr[3] = dotsXCorArr[3] + sin45Radius - 5

        dotsXCorArr[5] = dotsXCorArr[5] - sin45Radius + 5
        dotsXCorArr[6] = dotsXCorArr[6] - bigCircleRadius + 10
        dotsXCorArr[7] = dotsXCorArr[7] - sin45Radius + 5

        dotsYCorArr[0] = dotsYCorArr[0] - bigCircleRadius + 10
        dotsYCorArr[1] = dotsYCorArr[1] - sin45Radius + 5
        dotsYCorArr[3] = dotsYCorArr[3] + sin45Radius - 5

        dotsYCorArr[4] = dotsYCorArr[4] + this.bigCircleRadius - 5
        dotsYCorArr[5] = dotsYCorArr[5] + sin45Radius - 5
        dotsYCorArr[7] = dotsYCorArr[7] - sin45Radius + 5
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidthHeight = 2 * bigCircleRadius + 2 * radius
        setMeasuredDimension(calWidthHeight + 5, calWidthHeight + 5)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until noOfDots) {

            if (useMultipleColors) {
                defaultCirclePaint?.color = if (dotsColorsArray.size > i) dotsColorsArray[i] else defaultColor
            }

            defaultCirclePaint?.let {
                canvas.drawCircle(dotsXCorArr[i], dotsYCorArr[i], radius.toFloat(), it)
            }

        }
    }
}