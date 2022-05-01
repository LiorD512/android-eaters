package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.OrangeBtnBinding

class OrangeBtn @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

    private var binding: OrangeBtnBinding = OrangeBtnBinding.inflate(LayoutInflater.from(context), this, true)

    init{
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.OrangeBtnAttrs)
            if (a.hasValue(R.styleable.OrangeBtnAttrs_title)) {
                val title = a.getString(R.styleable.OrangeBtnAttrs_title)
                binding.orangeBtnText.text = title
            }
            a.recycle()
        }
    }


    fun setBtnEnabled(isEnabled : Boolean){
        this.isEnabled = isEnabled
        with(binding) {
            Log.d("wowOrange", "isEnabled: $isEnabled")
            if (isEnabled) {
                orangeBtnBackground.alpha = 1f
            } else {
                orangeBtnBackground.alpha = 0.5f
            }
        }
    }

    fun setTitle(title: String) {
        binding.orangeBtnText.text = title
    }
}