package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.blue_btn.view.*


class BlueBtn : FrameLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.blue_btn, this, true)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.BlueBtnAttrs)
            if (a.hasValue(R.styleable.BlueBtnAttrs_title)) {
                var title = a.getString(R.styleable.BlueBtnAttrs_title)
                blueBtnText.text = title
            }


            a.recycle()
        }
    }

    fun setBtnEnabled(isEnabled: Boolean) {
        Log.d("wowBlue", "isEnabled: $isEnabled")
        this.isEnabled = isEnabled
        if (isEnabled) {
            blueBtnBackground.alpha = 1f
        } else {
            blueBtnBackground.alpha = 0.5f
        }
    }

    fun setTitle(title: String) {
        blueBtnText.text = title
    }
}