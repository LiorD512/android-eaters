package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.dish_counter_view.view.*

class DishCounterView: LinearLayout{

    val MINUS = 0
    val PLUS = 1
    var count: Int = 1

    lateinit var listener: DishCounterViewListener
    interface DishCounterViewListener{
        fun onDishCounterChanged(count: Int)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.dish_counter_view, this, true)

        initUi()
    }

    private fun initUi() {
        dishCounterMinus.setOnClickListener { handleCount(MINUS) }
        dishCounterPlus.setOnClickListener { handleCount(PLUS) }
    }

    fun setDishCounterViewListener(listener: DishCounterViewListener){
        this.listener = listener
    }

    private fun handleCount(type: Int) {
        when(type){
            MINUS -> {
                if(count > 0){
                    count--
                }
            }
            PLUS -> {
                count++
            }
        }
        dishCounterCount.setText(count.toString())
        if(::listener.isInitialized){
            listener.onDishCounterChanged(count)
        }
    }

    fun getDishCount(): Int{
        return count
    }

}