package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.TipLineViewBinding
import android.view.inputmethod.EditorInfo


class TipLineView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: TipLineViewBinding = TipLineViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.TipLineView)
            val tipPercent = a.getInteger(R.styleable.TipLineView_tipPercent, 0)
            initUi(tipPercent)
            a.recycle()
        }
    }

    private fun initUi(tipPercent: Int) {
        when (tipPercent) {
            TIP_PERCENT_12 -> {
                init12PercentUi()
            }
            TIP_PERCENT_15 -> {
                init15PercentUi()
            }
            TIP_PERCENT_18 -> {
                init18PercentUi()
            }
            TIP_PERCENT_20 -> {
                init20PercentUi()
            }
            TIP_PERCENT_CUSTOM -> {
                initCustomUi()
            }
        }
    }

    private fun init12PercentUi() {
        with(binding) {
            tipPercentageIcon.setImageResource(R.drawable.ic_tip_12);
            tipPercentageText.text = "I love you shawarma’ch"
            tipPercentageValue.text = "12%"
        }
    }

    private fun init15PercentUi() {
        with(binding) {
            tipPercentageIcon.setImageResource(R.drawable.ic_tip_15);
            tipPercentageText.text = "You’re the sauce to my pasta"
            tipPercentageValue.text = "15%"
        }
    }

    private fun init18PercentUi() {
        with(binding) {
            tipPercentageIcon.setImageResource(R.drawable.ic_tip_18);
            tipPercentageText.text = "I walnut let you go"
            tipPercentageValue.text = "18%"
        }
    }

    private fun init20PercentUi() {
        with(binding) {
            tipPercentageIcon.setImageResource(R.drawable.ic_tip_20);
            tipPercentageText.text = "You have a pizza my heart"
            tipPercentageValue.text = "20%"
        }
    }

    private fun initCustomUi() {
        with(binding) {
            tipPercentageIcon.setImageResource(R.drawable.ic_tip_custom);
            tipPercentageText.text = "Custom"
            tipPercentageValue.isVisible = false
            tipPercentageEditLayout.isVisible = true
        }
    }

    fun setCustomTipListener(onTipDone: (Int) -> Unit) {
        with(binding) {
            tipPercentageEditValue.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val enteredAmountStr = tipPercentageEditValue.text.toString()
                    var enteredAmount = 0
                    if (enteredAmountStr.isNotEmpty()) {
                        enteredAmountStr.trim().toIntOrNull()?.let{
                            enteredAmount = it
                        }
                    }
                    onTipDone(enteredAmount)
                }
                false
            }
        }
    }

    fun select() {
        with(binding){
            isSelected = true
            tipPercentageValueLayout.isSelected = true
            tipPercentageValue.setTextColor(ContextCompat.getColor(context, R.color.teal_blue_50))
        }
    }

    fun unselect() {
        with(binding){
            isSelected = false
            tipPercentageValueLayout.isSelected = false
            tipPercentageValue.setTextColor(ContextCompat.getColor(context, R.color.dark_50))
        }
    }

    fun setCustomTipValue(tipAmount: Double) {
        with(binding){
            val string = "$$tipAmount"
            tipPercentageEditValue.hint = string
        }
    }

    companion object {
        const val TIP_PERCENT_12 = 0
        const val TIP_PERCENT_15 = 1
        const val TIP_PERCENT_18 = 2
        const val TIP_PERCENT_20 = 3
        const val TIP_PERCENT_CUSTOM = 4
    }

}