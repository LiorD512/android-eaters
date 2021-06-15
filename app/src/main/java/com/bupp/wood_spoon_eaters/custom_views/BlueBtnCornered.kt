package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
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

            val isRed = a.getBoolean(R.styleable.BlueBtnAttrs_makeItRed, false)
            if(isRed){
                binding.blueBtnBackground.setBackgroundResource(R.drawable.rectangle_red_btn_cornered)
                binding.blueBtnText.setTextColor(ContextCompat.getColor(context, R.color.coral))
            }

            val isOrange = a.getBoolean(R.styleable.BlueBtnAttrs_makeItOrange, false)
            if(isOrange){
                binding.blueBtnBackground.setBackgroundResource(R.drawable.rectangle_orange_btn_cornered)
                binding.blueBtnText.setTextColor(ContextCompat.getColor(context, R.color.white))
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

    fun setBtnText(btnText: String?){
        btnText?.let{
            binding.blueBtnText.text = btnText
        }
    }
}