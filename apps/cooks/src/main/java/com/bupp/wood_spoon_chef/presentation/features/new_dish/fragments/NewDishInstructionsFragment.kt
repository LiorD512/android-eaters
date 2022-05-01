package com.bupp.wood_spoon_chef.presentation.features.new_dish.fragments

import android.os.Bundle
import android.view.View
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.FragmentNewDishInstructionsBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.new_dish.NewDishViewModel
import com.bupp.wood_spoon_chef.data.remote.model.DishRequest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewDishInstructionsFragment : BaseFragment(R.layout.fragment_new_dish_instructions) {

    var binding: FragmentNewDishInstructionsBinding? = null
    val viewModel by sharedViewModel<NewDishViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewDishInstructionsBinding.bind(view)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding!!) {
            newDishInstructionsNext.setOnClickListener {
                if (allFieldsValid()) {
                    saveCurrentStep()
                }
            }
            newDishInstructionsBack.setOnClickListener {
                activity?.onBackPressed()
            }
        }
    }

    private fun initObservers() {
        viewModel.curDishLiveData.observe(viewLifecycleOwner, {
            loadUnSavedData(it)
        })
        viewModel.saveDraftEvent.observe(viewLifecycleOwner, {
            val event = it.getContentIfNotHandled()
            event?.let{
                saveCurrentStep(true)
            }
        })
    }

    private fun saveCurrentStep(isDraft: Boolean = false) {
        with(binding!!){
            val instructions = newDishInstructionsInput.getText()
            val portionSize = newDishInstructionsPortionSize.getTextOrNull()
            viewModel.saveDishInstructions(instructions, portionSize, isDraft)
        }
    }

    private fun allFieldsValid(): Boolean {
        with(binding!!) {
//            val instructionsValid = newDishInstructionsInput.checkIfValidAndSHowError()
            return newDishInstructionsPortionSize.checkIfValidAndSHowError() //instructionsValid &&
        }
    }

    private fun loadUnSavedData(dishRequest: DishRequest?) {
        with(binding!!) {
            dishRequest?.let { it ->
                it.instruction.let {
                    newDishInstructionsInput.setText(it)
                }
                it.portionSize.let {
                    newDishInstructionsPortionSize.setText(it)
                }
            }
        }

    }

    override fun clearClassVariables() {
        binding = null
    }
}