package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.SingleDishHeaderViewBinding
import com.bupp.wood_spoon_eaters.databinding.TipPercentViewBinding
import com.bupp.wood_spoon_eaters.utils.Utils


class TipPercentView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var binding: TipPercentViewBinding = TipPercentViewBinding.inflate(LayoutInflater.from(context), this, true)

    interface TipPercentViewListener {
        fun onTipIconClick(tipSelection: Int?)
    }

    private var listener: TipPercentViewListener? = null
    private var selectedTip: Int? = null

    fun setTipPercentViewListener(listener: TipPercentViewListener) {
        this.listener = listener
    }

    init{
        initUi()
    }

    private fun initUi() {
        with(binding){
            tipPercent10.setOnClickListener(this@TipPercentView)
            tipPercent15.setOnClickListener(this@TipPercentView)
            tipPercent20.setOnClickListener(this@TipPercentView)
            tipPercentCustom.setOnClickListener(this@TipPercentView)
        }
    }

    override fun onClick(v: View?) {
        with(binding){
            if(v!!.isSelected){
                v.isSelected = false
                selectedTip = null
            }else {
                clearAll()
                v.isSelected = true
                when (v.id) {
                    tipPercent10.id -> {
                        selectedTip = Constants.TIP_10_PERCENT_SELECTED
                    }
                    tipPercent15.id -> {
                        selectedTip = Constants.TIP_15_PERCENT_SELECTED
                    }
                    tipPercent20.id -> {
                        selectedTip = Constants.TIP_20_PERCENT_SELECTED
                    }
                    tipPercentCustom.id -> {
                        selectedTip = Constants.TIP_CUSTOM_SELECTED
                    }
                }
            }
            listener?.onTipIconClick(selectedTip)
        }
    }

    fun clearAll() {
        with(binding){
            tipPercent10.isSelected = false
            tipPercent15.isSelected = false
            tipPercent20.isSelected = false
            tipPercentCustom.isSelected = false
            selectedTip = null
        }
    }

    fun setCustomTipValue(tipValue: Int? = 0){
        val string = "Custom\n$$tipValue"
        var spannableString = Utils.setCustomFontTypeSpan(context, string, 0, 6, R.font.open_sans_semi_bold)
        binding.tipPercentCustom.text = spannableString
    }

    fun selectDefaultTip(tipPercentage: Int){
        with(binding){
            selectedTip = tipPercentage
            when (tipPercentage) {
                Constants.TIP_10_PERCENT_SELECTED -> {
                    tipPercent10.isSelected = true
                }
                Constants.TIP_15_PERCENT_SELECTED -> {
                    tipPercent15.isSelected = true
                }
                Constants.TIP_20_PERCENT_SELECTED -> {
                    tipPercent20.isSelected = true
                }
                else -> {
                    tipPercentCustom.isSelected = true
                    setCustomTipValue(tipPercentage)
                }
            }
        }
    }

}