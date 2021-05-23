//package com.bupp.wood_spoon_eaters.views.ws_marker
//
//import android.R.attr.bitmap
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.graphics.RectF
//import android.util.AttributeSet
//import android.view.View
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.utils.Utils
//
//
//class WsCircle(context: Context, attrs: AttributeSet) : View(context, attrs) {
//
//    companion object{
//        private const val START_ANGLE_POINT = 0
//    }
//
//    private val circleWidth = resources.getDimension(R.dimen.ws_marker_circle_width).toInt()
//    private val circleHeight = resources.getDimension(R.dimen.ws_marker_circle_height).toInt()
//
//    private var paint: Paint? = null
//    private var circle: RectF? = null
//
//    private var angle = 360f
//
//    init {
//        paint = Paint().apply {
//            isAntiAlias = true
//            style = Paint.Style.FILL
////            setStrokeWidth(strokeWidth.toFloat())
//            //Circle color
//            color = Color.RED
//        }
//    }
//
//    fun init(){
//        circle = RectF(
//            (width / 2 - circleWidth/2).toFloat(),
//            (height / 2 - circleHeight/2).toFloat(),
//            (width / 2 + circleWidth/2).toFloat(),
//            (height / 2 + circleHeight/2).toFloat()
//        )
//    }
//
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        init()
//        canvas.drawArc(circle!!, START_ANGLE_POINT.toFloat(), angle, false, paint!!)
//    }
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val width = MeasureSpec.makeMeasureSpec(550, MeasureSpec.UNSPECIFIED)
//        val height = MeasureSpec.makeMeasureSpec(550, MeasureSpec.UNSPECIFIED)
//        setMeasuredDimension(width, height)
//    }
//
//    fun getAngle(): Float {
//        return angle
//    }
//
//    fun setAngle(angle: Float) {
//        this.angle = angle
//    }
//
//}