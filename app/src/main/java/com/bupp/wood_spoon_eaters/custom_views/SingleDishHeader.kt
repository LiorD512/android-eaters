package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import kotlinx.android.synthetic.main.single_dish_header_view.view.*


class SingleDishHeader : FrameLayout {


    interface SingleDishHeaderListener {
        fun onBackClick()
        fun onPageClick(page: NewOrderMainViewModel.NewOrderScreen)
    }

    fun setSingleDishHeaderListener(listener: SingleDishHeaderListener) {
        this.listener = listener
    }

    var listener: SingleDishHeaderListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.single_dish_header_view, this, true)

        initUi()
    }

    private fun initUi() {

        singleDishHeaderInfo.setOnClickListener { scrollPageTo(NewOrderMainViewModel.NewOrderScreen.SINGLE_DISH_INFO) }
        singleDishHeaderCook.setOnClickListener { scrollPageTo(NewOrderMainViewModel.NewOrderScreen.SINGLE_DISH_COOK) }
        singleDishHeaderIngredient.setOnClickListener { scrollPageTo(NewOrderMainViewModel.NewOrderScreen.SINGLE_DISH_INGR) }
        singleDishHeaderBack.setOnClickListener { listener?.onBackClick() }
        singleDishHeaderInfo.performClick()
    }

    private fun scrollPageTo(scrollPos: NewOrderMainViewModel.NewOrderScreen) {
        unSelectAll()
        when (scrollPos) {
            NewOrderMainViewModel.NewOrderScreen.SINGLE_DISH_INFO -> {
                singleDishHeaderInfo.isSelected = true
                singleDishHeaderInfo.setTypeface(singleDishHeaderInfo.typeface, Typeface.BOLD)
                listener?.onPageClick(scrollPos)
            }
            NewOrderMainViewModel.NewOrderScreen.SINGLE_DISH_INGR -> {
                singleDishHeaderIngredient.isSelected = true
                singleDishHeaderIngredient.setTypeface(singleDishHeaderInfo.typeface, Typeface.BOLD)
                listener?.onPageClick(scrollPos)
            }
            NewOrderMainViewModel.NewOrderScreen.SINGLE_DISH_COOK -> {
                singleDishHeaderCook.isSelected = true
                singleDishHeaderCook.setTypeface(singleDishHeaderInfo.typeface, Typeface.BOLD)
                listener?.onPageClick(scrollPos)
            }
        }
    }

    fun updateUi(page: NewOrderMainViewModel.NewOrderScreen){
        scrollPageTo(page)
    }

    private fun unSelectAll() {
        singleDishHeaderInfo.isSelected = false
        singleDishHeaderCook.isSelected = false
        singleDishHeaderIngredient.isSelected = false
        singleDishHeaderInfo.setTypeface(null);
        singleDishHeaderIngredient.setTypeface(null);
        singleDishHeaderCook.setTypeface(null);

    }

//    companion object {
//        const val INFO = 0
//        const val INGREDIENT = 1
//        const val COOK = 2
//        const val CHECKOUT = 3
//    }


}