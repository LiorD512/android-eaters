package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.DishCounterViewBinding

class DishCounterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: DishCounterViewBinding = DishCounterViewBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    init {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        if (attrs != null) {
            val attrSet =
                context.obtainStyledAttributes(attrs, R.styleable.DishCounterView)

            if (attrSet.hasValue(R.styleable.DishCounterView_count)) {
                val counter = attrSet.getString(R.styleable.DishCounterView_count)
                setCounter(counter)
            }
            attrSet.recycle()
        }
    }

    fun setCounter(counter: String?) {
        counter?.toIntOrNull()?.let {
            binding.tvCounter.text = if (it in 0..99) {
                it.toString()
            } else {
                "99+"
            }
        }
    }

}
