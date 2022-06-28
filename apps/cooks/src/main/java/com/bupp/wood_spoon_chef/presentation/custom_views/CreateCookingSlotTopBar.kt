package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.CreateCookingSlotTopBarBinding

class CreateCookingSlotTopBar @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    interface CreateCookingSlotTopBarListener {
        fun onBackClick()
    }

    private var binding: CreateCookingSlotTopBarBinding = CreateCookingSlotTopBarBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    private var listener: CreateCookingSlotTopBarListener? = null

    init {
        initUi()
        initAttrs(attrs)
    }

    private fun initUi() {
        binding.apply {
            createCookingSlotTopBarImg.setOnClickListener { listener?.onBackClick() }
        }
    }

    private fun initAttrs(attrs: AttributeSet?) {
        if (attrs != null) {
            val attrSet = context.obtainStyledAttributes(attrs, R.styleable.CreateCookingSlotTopBar)

            if (attrSet.hasValue(R.styleable.CreateCookingSlotTopBar_title)) {
                val title = attrSet.getString(R.styleable.CreateCookingSlotTopBar_title)
                setTitle(title)
            }

            if (attrSet.hasValue(R.styleable.CreateCookingSlotTopBar_cooking_slot_bar_icon)) {
                val icon = attrSet.getResourceId(
                    R.styleable.CreateCookingSlotTopBar_cooking_slot_bar_icon,
                    0
                )
                setIcon(icon)

            }
            attrSet.recycle()
        }
    }

    fun setCookingSlotTopBarListener(listenerInstance: CreateCookingSlotTopBarListener){
        listener = listenerInstance
    }

    private fun setTitle(title: String?) {
        binding.createCookingSlotTopBarTitle.text = title
    }

    private fun setIcon(icon: Int) {
        binding.createCookingSlotTopBarImg.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                icon
            )
        )
    }
}