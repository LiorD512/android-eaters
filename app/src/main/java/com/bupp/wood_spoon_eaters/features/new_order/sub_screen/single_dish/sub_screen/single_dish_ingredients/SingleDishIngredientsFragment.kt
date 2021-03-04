package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_ingredients

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import androidx.fragment.app.Fragment
import android.annotation.SuppressLint
import android.util.Log
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_eaters.model.FullDish
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.bupp.wood_spoon_eaters.common.Constants
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlinx.android.synthetic.main.fragment_single_dish_ingredients.*
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_info.SingleDishInfoFragment
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class SingleDishIngredientsFragment : Fragment(R.layout.fragment_single_dish_ingredients), DishIngredientsAdapter.DishIngredientsAdapterListener {

    private val mainViewModel by sharedViewModel<NewOrderMainViewModel>()
    private val viewModel by viewModel<SingleDishIngredientViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("dishIngredients")

        initObservers()
    }

    private fun initObservers() {
        mainViewModel.dishInfoEvent.observe(viewLifecycleOwner, Observer{
            initIngredient(it)
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initIngredient(currentDish: FullDish) {
        singleDishIngredientCalories.text = "${currentDish.calorificValue.toInt()} Calories"
        singleDishIngredientProtein.text = "${currentDish.proteins.toInt()}g Protein"
        singleDishIngredientCarbohydrate.text = "${currentDish.carbohydrates.toInt()}g Carbohydrate"

        if (currentDish.cookingMethods.size > 0) {
            singleDishIngredientSauteing.text = currentDish.cookingMethods[0].name
        }

        singleDishIngredientList.layoutManager = LinearLayoutManager(context)
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.chooser_divider)?.let { divider.setDrawable(it) }
        singleDishIngredientList.addItemDecoration(divider)
        val ingredientsAdapter = DishIngredientsAdapter(this)
        singleDishIngredientList.adapter = ingredientsAdapter

        ingredientsAdapter.submitList(currentDish.dishIngredients)

    }

    override fun onIngredientChange(ingredientsRemoved: List<Long>) {
        viewModel.updateCurrentOrderItem(ingredientsRemoved)
    }

    companion object{
        const val TAG = "wowSingleDishIngredientsFrag"
    }
}