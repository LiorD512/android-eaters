//package com.bupp.wood_spoon_eaters.custom_views
//
//import android.content.Context
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.widget.LinearLayout
//import com.bupp.wood_spoon_eaters.R
//
//class DishCounterView: LinearLayout{
//
//    private var quantityLeft: Int? = 1
//    val MINUS = 0
//    val PLUS = 1
//    var count: Int = 1
//
//    lateinit var listener: DishCounterViewListener
//    interface DishCounterViewListener{
//        fun onDishCounterChanged(count: Int)
//    }
//
//    constructor(context: Context) : this(context, null)
//    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        LayoutInflater.from(context).inflate(R.layout.dish_counter_view, this, true)
//
//        initUi()
//    }
//
//    private fun initUi() {
//        dishCounterMinus.setOnClickListener { handleCount(MINUS) }
//        dishCounterPlus.setOnClickListener { handleCount(PLUS) }
//    }
//
//    fun setDishCounterViewListener(listener: DishCounterViewListener, quantityLeft: Int? = 1){
//        this.listener = listener
//        this.quantityLeft = quantityLeft
//        updateUi()
//    }
//
//    private fun handleCount(type: Int) {
//        when(type){
//            MINUS -> {
//                if(count > 0){
//                    count--
//                }
//
//            }
//            PLUS -> {
//                count++
//
//            }
//        }
//        updateUi()
//        dishCounterCount.setText(count.toString())
//        if(::listener.isInitialized){
//            listener.onDishCounterChanged(count)
//        }
//    }
//
//    private fun updateUi() {
//        if(count == 1){
//            dishCounterMinus.isEnabled = false
//            dishCounterMinus.alpha = 0.5f
//        }else{
//            dishCounterMinus.isEnabled = true
//            dishCounterMinus.alpha = 1f
//        }
//        if(count == quantityLeft){
//            dishCounterPlus.isEnabled = false
//            dishCounterPlus.alpha = 0.5f
//        }else{
//            dishCounterPlus.isEnabled = true
//            dishCounterPlus.alpha = 1f
//        }
//    }
//
//    fun getDishCount(): Int{
//        return count
//    }
//
//}