//package com.bupp.wood_spoon_eaters.custom_views
//
//import android.content.Context
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.widget.FrameLayout
//import androidx.core.content.ContextCompat
//import com.bupp.wood_spoon_eaters.R
//
//
//class BlueStrokeBtn : FrameLayout {
//
//    constructor(context: Context) : this(context, null)
//
//    constructor(context: Context, attrs: AttributeSet?) :
//            this(context, attrs, 0)
//
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        LayoutInflater.from(context).inflate(R.layout.blue_stroke_btn, this, true)
//
//        if (attrs != null) {
//            val a = context.obtainStyledAttributes(attrs, R.styleable.BlueBtnAttrs)
//            if (a.hasValue(R.styleable.BlueBtnAttrs_title)) {
//                var title = a.getString(R.styleable.BlueBtnAttrs_title)
//                blueBtnText.text = title
//            }
//            if (a.hasValue(R.styleable.BlueBtnAttrs_textColor)) {
//                var textColor =
//                    a.getColor(R.styleable.BlueBtnAttrs_textColor, ContextCompat.getColor(context, R.color.black))
//                blueBtnText.setTextColor(textColor)
//            }
//            a.recycle()
//        }
//    }
//
//    public fun setBtnEnabled(isEnabled: Boolean) {
//        blueBtnBackground.isEnabled = isEnabled
//        blueBtnText.isEnabled = isEnabled
//        if (isEnabled) {
//            blueBtnBackground.alpha = 1f
//        } else {
//            blueBtnBackground.alpha = 0.5f
//        }
//
//    }
//}