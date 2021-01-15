package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.MenuItem
import kotlinx.android.synthetic.main.quantity_view.view.*

class QuantityView : LinearLayout{

    companion object{
        const val CENTER = 0
        const val RIGHT = 1
    }


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.quantity_view, this, true)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.QuantityView)
            if (a.hasValue(R.styleable.QuantityView_views_gravity)) {
                val viewsGravity = a.getInt(R.styleable.QuantityView_views_gravity, -1)
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                when(viewsGravity){
                    CENTER -> {
                        params.apply {
                            gravity = Gravity.CENTER
                        }
                    }
                    RIGHT -> {
                        params.apply {
                            gravity = Gravity.RIGHT
                        }
                    }
                }
                quantityViewCountLayout.layoutParams = params
            }
            }
    }

    fun initQuantityView(menuItem: MenuItem){
        val quantityLeft = menuItem.quantity - menuItem.unitsSold
        if(quantityLeft <= 0){
            quantityViewQuantity.text = "Sold Out!"
        }else{
            quantityViewQuantity.text = "$quantityLeft Left"
        }


    }





}