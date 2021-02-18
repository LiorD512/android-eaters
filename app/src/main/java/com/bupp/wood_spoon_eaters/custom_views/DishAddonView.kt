package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.OrderItem
import kotlinx.android.synthetic.main.dish_addon_view.view.*


class DishAddonView : FrameLayout, PlusMinusView.PlusMinusInterface {

    private var listener: DishAddonListener? = null
    fun setDishAddonListener(listener: DishAddonListener){
        this.listener = listener
    }

    interface DishAddonListener{
        fun onDishClick(position: Int){}
        fun onAddBtnClick(position: Int){}
        fun onDishCountChange(counter: Int, position: Int) {}
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.dish_addon_view, this, true)
    }

    //this view represent two types of entities: Order Items (dishes that are in the order list)
    //and cook's additional Dishes
    fun<T> setDish(dish: T, position: Int) {
        val currentDish: Dish
        var shouldExpend: Boolean = false

        when(dish){
            is OrderItem -> {
                currentDish = dish.dish
                shouldExpend = currentDish.isRecurring

                dishAddonPrice.text = dish.price.formatedValue
                dishAddonCount.text = "${dish.quantity}"
                dishAddonAddBtn.visibility = View.GONE
                dishAddonPlusMinus.visibility = View.VISIBLE
//                val quantityLeft = dish.quantity
                dish.menuItem?.let{
                    dishAddonPlusMinus.setPlusMinusListener(this, position, initialCounter = dish.quantity, quantityLeft = dish.menuItem?.getQuantityCount())
                }
            }
            else -> {
                currentDish = dish as Dish
                dishAddonAddBtn.visibility = View.VISIBLE
                dishAddonPlusMinus.visibility = View.GONE
                dishAddonPrice.text = dish.price.formatedValue
                dishAddonCount.text = "${currentDish.menuItem?.quantity ?: 0}"
                dishAddonAddBtn.setOnClickListener { listener?.onAddBtnClick(position) }
//                dishAddonAddBtn.setOnClickListener { listener?.onAddBtnClick(currentDish) }
                dishAddonImg.setOnClickListener { listener?.onDishClick(position) }
            }
        }
        Glide.with(context).load(currentDish.thumbnail).into(dishAddonImg)
        dishAddonName.text = currentDish.name

        setUiState(shouldExpend)

    }

    override fun onPlusMinusChange(counter: Int, position: Int) {
        listener?.onDishCountChange(counter, position)
    }


    fun setUiState(isExpanded: Boolean){
        if(isExpanded){
            dishAddonChooserLayout.visibility = View.VISIBLE
        }else{
            dishAddonChooserLayout.visibility = View.GONE
        }
    }


}