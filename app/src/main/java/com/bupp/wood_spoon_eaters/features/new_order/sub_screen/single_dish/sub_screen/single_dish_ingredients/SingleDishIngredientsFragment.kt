//package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_ingredients
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.view.View
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import by.kirich1409.viewbindingdelegate.viewBinding
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.databinding.FragmentSingleDishIngredientsBinding
//import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
//import com.bupp.wood_spoon_eaters.model.FullDish
//import com.segment.analytics.Analytics
//import org.koin.androidx.viewmodel.ext.android.sharedViewModel
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//class SingleDishIngredientsFragment : Fragment(R.layout.fragment_single_dish_ingredients), DishIngredientsAdapter.DishIngredientsAdapterListener {
//
//    val binding: FragmentSingleDishIngredientsBinding by viewBinding()
//    private val mainViewModel by sharedViewModel<NewOrderMainViewModel>()
//    private val viewModel by viewModel<SingleDishIngredientViewModel>()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        Analytics.with(requireContext()).screen("dishIngredients")
//
//        initObservers()
//    }
//
//    private fun initObservers() {
//        mainViewModel.dishInfoEvent.observe(viewLifecycleOwner, Observer{
//            initIngredient(it)
//        })
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun initIngredient(currentDish: FullDish) {
//        with(binding){
//            currentDish.accommodations?.let{
//                singleDishIngredientAccommodations.setBody(it)
//                singleDishIngredientAccommodations.visibility = View.VISIBLE
//            }
//            currentDish.ingredients?.let{
//                singleDishIngredientIngredients.setBody(it)
//            }
//            currentDish.instruction?.let{
//                singleDishIngredientInstructions.setBody(it)
//                singleDishIngredientInstructions.visibility = View.VISIBLE
//            }
//        }
//    }
//
//    override fun onIngredientChange(ingredientsRemoved: List<Long>) {
//        viewModel.updateCurrentOrderItem(ingredientsRemoved)
//    }
//
//    companion object{
//        const val TAG = "wowSingleDishIngredientsFrag"
//    }
//}