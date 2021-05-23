//package com.bupp.wood_spoon_eaters.views
//
//import android.content.Context
//import android.util.AttributeSet
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.LinearLayout
//import com.bumptech.glide.Glide
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.custom_views.PlusMinusView
//import com.bupp.wood_spoon_eaters.databinding.ChangingPictureViewBinding
//import com.bupp.wood_spoon_eaters.databinding.DishAddonViewBinding
//import com.bupp.wood_spoon_eaters.model.Dish
//import com.bupp.wood_spoon_eaters.model.OrderItem
//
//
//class DishAddonView  @JvmOverloads
//constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
//    LinearLayout(context, attrs, defStyleAttr), PlusMinusView.PlusMinusInterface {
//
//
//    private var binding: DishAddonViewBinding = DishAddonViewBinding.inflate(LayoutInflater.from(context), this, true)
//
//    private var listener: DishAddonListener? = null
//    fun setDishAddonListener(listener: DishAddonListener){
//        this.listener = listener
//    }
//
//    interface DishAddonListener{
//        fun onDishClick(position: Int){}
//        fun onAddBtnClick(position: Int){}
//        fun onDishCountChange(counter: Int, position: Int) {}
//    }
//
//    //this view represent two types of entities: Order Items (dishes that are in the order list)
//    //and cook's additional Dishes
//    fun<T> setDish(dish: T, position: Int) {
//        Log.d("wowDishAddon","setDish")
//        with(binding){
//            val currentDish: Dish
//            var shouldExpend: Boolean = false
//
//            when(dish){
//                is OrderItem -> {
//                    currentDish = dish.dish
////                    shouldExpend = currentDish.isRecurring
//
//                    dishAddonPrice.text = dish.price.formatedValue
//                    dishAddonCount.text = "${dish.quantity}"
//                    dishAddonAddBtn.visibility = View.GONE
//                    dishAddonPlusMinus.visibility = View.VISIBLE
//                    dish.menuItem?.let{
//                        dishAddonPlusMinus.setPlusMinusListener(this@DishAddonView, position, initialCounter = dish.quantity, quantityLeft = dish.menuItem?.getQuantityCount())
//                    }
//                }
//                else -> {
//                    currentDish = dish as Dish
//                    dishAddonAddBtn.visibility = View.VISIBLE
//                    dishAddonPlusMinus.visibility = View.GONE
//                    dishAddonPrice.text = dish.price.formatedValue
//                    dishAddonCount.text = "${currentDish.menuItem?.quantity ?: 0}"
//                    dishAddonAddBtn.setOnClickListener { listener?.onAddBtnClick(position) }
//                    dishAddonImg.setOnClickListener { listener?.onDishClick(position) }
//                }
//
//            }
//
//            dishAddonImg.loadResizableImage(currentDish.thumbnail)
//            dishAddonName.text = currentDish.name
//
//        }
//
//    }
//
//    override fun onPlusMinusChange(counter: Int, position: Int) {
//        listener?.onDishCountChange(counter, position)
//    }
//
//
//
//
//}