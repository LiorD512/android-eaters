package com.bupp.wood_spoon_chef.presentation.features.new_dish.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.FragmentNewDishDetailsBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.new_dish.NewDishViewModel
import com.bupp.wood_spoon_chef.presentation.features.new_dish.bottom_sheets.DishCategoriesBottomSheet
import com.bupp.wood_spoon_chef.data.remote.model.DishCategory
import com.bupp.wood_spoon_chef.data.remote.model.DishRequest
import com.bupp.wood_spoon_chef.utils.AnimationUtil
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewDishDetailsFragment : BaseFragment(R.layout.fragment_new_dish_details),
    DishCategoriesBottomSheet.DishCategoriesBottomSheetListener {

    var binding: FragmentNewDishDetailsBinding? = null
    val viewModel by sharedViewModel<NewDishViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewDishDetailsBinding.bind(view)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding!!) {
            newDishDetailsNext.setOnClickListener {
                if (allFieldsValid()) {
                    saveCurrentStep()
                }
            }
            newDishDetailsBack.setOnClickListener {
                activity?.onBackPressed()
            }
            newDishDetailsCategories.setOnClickListener {
                DishCategoriesBottomSheet(
                    viewModel.getDishCategories(),
                    this@NewDishDetailsFragment
                ).show(childFragmentManager, Constants.DISH_CATEGORIES_BOTTOM_SHEET_TAG)
            }
            viewModel.getPrepTimeList().forEachIndexed { index, prepTimeRange ->
                when (index) {
                    0 -> {
                        binding!!.newDishDetailsRadio1.text = prepTimeRange.getRangeStr()
                    }
                    1 -> {
                        binding!!.newDishDetailsRadio2.text = prepTimeRange.getRangeStr()
                    }
                    2 -> {
                        binding!!.newDishDetailsRadio3.text = prepTimeRange.getRangeStr()
                    }
                    3 -> {
                        binding!!.newDishDetailsRadio4.text = prepTimeRange.getRangeStr()
                    }
                }
            }
        }
    }

    private fun saveCurrentStep(isDraft: Boolean = false) {
        with(binding!!) {
            val ingredients = (newDishDetailsIngredients.getText() ?: "").replace("\n", " ")
                .replace("\r", " ")
            val prepTimeBtnId = newDishDetailsRadioLayout.checkedRadioButtonId
            val selectedPrepStr =
                this@NewDishDetailsFragment.view?.findViewById<RadioButton>(prepTimeBtnId)?.text

            val prepTimeId = viewModel.getPrepTimeByStr(selectedPrepStr.toString())
            viewModel.saveDishDetails(ingredients, prepTimeId, isDraft)
        }
    }


    private fun initObservers() {
        viewModel.curDishLiveData.observe(viewLifecycleOwner) {
            loadUnSavedData(it)
        }
        viewModel.saveDraftEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                saveCurrentStep(true)
            }
        }
    }

    private fun allFieldsValid(): Boolean {
        with(binding!!) {
            val validIngredients = newDishDetailsIngredients.checkIfValidAndSHowError()
            val validCategory = viewModel.isCategorySelected()
            if(!validCategory){
                AnimationUtil().shakeView(newDishDetailsCategories, requireContext())
            }
            val validPrepTimeId = newDishDetailsRadioLayout.checkedRadioButtonId != -1
            if (!validPrepTimeId) {
                AnimationUtil().shakeView(newDishDetailsRadioLayout, requireContext())
            }
            return validIngredients && validPrepTimeId && validCategory
        }
    }

    private fun loadUnSavedData(dishRequest: DishRequest?) {
        with(binding!!) {
            dishRequest?.let { it ->
                it.dishCategory?.let{
                    onCategorySelected(it)
                }
                it.ingredients?.let {
                    newDishDetailsIngredients.setText(it)
                }
                it.prepTimeRangeId?.let {
                    viewModel.getPrepTimeList().forEachIndexed { index, prepTimeRange ->
                        if (prepTimeRange.id == it) {
                            when (index) {
                                0 -> {
                                    binding!!.newDishDetailsRadio1.toggle()
                                }
                                1 -> {
                                    binding!!.newDishDetailsRadio2.toggle()
                                }
                                2 -> {
                                    binding!!.newDishDetailsRadio3.toggle()
                                }
                                3 -> {
                                    binding!!.newDishDetailsRadio4.toggle()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCategorySelected(category: DishCategory?) {
        binding?.newDishDetailsCategories?.setText(category?.name ?: "")
        viewModel.saveDishCategory(category)
    }

    override fun clearClassVariables() {
        binding = null
    }

    companion object {
        const val TAG = "wowNewDishDetails"
    }

}