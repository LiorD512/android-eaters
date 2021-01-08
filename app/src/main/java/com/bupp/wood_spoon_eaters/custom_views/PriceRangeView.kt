package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import kotlinx.android.synthetic.main.price_range_view.view.*


class PriceRangeView : LinearLayout, View.OnClickListener {


    interface PriceRangeViewListener{
        fun onPriceRangeClick()
    }

    fun setPriceRangeViewListener(listener: PriceRangeViewListener){
        this.listener = listener
    }

    private var listener: PriceRangeViewListener? = null
    private var rangeSelected: Int = Constants.PRICE_NOT_SELECTED

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.price_range_view, this, true)

        priceRangeViewGroup1Layout.setOnClickListener(this)
        priceRangeViewGroup2Layout.setOnClickListener(this)
        priceRangeViewGroup3Layout.setOnClickListener(this)
        priceRangeViewGroup4Layout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        reset()
        when(v){
            priceRangeViewGroup1Layout ->{
                selectCurrentRange(priceRangeViewGroup1Layout, true)
                rangeSelected = Constants.PRICE_GROUP_1
            }
            priceRangeViewGroup2Layout ->{
                selectCurrentRange(priceRangeViewGroup2Layout, true)
                rangeSelected = Constants.PRICE_GROUP_2
            }
            priceRangeViewGroup3Layout ->{
                selectCurrentRange(priceRangeViewGroup3Layout, true)
                rangeSelected = Constants.PRICE_GROUP_3
            }
            priceRangeViewGroup4Layout ->{
                selectCurrentRange(priceRangeViewGroup4Layout, true)
                rangeSelected = Constants.PRICE_GROUP_4
            }

        }
            listener?.onPriceRangeClick()
    }

    fun reset() {
        rangeSelected = Constants.PRICE_NOT_SELECTED
        selectCurrentRange(priceRangeViewGroup1Layout, false)
        selectCurrentRange(priceRangeViewGroup2Layout, false)
        selectCurrentRange(priceRangeViewGroup3Layout, false)
        selectCurrentRange(priceRangeViewGroup4Layout, false)
    }

    private fun selectCurrentRange(layout: LinearLayout, isSelected: Boolean) {
        val childCount = layout.childCount
        for (i in 0 until childCount) {
            val v = layout.getChildAt(i)
            v.isSelected = isSelected
        }
    }

    fun setSelectedRange(minSelectedValue: Double){
        when(minSelectedValue){
            1.0 ->{
                onClick(priceRangeViewGroup1Layout)
            }
            11.0 ->{
                onClick(priceRangeViewGroup2Layout)
            }
            21.0 ->{
                onClick(priceRangeViewGroup3Layout)
            }
            30.0 ->{
                onClick(priceRangeViewGroup4Layout)
            }
        }
    }

    fun getSelectedRange(): Int {
        return rangeSelected
    }

    fun getMinMax(): Pair<Double?, Double?>?{
        when(getSelectedRange()){
            Constants.PRICE_GROUP_1 -> {
                return Pair(1.0, 10.0)
            }
            Constants.PRICE_GROUP_2 -> {
                return Pair(11.0, 20.0)
            }
            Constants.PRICE_GROUP_3 -> {
                return Pair(21.0, 30.0)
            }
            Constants.PRICE_GROUP_4 -> {
                return Pair(30.0, null)
            }
        }
        return null
    }


}