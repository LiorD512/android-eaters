//package com.bupp.wood_spoon_eaters.custom_views
//
//import android.content.Context
//import android.graphics.Typeface
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.widget.FrameLayout
//import com.bupp.wood_spoon_eaters.databinding.SingleDishHeaderViewBinding
//import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
//
//
//class SingleDishHeader @JvmOverloads
//constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
//    FrameLayout(context, attrs, defStyleAttr) {
//
//    private var binding: SingleDishHeaderViewBinding = SingleDishHeaderViewBinding.inflate(LayoutInflater.from(context), this, true)
//
//
//    interface SingleDishHeaderListener {
//        fun onBackClick()
//        fun onPageClick(page: NewOrderMainViewModel.NewOrderScreen)
//    }
//
//    fun setSingleDishHeaderListener(listener: SingleDishHeaderListener) {
//        this.listener = listener
//    }
//
//    var listener: SingleDishHeaderListener? = null
//
//    init{
//        initUi()
//    }
//
//    private fun initUi() {
//with(binding){
//            singleDishHeaderInfo.setOnClickListener { scrollPageTo(NewOrderMainViewModel.NewOrderScreen.SINGLE_DISH_INFO) }
//            singleDishHeaderCook.setOnClickListener { scrollPageTo(NewOrderMainViewModel.NewOrderScreen.SINGLE_DISH_COOK) }
//            singleDishHeaderIngredient.setOnClickListener { scrollPageTo(NewOrderMainViewModel.NewOrderScreen.SINGLE_DISH_INGR) }
//            singleDishHeaderBack.setOnClickListener { listener?.onBackClick() }
//            singleDishHeaderInfo.performClick()
//}
//    }
//
//    private fun scrollPageTo(scrollPos: NewOrderMainViewModel.NewOrderScreen) {
//        with(binding){
//            unSelectAll()
//            when (scrollPos) {
//                NewOrderMainViewModel.NewOrderScreen.SINGLE_DISH_INFO -> {
//                    singleDishHeaderInfo.isSelected = true
//                    singleDishHeaderInfo.setTypeface(singleDishHeaderInfo.typeface, Typeface.BOLD)
//                    listener?.onPageClick(scrollPos)
//                }
//                NewOrderMainViewModel.NewOrderScreen.SINGLE_DISH_INGR -> {
//                    singleDishHeaderIngredient.isSelected = true
//                    singleDishHeaderIngredient.setTypeface(singleDishHeaderInfo.typeface, Typeface.BOLD)
//                    listener?.onPageClick(scrollPos)
//                }
//                NewOrderMainViewModel.NewOrderScreen.SINGLE_DISH_COOK -> {
//                    singleDishHeaderCook.isSelected = true
//                    singleDishHeaderCook.setTypeface(singleDishHeaderInfo.typeface, Typeface.BOLD)
//                    listener?.onPageClick(scrollPos)
//                }
//                else -> {}
//            }
//        }
//    }
//
//    fun updateUi(page: NewOrderMainViewModel.NewOrderScreen){
//        scrollPageTo(page)
//    }
//
//    private fun unSelectAll() {
//        with(binding){
//            singleDishHeaderInfo.isSelected = false
//            singleDishHeaderCook.isSelected = false
//            singleDishHeaderIngredient.isSelected = false
//            singleDishHeaderInfo.setTypeface(null);
//            singleDishHeaderIngredient.setTypeface(null);
//            singleDishHeaderCook.setTypeface(null);
//        }
//
//    }
//
////    companion object {
////        const val INFO = 0
////        const val INGREDIENT = 1
////        const val COOK = 2
////        const val CHECKOUT = 3
////    }
//
//
//}