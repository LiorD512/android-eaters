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
import com.bupp.wood_spoon_eaters.databinding.PriceRangeViewBinding
import com.bupp.wood_spoon_eaters.databinding.QuantityViewBinding
import com.bupp.wood_spoon_eaters.model.MenuItem

class QuantityView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr){

    private var binding: QuantityViewBinding = QuantityViewBinding.inflate(LayoutInflater.from(context), this, true)

    companion object{
        const val CENTER = 0
        const val RIGHT = 1
    }


    init{
        with(binding){
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
    }

    fun initQuantityView(menuItem: MenuItem){
        with(binding){
            val quantityLeft = menuItem.quantity - menuItem.unitsSold
            if(quantityLeft <= 0){
                quantityViewQuantity.text = "Sold Out!"
            }else{
                quantityViewQuantity.text = "$quantityLeft Left"
            }
        }


    }





}