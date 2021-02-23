package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.tip_percent_view.view.*


class TipPercentView : FrameLayout, View.OnClickListener {

    interface TipPercentViewListener {
        fun onTipIconClick(tipSelection: Int?)
    }

    private var listener: TipPercentViewListener? = null
    private var selectedTip: Int? = null

    fun setTipPercentViewListener(listener: TipPercentViewListener) {
        this.listener = listener
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.tip_percent_view, this, true)

        initUi()
    }

    private fun initUi() {
        tipPercent10.setOnClickListener(this)
        tipPercent15.setOnClickListener(this)
        tipPercent20.setOnClickListener(this)
        tipPercentCustom.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v!!.isSelected){
            v.isSelected = false
            selectedTip = Constants.TIP_NOT_SELECTED
        }else {
            clearAll()
            v!!.isSelected = true
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

    fun clearAll() {
        tipPercent10.isSelected = false
        tipPercent15.isSelected = false
        tipPercent20.isSelected = false
        tipPercentCustom.isSelected = false
        selectedTip = null
    }

    fun setCustomTipValue(tipValue: Int? = 0){
        val string = "Custom\n$$tipValue"
        var spannableString = Utils.setCustomFontTypeSpan(context, string, 0, 6, R.font.open_sans_semi_bold)
        tipPercentCustomText.text = spannableString
    }

}