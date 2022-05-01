package com.bupp.wood_spoon_chef.presentation.features.new_dish.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.dialogs.custom_string_chooser.CustomStringChooserBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.FragmentNewDishDietaryBinding
import com.bupp.wood_spoon_chef.presentation.features.new_dish.NewDishViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CuisineIcon
import com.bupp.wood_spoon_chef.data.remote.model.DishRequest
import com.bupp.wood_spoon_chef.data.remote.model.SelectableIcon
import com.bupp.wood_spoon_chef.presentation.views.WSEditText
import com.bupp.wood_spoon_chef.presentation.views.horizontal_dietary_view.HorizontalDietaryView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewDishDietaryFragment : BaseFragment(R.layout.fragment_new_dish_dietary), HorizontalDietaryView.HorizontalDietaryViewListener, WSEditText.WSEditTextListener, CustomStringChooserBottomSheet.CustomStringChooserListener {

    var binding: FragmentNewDishDietaryBinding? = null
    val viewModel by sharedViewModel<NewDishViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewDishDietaryBinding.bind(view)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding!!){
            newDishDietaryIcons.initHorizontalDietaryView(viewModel.getDietaryIcons(), this@NewDishDietaryFragment)

            newDishDietaryNext.setOnClickListener {
                if (allFieldsValid()) {
                    saveCurrentStep()
                }
            }
            newDishDietaryBack.setOnClickListener {
                activity?.onBackPressed()
            }

            newDishDietaryCuisine.setIsEditable(false, this@NewDishDietaryFragment)
        }
    }

    private fun saveCurrentStep(isDraft: Boolean = false) {
        with(binding!!){
            val accommodations = newDishDietaryAccommodations.getText()

            viewModel.saveDishAccommodations(accommodations, isDraft)
            Log.d("wow","wow")
        }
    }

    private fun initObservers() {
        viewModel.curDishLiveData.observe(viewLifecycleOwner, {
            loadUnSavedData(it)
        })
        viewModel.cuisineSelectData.observe(viewLifecycleOwner, { it ->
            val event = it.getContentIfNotHandled()
            event?.let{
                CustomStringChooserBottomSheet.newInstance(it.cuisineIcons, this, it.selectedCuisine).show(childFragmentManager, Constants.CUSTOM_STRING_CHOOSER_BOTTOM_SHEET)
            }
        })
        viewModel.saveDraftEvent.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let{
                saveCurrentStep(true)
            }
        })
    }

    private fun allFieldsValid(): Boolean {
        with(binding!!) {
//            val dietaryValid = newDishDietaryIcons.checkIfValidAndSHowError()
            return newDishDietaryCuisine.checkIfValidAndSHowError() //dietaryValid && cuisineValid
        }
    }

    private fun loadUnSavedData(dishRequest: DishRequest?) {
        with(binding!!) {
            dishRequest?.let { it ->
                it.cuisineIds?.let {
                    if(it.isNotEmpty()){
                        newDishDietaryCuisine.setText(viewModel.getSelectedCuisineName(it[0]))
                    }
                }
                it.dietaryIds?.let{
                    newDishDietaryIcons.setSelectedDietaryIds(it)
                }
                it.accommodations.let{
                    newDishDietaryAccommodations.setText(it)
                }
            }
        }

    }

    override fun onWSEditUnEditableClick() {
        viewModel.onSelectCuisineClick()

    }

    override fun onDietaryClick(dietary: List<SelectableIcon>) {
        with(binding!!){
            val dietary = newDishDietaryIcons.getSelectedItemsIds()
            viewModel.saveDishDietary(dietary)
        }
    }

    override fun onCuisineSelected(cuisine: CuisineIcon) {
        viewModel.setSelectedCuisine(cuisine)
    }

    override fun clearClassVariables() {
        binding = null
    }


}