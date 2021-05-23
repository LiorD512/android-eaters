package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.PriceRangeViewBinding


class PriceRangeView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var binding: PriceRangeViewBinding = PriceRangeViewBinding.inflate(LayoutInflater.from(context), this, true)


    interface PriceRangeViewListener{
        fun onPriceRangeClick()
    }

    fun setPriceRangeViewListener(listener: PriceRangeViewListener){
        this.listener = listener
    }

    private var listener: PriceRangeViewListener? = null
    private var rangeSelected: Int = Constants.PRICE_NOT_SELECTED

    init {
        with(binding){
            priceRangeViewGroup1Layout.setOnClickListener(this@PriceRangeView)
            priceRangeViewGroup2Layout.setOnClickListener(this@PriceRangeView)
            priceRangeViewGroup3Layout.setOnClickListener(this@PriceRangeView)
            priceRangeViewGroup4Layout.setOnClickListener(this@PriceRangeView)
        }
    }

    override fun onClick(v: View?) {
        with(binding){
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
    }

    fun reset() {
        with(binding){
            rangeSelected = Constants.PRICE_NOT_SELECTED
            selectCurrentRange(priceRangeViewGroup1Layout, false)
            selectCurrentRange(priceRangeViewGroup2Layout, false)
            selectCurrentRange(priceRangeViewGroup3Layout, false)
            selectCurrentRange(priceRangeViewGroup4Layout, false)
        }
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
                onClick(binding.priceRangeViewGroup1Layout)
            }
            11.0 ->{
                onClick(binding.priceRangeViewGroup2Layout)
            }
            21.0 ->{
                onClick(binding.priceRangeViewGroup3Layout)
            }
            30.0 ->{
                onClick(binding.priceRangeViewGroup4Layout)
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