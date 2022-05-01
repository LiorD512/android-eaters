package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.OrangeStrokeBtnBinding

class OrangeStrokeBtn : FrameLayout{

    private var binding: OrangeStrokeBtnBinding = OrangeStrokeBtnBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.orange_stroke_btn, this, true)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.OrangeBtnAttrs)
            if (a.hasValue(R.styleable.OrangeBtnAttrs_title)) {
                val title = a.getString(R.styleable.OrangeBtnAttrs_title)
                binding.orangeBtnText.text = title
            }
            if (a.hasValue(R.styleable.OrangeBtnAttrs_textColor)) {
                val textColor = a.getColor(R.styleable.OrangeBtnAttrs_textColor, ContextCompat.getColor(context, R.color.black))
                binding.orangeBtnText.setTextColor(textColor)
            }
            a.recycle()
        }
    }

}