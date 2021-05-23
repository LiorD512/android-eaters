package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.BlueBtnCorneredBinding


class BlueBtnCornered @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr){

    private var binding: BlueBtnCorneredBinding = BlueBtnCorneredBinding.inflate(LayoutInflater.from(context), this, true)

    init{
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.BlueBtnAttrs)
            if (a.hasValue(R.styleable.BlueBtnAttrs_title)) {
                var title = a.getString(R.styleable.BlueBtnAttrs_title)
                binding.blueBtnText.text = title
            }
            a.recycle()
        }
    }

    fun setBtnEnabled(isEnabled: Boolean) {
        with(binding){
            blueBtnBackground.isEnabled = isEnabled;
            blueBtnText.isEnabled = isEnabled;
            if (isEnabled) {
                blueBtnBackground.alpha = 1f
            } else {
                blueBtnBackground.alpha = 0.5f
            }
        }

    }
}