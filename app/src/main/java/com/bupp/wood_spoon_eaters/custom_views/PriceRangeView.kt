package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.Constants
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
    }

    override fun onClick(v: View?) {
        reset()
        when(v){
            priceRangeViewGroup1Layout ->{
                setRangeSelected(priceRangeViewGroup1Layout, true)
                rangeSelected = Constants.PRICE_GROUP_1
            }
            priceRangeViewGroup2Layout ->{
                setRangeSelected(priceRangeViewGroup2Layout, true)
                rangeSelected = Constants.PRICE_GROUP_2
            }
            priceRangeViewGroup3Layout ->{
                setRangeSelected(priceRangeViewGroup3Layout, true)
                rangeSelected = Constants.PRICE_GROUP_3
            }
            priceRangeViewGroup4Layout ->{
                setRangeSelected(priceRangeViewGroup4Layout, true)
                rangeSelected = Constants.PRICE_GROUP_4
            }

        }
            listener?.onPriceRangeClick()
    }

    fun reset() {
        setRangeSelected(priceRangeViewGroup1Layout, false)
        setRangeSelected(priceRangeViewGroup2Layout, false)
        setRangeSelected(priceRangeViewGroup3Layout, false)
        setRangeSelected(priceRangeViewGroup4Layout, false)
    }

    private fun setRangeSelected(layout: LinearLayout, isSelected: Boolean) {
        val childCount = layout.childCount
        for (i in 0 until childCount) {
            val v = layout.getChildAt(i)
            v.isSelected = isSelected
        }
    }

    fun getSelectedRange(): Int {
        return rangeSelected
    }


}