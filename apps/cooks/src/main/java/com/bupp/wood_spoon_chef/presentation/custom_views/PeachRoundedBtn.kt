package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.PeachRoundedBtnBinding

class PeachRoundedBtn @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val binding: PeachRoundedBtnBinding =
        PeachRoundedBtnBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        if (attrs != null) {
            val attrSet = context.obtainStyledAttributes(attrs, R.styleable.PeachRoundedBtn)

            if (attrSet.hasValue(R.styleable.PeachRoundedBtn_title)) {
                val title = attrSet.getString(R.styleable.PeachRoundedBtn_title)
                setTitle(title)
            }

            attrSet.recycle()
        }
    }

    fun setTitle(title: String?) {
        binding.peachBtnText.text = title
    }
}