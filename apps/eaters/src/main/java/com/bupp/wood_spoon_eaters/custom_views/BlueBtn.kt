package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.BlueBtnBinding


class BlueBtn @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: BlueBtnBinding = BlueBtnBinding.inflate(LayoutInflater.from(context), this, true)

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
        Log.d("wowBlue", "isEnabled: $isEnabled")
        this.isEnabled = isEnabled
        if (isEnabled) {
            binding.blueBtnBackground.alpha = 1f
        } else {
            binding. blueBtnBackground.alpha = 0.5f
        }
    }

    fun setTitle(title: String) {
        binding.blueBtnText.text = title
    }
}