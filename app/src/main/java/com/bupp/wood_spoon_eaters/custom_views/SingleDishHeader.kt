package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.action_title_view.view.*
import kotlinx.android.synthetic.main.single_dish_fragment_dialog_fragment.*
import kotlinx.android.synthetic.main.single_dish_header_view.view.*


class SingleDishHeader : FrameLayout {



    interface SingleDishHeaderListener {
        fun onBackClick()
        fun onPageClick(page: Int)
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

        singleDishHeaderInfo.setOnClickListener { scrollPageTo(Companion.INFO) }
        singleDishHeaderCook.setOnClickListener { scrollPageTo(COOK) }
        singleDishHeaderIngredient.setOnClickListener { scrollPageTo(INGREDIENT) }
        singleDishHeaderBack.setOnClickListener { listener?.onBackClick() }
        singleDishHeaderInfo.performClick()
    }

    private fun scrollPageTo(scrollPos: Int) {
        unSelectAll()
        when (scrollPos) {
            Companion.INFO -> {
                singleDishHeaderInfo.isSelected = true
                singleDishHeaderInfo.setTypeface(singleDishHeaderInfo.getTypeface(), Typeface.BOLD);
                listener?.onPageClick(Companion.INFO)
            }
            INGREDIENT -> {
                singleDishHeaderIngredient.isSelected = true
                singleDishHeaderIngredient.setTypeface(singleDishHeaderInfo.getTypeface(), Typeface.BOLD);
                listener?.onPageClick(INGREDIENT)
            }
            COOK -> {
                singleDishHeaderCook.isSelected = true
                singleDishHeaderCook.setTypeface(singleDishHeaderInfo.getTypeface(), Typeface.BOLD);
                listener?.onPageClick(COOK)
            }
        }
    }

    fun updateUi(page: Int){
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

    companion object {
        const val INFO = 0
        const val INGREDIENT = 1
        const val COOK = 2
    }


}